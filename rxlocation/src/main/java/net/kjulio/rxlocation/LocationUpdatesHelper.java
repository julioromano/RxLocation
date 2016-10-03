package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import rx.Observable;
import rx.Subscriber;

/**
 * Manages a location request and its callbacks sending signals to a rx Subscriber.
 */
class LocationUpdatesHelper extends BaseHelper implements LocationListener {

    private final LocationRequest locationRequest;

    private LocationUpdatesHelper(Context context, Subscriber<? super Location> subscriber,
                                  LocationRequest locationRequest) {
        super(context, subscriber);
        this.locationRequest = locationRequest;
    }

    static Observable<Location> observable(final Context context,
                                           final LocationRequest locationRequest) {
        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        new LocationUpdatesHelper(context, subscriber, locationRequest);
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private PendingResult<Status> requestLocationUpdates() {
        try {
            return LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        } catch (SecurityException e) {
            subscriber.onError(e);
            return null;
        }
    }

    private PendingResult<Status> removeLocationUpdates() {
        return LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @Override
    void onLocationPermissionsGranted(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    @Override
    void onGooglePlayServicesDisconnecting() {
        removeLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        subscriber.onNext(location);
    }
}
