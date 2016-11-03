package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import io.reactivex.ObservableEmitter;

/**
 * Manages a location request and its callbacks sending signals to a rx Subscriber.
 */
class LocationUpdatesHelper extends BaseHelper implements LocationListener {

    private final FusedLocationProviderApi fusedLocationProviderApi;
    private final LocationRequest locationRequest;

    LocationUpdatesHelper(Context context, GoogleApiClientFactory googleApiClientFactory,
                          FusedLocationProviderFactory fusedLocationProviderFactory,
                          ObservableEmitter<Location> emitter, LocationRequest locationRequest) {
        super(context, googleApiClientFactory, emitter);
        this.fusedLocationProviderApi = fusedLocationProviderFactory.create();
        this.locationRequest = locationRequest;
    }

    @Override
    void onLocationPermissionsGranted() {
        try {
            fusedLocationProviderApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this, handler.getLooper());
        } catch (SecurityException e) {
            emitter.onError(e);
        }
    }

    @Override
    void onGooglePlayServicesDisconnecting() {
        fusedLocationProviderApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        emitter.onNext(location);
    }
}
