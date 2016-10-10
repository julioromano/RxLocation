package net.kjulio.rxlocation;

import com.google.android.gms.location.FusedLocationProviderApi;


interface FusedLocationProviderFactory {

    FusedLocationProviderApi create();
}
