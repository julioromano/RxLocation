package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.Closeable;

import rx.Subscriber;

/**
 * Object that manages a location request and its callbacks sending signals to a rx Subscriber.
 */
class LocationHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, Closeable {

    private final Context context;
    private final LocationRequest locationRequest;
    private final Subscriber<? super Location> subscriber;
    private final GoogleApiClient googleApiClient;
    private final Handler handler = new Handler(Looper.getMainLooper());

    LocationHelper(Context context, LocationRequest locationRequest, Subscriber<? super Location> subscriber) {
        this.context = context;
        this.locationRequest = locationRequest;
        this.subscriber = subscriber;
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        this.googleApiClient.blockingConnect();
        onGapiConnected();
    }

    private void onGapiConnected() {
        if (PermissionsUtils.checkPermissions(context)) {
            requestLocationUpdates();
        } else {
            // Spawn permission request activity
            PermissionActivity.requestPermissions(context);
            // wait on global globalLock. This code must not run on the UI thread or it will block
            // it and PermissionRequestActivity will also block.
            synchronized (RxLocation.permissionsRequestLock) {
                try {
                    RxLocation.permissionsRequestLock.wait();
                }catch (InterruptedException e) {
                    subscriber.onError(e);
                }
            }
            // when globalLock is released by PermissionActivity recheck permissions and go on.
            if (PermissionsUtils.checkPermissions(context)) {
                requestLocationUpdates();
            } else {
                subscriber.onError(new SecurityException("Location permission not granted."));
            }
        }
    }

    private void requestLocationUpdates() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            googleApiClient, locationRequest, LocationHelper.this);
                } catch (SecurityException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public void close() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        googleApiClient, LocationHelper.this);
                googleApiClient.disconnect();
            }
        });
    }

    /**
     * All GoogleApi callbacks (onConnected, onConnectionSuspended, onConnectionFailed,
     * onLocationChanged) are invoked from the UI thread.
     *
     * Care should be taken at what code is called from within these methods as it might block
     * the UI thread.
     */

    @Override
    public void onConnected(@Nullable Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {
        // Method's body left empty as we deem it's the best behavior.
        //
        // http://stackoverflow.com/a/27350444/972721
        // onConnectionSuspended() is called for example when a user intentionally disables or
        // uninstalls Google Play Services.
        //
        // http://stackoverflow.com/a/26147518/972721
        // After the library calls onConnectionSuspended() it will automatically try to reconnect,
        // if it fails onConnectionFailed() will be inovked otherwise normal operation will resume.
        //
        // Shall we propagate the onConnectionSuspended() as onError() or shall we just ignore it?
        // If we propagate it the subscription will be cancelled when a connection suspension
        // occurs, if we ignore it the subscription will stay and upon successful reconnection the
        // observable will go on emitting items. If the reconnection fails onConnectionFailed()
        // will be called and our subscription will be cancelled.
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        subscriber.onError(new GapiConnectionFailed(connectionResult));
    }

    @Override
    public void onLocationChanged(Location location) {
        subscriber.onNext(location);
    }
}
