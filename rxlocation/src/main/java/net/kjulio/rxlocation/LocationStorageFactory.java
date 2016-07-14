package net.kjulio.rxlocation;

import android.content.Context;

import com.google.gson.Gson;

class LocationStorageFactory {

    private static LocationStorage locationStorage;

    private final Context context;

    LocationStorageFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    synchronized LocationStorage getInstance() {
        if (locationStorage == null) {
            locationStorage = new LocationStorage(context, new Gson());
        }
        return locationStorage;
    }

}
