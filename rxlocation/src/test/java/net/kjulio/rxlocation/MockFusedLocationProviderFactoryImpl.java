package net.kjulio.rxlocation;

import com.google.android.gms.location.FusedLocationProviderApi;


class MockFusedLocationProviderFactoryImpl implements FusedLocationProviderFactory {

    @Override
    public FusedLocationProviderApi create() {
        return new MockFusedLocationProvider();
    }
}
