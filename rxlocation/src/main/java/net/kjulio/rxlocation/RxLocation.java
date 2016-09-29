package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.LocationRequest;

import rx.Observable;
import rx.Single;

/**
 * RxLocation.
 *
 * An easy to use Android library to get location data in an RxJava way.
 *
 * https://github.com/julioromano/RxLocation
 */
public abstract class RxLocation {

    /**
     * Returns a new instance of the RxLocation library.
     *
     * @param context the current application or activity context.
     * @return a new instance of RxLocation
     */
    public static RxLocation newInstance(Context context) {
        return new RxLocationImpl(
                context,
                Looper.getMainLooper()
        );
    }

    /**
     * Clients subscribe to the location helper via this method.
     * By default the issued observable times out after 5 seconds to prevent indefinite waits
     * when the location data takes too long to arrive (e.g. when GPS only location is enabled on
     * the device and the device is indoors).
     *
     * @return an Observable that returns Location items.
     */
    public abstract Observable<Location> locationObservable();

    public abstract Observable<Location> locationObservable(LocationRequest locationRequest);

    /**
     * Gets a single Location value.
     *
     * @return
     */
    public abstract Single<Location> locationSingle();

    public abstract Single<Location> locationSingle(LocationRequest locationRequest);
}
