package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * RxLocation.
 *
 * An easy to use Android library to get location data in an RxJava way.
 *
 * https://github.com/julioromano/RxLocation
 */
public class RxLocation {

    private RxLocation() {}

    /**
     * Clients subscribe to get location updates via this method.
     *
     * This observable will never call onComplete() thus manual unsubscribe() is necessary.
     *
     * When using setExpirationDuration() or setNumUpdates() or setExpirationTime() the observable
     * will not terminate automatically and will just stop emitting new items without releasing any
     * resources.
     *
     * @return an Observable that returns Location items.
     */
    public static Observable<Location> locationObservable(final Context context, final LocationRequest locationRequest) {

        Scheduler scheduler = Schedulers.newThread();

        return Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        final LocationHelper locationHelper =
                                new LocationHelper(context, locationRequest, subscriber);
                        subscriber.add(Subscriptions.create(new Action0() {
                            @Override
                            public void call() {
                                locationHelper.close();
                            }
                        }));
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                // Mandatory as we use gapi blockingConnect().
                .subscribeOn(scheduler)
                // Location callbacks happen on the UI thread so we force them to happen on the same
                // scheduler as we run the subscription in for consistency. This setting can be
                // overridden by the user.
                .observeOn(scheduler);
    }

}
