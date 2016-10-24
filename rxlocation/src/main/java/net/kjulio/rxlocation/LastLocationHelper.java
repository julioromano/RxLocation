package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderApi;

import rx.Subscriber;

class LastLocationHelper extends BaseHelper {

    private final FusedLocationProviderApi fusedLocationProviderApi;

    LastLocationHelper(Context context, GoogleApiClientFactory googleApiClientFactory,
                               FusedLocationProviderFactory fusedLocationProviderFactory,
                               Subscriber<? super Location> subscriber) {
        super(context, googleApiClientFactory, subscriber);
        this.fusedLocationProviderApi = fusedLocationProviderFactory.create();
    }

    @Override
    void onLocationPermissionsGranted() {
        Location location;
        try {
            location = fusedLocationProviderApi.getLastLocation(googleApiClient);
        } catch (SecurityException e) {
            subscriber.onError(e);
            return;
        }
        if (location != null) {
            subscriber.onNext(location);
            subscriber.onCompleted();
        } else {
            subscriber.onError(new RuntimeException("Last location is null."));
        }
    }

    @Override
    void onGooglePlayServicesDisconnecting() {}
}
