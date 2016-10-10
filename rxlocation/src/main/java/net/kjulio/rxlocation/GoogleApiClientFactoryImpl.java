package net.kjulio.rxlocation;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


class GoogleApiClientFactoryImpl implements GoogleApiClientFactory {

    @Override
    public GoogleApiClient create(Context context, Handler handler,
                                  GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                                  GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {

            return new GoogleApiClient.Builder(context)
                .setHandler(handler)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();
    }
}
