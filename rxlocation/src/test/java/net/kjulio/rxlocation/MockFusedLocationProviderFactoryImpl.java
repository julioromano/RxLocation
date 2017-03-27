package net.kjulio.rxlocation;

import com.google.android.gms.location.FusedLocationProviderApi;

import net.kjulio.rxlocation.internal.FusedLocationProviderFactory;


class MockFusedLocationProviderFactoryImpl implements FusedLocationProviderFactory {

    @Override
    public FusedLocationProviderApi create() {
        return new MockFusedLocationProvider();
    }
}
