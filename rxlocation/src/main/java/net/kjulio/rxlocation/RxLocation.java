package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * RxLocation.
 *
 * An easy to use Android library to get location data in an RxJava way.
 *
 * https://github.com/julioromano/RxLocation
 */
public class RxLocation {

    static final Object permissionsRequestLock = new Object();

    private RxLocation() {}

    /**
     * Clients subscribe to the location helper via this method.
     * By default the issued observable times out after 5 seconds to prevent indefinite waits
     * when the location data takes too long to arrive (e.g. when GPS only location is enabled on
     * the device and the device is indoors).
     *
     * @return an Observable that returns Location items.
     */
    public static Observable<Location> locationObservable(final Context context, final LocationRequest locationRequest) {

        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        final LocationHelper locationHelper = new LocationHelper(context, locationRequest);
                        locationHelper.start(subscriber);

                        subscriber.add(Subscriptions.create(new Action0() {
                            @Override
                            public void call() {
                                locationHelper.stop();
                            }
                        }));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * Gets a single Location value.
     *
     * @return
     */
    public static Single<Location> locationSingle(final Context context, final LocationRequest locationRequest) {
        return locationObservable(context, locationRequest).first().toSingle();
    }
}
