package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderApi;

import rx.Observable;
import rx.Subscriber;

class LastLocationHelper extends BaseHelper {

    private final FusedLocationProviderApi fusedLocationProviderApi;

    private LastLocationHelper(Context context, GoogleApiClientFactory googleApiClientFactory,
                               FusedLocationProviderFactory fusedLocationProviderFactory,
                               Subscriber<? super Location> subscriber) {
        super(context, googleApiClientFactory, subscriber);
        this.fusedLocationProviderApi = fusedLocationProviderFactory.create();
    }

    static Observable<Location> observable(final Context context, final GoogleApiClientFactory googleApiClientFactory,
                                           final FusedLocationProviderFactory fusedLocationProviderFactory) {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        new LastLocationHelper(context, googleApiClientFactory,
                                fusedLocationProviderFactory, subscriber);
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private void getLastLocation() {
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
    void onLocationPermissionsGranted(@Nullable Bundle bundle) {
        getLastLocation();
    }

    @Override
    void onGooglePlayServicesDisconnecting() {}
}
