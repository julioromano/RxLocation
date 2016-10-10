package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import rx.Observable;

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
    public static Observable<Location> locationUpdates(Context context,
                                                          LocationRequest locationRequest) {
        return LocationUpdatesHelper.observable(context, new GoogleApiClientFactoryImpl(),
                new FusedLocationProviderFactoryImpl(), locationRequest);
    }

    public static Observable<Location> lastLocation(Context context) {
        return LastLocationHelper.observable(context, new GoogleApiClientFactoryImpl(),
                new FusedLocationProviderFactoryImpl());
    }

}
