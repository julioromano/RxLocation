package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.android.gms.location.LocationRequest;

import rx.Observable;
import rx.Single;

/**
 * RxLocation. An easy to use Android library to get location data in an rxJava way.
 */
public abstract class RxLocation {

    public static RxLocation newInstance(Context context) {
        return new RxLocationImpl(
                context,
                Looper.getMainLooper(),
                new LocationStorageFactory(context).getInstance()
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

    /**
     * Get current last known location from the location framework.
     *
     * @return The current location. Return 0.00/0.00 if the app doesn't have
     *          location permission or if location is not available.
     */
    public abstract @NonNull Location lastLocation();
}
