package net.kjulio.rxlocation.internal;

import com.google.android.gms.location.FusedLocationProviderApi;


interface FusedLocationProviderFactory {

    FusedLocationProviderApi create();
}
