package net.kjulio.rxlocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

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
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
            subscriber = null;
        } else {
            throw new RuntimeException("Already stopped.");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
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
