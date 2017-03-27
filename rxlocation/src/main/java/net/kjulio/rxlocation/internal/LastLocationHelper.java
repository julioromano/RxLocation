package net.kjulio.rxlocation.internal;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderApi;

import io.reactivex.ObservableEmitter;

public class LastLocationHelper extends BaseHelper {

    private final FusedLocationProviderApi fusedLocationProviderApi;

    public LastLocationHelper(Context context, GoogleApiClientFactory googleApiClientFactory,
                       FusedLocationProviderFactory fusedLocationProviderFactory,
                       ObservableEmitter<Location> emitter) {
        super(context, googleApiClientFactory, emitter);
        this.fusedLocationProviderApi = fusedLocationProviderFactory.create();
    }

    @Override
    void onLocationPermissionsGranted() {
        Location location;
        try {
            location = fusedLocationProviderApi.getLastLocation(googleApiClient);
        } catch (SecurityException e) {
            emitter.onError(e);
            return;
        }
        if (location != null) {
            emitter.onNext(location);
            emitter.onComplete();
        } else {
            emitter.onError(new RuntimeException("Last location is null."));
        }
    }

    @Override
    void onGooglePlayServicesDisconnecting() {}
}
