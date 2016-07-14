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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import timber.log.Timber;


/**
 * Helper class that wraps Android's locations services.
 * Exposes an observable that emits the location updates.
 * It will only trigger Android Location service when there is at least
 * one subscriber.
 *
 * It does not handle andorid M permissions requests. It merely catches a SecurityException
 * in case location permission has not been granted.
 */
class RxLocationImpl extends RxLocation {

    private static final int TIMEOUT_SECONDS = 5;

    private final AtomicInteger activeRequestsNumber = new AtomicInteger();
    private final Handler handler;
    private final LocationStorage locationStorage;
    private final GoogleApiClient googleApiClient;
    private final LocationRequest defaultLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private Location lastKnownLocation;

    RxLocationImpl(Context context, Looper mainLooper, LocationStorage locationStorage) {
        handler = new Handler(mainLooper);
        GoogleApiClientConnectionListener googleApiClientConnectionListener =
                new GoogleApiClientConnectionListener();
        this.googleApiClient = new GoogleApiClient.Builder(context.getApplicationContext())
                .addConnectionCallbacks(googleApiClientConnectionListener)
                .addOnConnectionFailedListener(googleApiClientConnectionListener)
                .addApi(LocationServices.API)
                .build();
        this.locationStorage = locationStorage;
        Location location = locationStorage.getLastLoc(); // restores state from db, if there's no state set a fake location.
        if (location != null) {
            lastKnownLocation = location;
        } else {
            lastKnownLocation = getFakeLocation();
        }
    }

    @Override
    public Observable<Location> locationObservable() {
        return locationObservable(defaultLocationRequest);
    }

    @Override
    public Observable<Location> locationObservable(final LocationRequest locationRequest) {

        final Scheduler scheduler = Schedulers.io();
        final RxLocationListener[] rxLocationListener = new RxLocationListener[1];

        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(final Subscriber<? super Location> subscriber) {
                ConnectionResult connectionResult =
                        googleApiClient.blockingConnect(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (connectionResult.isSuccess()) {
                    activeRequestsNumber.incrementAndGet();
                    rxLocationListener[0] = new RxLocationListener(subscriber, scheduler);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                requestLocationUpdates(
                                        googleApiClient, locationRequest, rxLocationListener[0]);
                            } catch (SecurityException e) {
                                subscriber.onError(e);
                            }
                        }
                    });
                } else {
                    subscriber.onError(new IllegalStateException(String.format(Locale.getDefault(),
                            "Unable to connect to Play Services: %s", connectionResult.getErrorMessage())));
                }
            }
        }).doOnUnsubscribe(new Action0() {
            @Override
            public void call() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        removeLocationUpdates(googleApiClient, rxLocationListener[0]);

                        // Remove the reference to the RxLocationListener instance to ensure it can
                        // be garbage collected.
                        rxLocationListener[0] = null;

                        if(activeRequestsNumber.decrementAndGet()==0) {
                            googleApiClient.disconnect();
                        }
                    }
                });
            }
        }).subscribeOn(scheduler).timeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public Single<Location> locationSingle() {
        return locationObservable().first().toSingle();
    }

    @Override
    public Single<Location> locationSingle(LocationRequest locationRequest) {
        return locationObservable(locationRequest).first().toSingle();
    }

    @Override
    @NonNull
    public Location lastLocation() {
        if (lastKnownLocation != null) {
            return lastKnownLocation;
        } else {
            return getFakeLocation();
        }
    }

    private Location getFakeLocation() {
        Location location = new Location("FakeLocationProvider");
        location.setLatitude(0.0d);
        location.setLongitude(0.0d);
        return location;
    }

    private PendingResult<Status> requestLocationUpdates(GoogleApiClient googleApiClient,
                                                         LocationRequest locationRequest,
                                                         LocationListener locationListener)
            throws SecurityException {
            return LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    locationRequest,
                    locationListener
            );
    }

    private PendingResult<Status> removeLocationUpdates(GoogleApiClient googleApiClient,
                                                        LocationListener locationListener) {
        return LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,
                locationListener);
    }

    private class RxLocationListener implements LocationListener {

        private final Subscriber<? super Location> subscriber;
        private final Scheduler.Worker worker;

        private RxLocationListener(Subscriber<? super Location> subscriber, Scheduler scheduler) {
            this.subscriber = subscriber;
            this.worker = scheduler.createWorker();
        }

        @Override
        public void onLocationChanged(final Location location) {
            worker.schedule(new Action0() {
                @Override
                public void call() {
                    sendLocation(location);
                }
            });
        }

        private void sendLocation(Location location) {
            if (location == null) {
                subscriber.onError(new NullPointerException("Null location"));
            } else {
                if (!subscriber.isUnsubscribed()) {
                    lastKnownLocation = location;
                    locationStorage.putLastLoc(lastKnownLocation);
                    subscriber.onNext(location);
                }
            }
        }

    }

    private static class GoogleApiClientConnectionListener implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener {

        private GoogleApiClientConnectionListener() {}

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Timber.d("onConnected: %s", bundle);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Timber.d("onConnectionSuspended: %d", i);
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Timber.e("onConnectionFailed: %s.", connectionResult);
        }

    }

}
