package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.functions.Cancellable;

/**
 * RxLocation.
 *
 * An easy to use Android library to get location data in an RxJava way.
 * https://github.com/julioromano/RxLocation
 *
 *
 * All public methods of the library have a static and non-static version.
 *
 * The static methods can be invoked straight away:
 * {@code
 *  Observable<Location> o = RxLocation.lastLocation(context);
 * }
 *
 * The non-static methods require an RxLocation instance instead:
 * {@code
 *  RxLocation rxLocation = new RxLocation(context);
 *  Observable<Location> o = rxLocation.lastLocation();
 * }
 *
 * The library is stateless: there is no difference in using the static vs non-static version.
 */
public class RxLocation {

    private final Context context;

    public RxLocation(Context context) {
        this.context = context;
    }

    /**
     * Yields periodical location updates.
     *
     * This observable will never call onComplete() thus manual unsubscribe() is necessary.
     *
     * When using setExpirationDuration() or setNumUpdates() or setExpirationTime() the observable
     * will not terminate automatically and will just stop emitting new items without releasing any
     * resources.
     *
     * @return an Observable that returns Location items.
     */
    public Observable<Location> locationUpdates(LocationRequest locationRequest) {
        return RxLocation.locationUpdates(context, locationRequest);
    }

    /**
     * Yields the last location available to the system.
     *
     * This observable will emit only one element and then call onComplete.
     *
     * @return an Observable that returns one Location item.
     */
    public Single<Location> lastLocation() {
        return RxLocation.lastLocation(context);
    }

    /**
     * Yields periodical location updates.
     *
     * This observable will never call onComplete() thus manual unsubscribe() is necessary.
     *
     * When using setExpirationDuration() or setNumUpdates() or setExpirationTime() the observable
     * will not terminate automatically and will just stop emitting new items without releasing any
     * resources.
     *
     * @return an Observable that returns Location items.
     */
    @SuppressWarnings("WeakerAccess") // It's an entry point.
    public static Observable<Location> locationUpdates(final Context context,
                                                   final LocationRequest locationRequest) {
        return Observable.create(new ObservableOnSubscribe<Location>() {
            @Override
            public void subscribe(ObservableEmitter<Location> e) throws Exception {

                final LocationUpdatesHelper locationUpdatesHelper = new LocationUpdatesHelper(
                        context, new GoogleApiClientFactoryImpl(),
                        new FusedLocationProviderFactoryImpl(), e, locationRequest);

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        locationUpdatesHelper.stop();
                    }
                });

                locationUpdatesHelper.start();
            }
        });
    }

    /**
     * Yields the last location available to the system.
     *
     * This observable will emit only one element and then call onComplete.
     *
     * @return an Observable that returns one Location item.
     */
    @SuppressWarnings("WeakerAccess") // It's an entry point.
    public static Single<Location> lastLocation(final Context context) {
        return Observable.create(new ObservableOnSubscribe<Location>() {
            @Override
            public void subscribe(ObservableEmitter<Location> e) throws Exception {

                final LastLocationHelper lastLocationHelper = new LastLocationHelper(
                        context, new GoogleApiClientFactoryImpl(),
                        new FusedLocationProviderFactoryImpl(), e);

                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        lastLocationHelper.stop();
                    }
                });

                lastLocationHelper.start();
            }
        }).singleOrError();
    }
}
