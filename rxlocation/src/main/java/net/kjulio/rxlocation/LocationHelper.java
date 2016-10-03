package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import rx.Subscriber;

/**
 * Object that manages a location request and its callbacks sending signals to a rx Subscriber.
 */
class LocationHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final Context context;
    private final Subscriber<? super Location> subscriber;
    private final HandlerThread handlerThread;
    private final Handler handler;
    private final GoogleApiClient googleApiClient;
    private final LocationRequest locationRequest;

    /**
     * This constructor must not be called from the UI thread because it uses
     * googleApiClient.blockingConnect() blocking function.
     */
    LocationHelper(Context context, LocationRequest locationRequest, Subscriber<? super Location> subscriber) {
        this.context = context;
        this.subscriber = subscriber;
        this.handlerThread = new HandlerThread("MyHandlerThread");
        this.handlerThread.start();
        this.handler = new Handler(handlerThread.getLooper());
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .setHandler(handler)
                .build();
        this.locationRequest = locationRequest;
    }

    void start() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                googleApiClient.connect();
            }
        });
    }

    void stop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                removeLocationUpdates();
                googleApiClient.disconnect();
            }
        });
        handlerThread.quit();
        handlerThread.interrupt();
    }

    private PendingResult<Status> requestLocationUpdates() {
        try {
            return LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, LocationHelper.this);
        } catch (SecurityException e) {
            subscriber.onError(e);
            return null;
        }
    }

    private PendingResult<Status> removeLocationUpdates() {
        return LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, LocationHelper.this);
    }

    /**
     * All GoogleApi callbacks (onConnected, onConnectionSuspended, onConnectionFailed,
     * onLocationChanged) are invoked from the UI thread.
     *
     * Care should be taken at what code is called from within these methods as it might block
     * the UI thread.
     */

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (PermissionsActivity.checkPermissions(context)) {
            requestLocationUpdates();
        } else {
            // Spawn permission request activity
            PermissionsActivity.requestPermissions(context);
            // wait on global globalLock. This code must not run on the UI thread or it will block
            // it and PermissionRequestActivity will also block.
            synchronized (PermissionsActivity.permissionsRequestLock) {
                try {
                    PermissionsActivity.permissionsRequestLock.wait();
                } catch (InterruptedException e) {
                    subscriber.onError(e);
                }
            }
            // when globalLock is released by PermissionsActivity recheck permissions and go on.
            if (PermissionsActivity.checkPermissions(context)) {
                requestLocationUpdates();
            } else {
                subscriber.onError(new SecurityException("Location permission not granted."));
            }
        }
    }

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
        // if it fails onConnectionFailed() will be invoked otherwise normal operation will resume.
        //
        // Shall we propagate the onConnectionSuspended() as onError() or shall we just ignore it?
        // If we propagate it the subscription will be cancelled when a connection suspension
        // occurs, if we ignore it the subscription will stay and upon successful reconnection the
        // observable will go on emitting items. If the reconnection fails onConnectionFailed()
        // will be called and our subscription will be cancelled.
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        subscriber.onError(new GapiConnectionFailedException(connectionResult));
    }

    @Override
    public void onLocationChanged(Location location) {
        subscriber.onNext(location);
    }
}
