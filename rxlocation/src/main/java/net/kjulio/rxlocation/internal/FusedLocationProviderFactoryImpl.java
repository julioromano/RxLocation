package net.kjulio.rxlocation.internal;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;


public class FusedLocationProviderFactoryImpl implements FusedLocationProviderFactory {

    @Override
    public FusedLocationProviderApi create() {
        return LocationServices.FusedLocationApi;
    }
}
