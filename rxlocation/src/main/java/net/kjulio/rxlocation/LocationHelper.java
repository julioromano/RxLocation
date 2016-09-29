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

import rx.Subscriber;

/**
 * Object that manages a location request and its callbacks sending signals to a rx Subscriber.
 */
public class LocationHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final Context context;
    private final LocationRequest locationRequest;
    private final GoogleApiClient googleApiClient;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private Subscriber<? super Location> subscriber = null;

    LocationHelper(Context context, LocationRequest locationRequest) {
        this.context = context;
        this.locationRequest = locationRequest;
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    void start(Subscriber<? super Location> subscriber) {
        if (subscriber == null) {
            googleApiClient.connect();
        } else {
            throw new RuntimeException("Already started.");
        }
    }

    void stop() {
        if (subscriber != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    LocationServices.FusedLocationApi.removeLocationUpdates(
                            googleApiClient, LocationHelper.this);
                    googleApiClient.disconnect();
                    subscriber = null;
                }
            });
        } else {
            throw new RuntimeException("Already stopped.");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (PermissionsUtils.checkPermissions(context)) {
            requestLocationUpdates();
        } else {
            // Spawn permission request activity
            PermissionActivity.requestPermissions(context);
            // wait on global globalLock
            synchronized (RxLocation.permissionsRequestLock) {
                try {
                    RxLocation.permissionsRequestLock.wait();
                }catch (InterruptedException e) {
                    subscriber.onError(e);
                }
            }
            // when globalLock released  recheck for error or go on
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
    public void onConnectionSuspended(int i) {
        // TODO: Consider propagating the connection suspension with onError().
        // Shall we propagate the suspension as onError or shall we just ignore it?
        // If we propagate it the subscription will be cancelled when a connection suspension
        // occurs, if we ignore it the subscription will stay and upon reconnection the observable
        // will go on emitting items.
        // For the moment we ignore it and defer further discussion about this topic.
        //
        // subscriber.onError(new GapiConnectionSuspended(i));
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
