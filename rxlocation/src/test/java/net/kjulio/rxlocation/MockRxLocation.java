package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import rx.Observable;

public class MockRxLocation {

    private MockRxLocation() {}

    public static Observable<Location> locationUpdates(Context context,
                                                        LocationRequest locationRequest) {
        return LocationUpdatesHelper.observable(context, new MockGoogleApiClientFactoryImpl(),
                new MockFusedLocationProviderFactoryImpl(), locationRequest);
    }

    public static Observable<Location> lastLocation(Context context) {
        return LastLocationHelper.observable(context, new MockGoogleApiClientFactoryImpl(),
                new MockFusedLocationProviderFactoryImpl());
    }
}
