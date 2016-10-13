package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import rx.Observable;

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
 * Observable<Location> o = RxLocation.lastLocation(context);
 *
 * The non-static methods require an RxLocation instance instead:
 * RxLocation rxLocation = new RxLocation(context);
 * Observable<Location> o = rxLocation.lastLocation();
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
    public Observable<Location> lastLocation() {
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
    public static Observable<Location> locationUpdates(Context context,
                                                          LocationRequest locationRequest) {
        return LocationUpdatesHelper.observable(context, new GoogleApiClientFactoryImpl(),
                new FusedLocationProviderFactoryImpl(), locationRequest);
    }

    /**
     * Yields the last location available to the system.
     *
     * This observable will emit only one element and then call onComplete.
     *
     * @return an Observable that returns one Location item.
     */
    public static Observable<Location> lastLocation(Context context) {
        return LastLocationHelper.observable(context, new GoogleApiClientFactoryImpl(),
                new FusedLocationProviderFactoryImpl());
    }

}
