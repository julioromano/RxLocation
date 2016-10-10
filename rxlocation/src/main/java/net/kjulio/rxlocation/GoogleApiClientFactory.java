package net.kjulio.rxlocation;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.common.api.GoogleApiClient;


interface GoogleApiClientFactory {

    GoogleApiClient create(Context context, Handler handler,
                           GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                           GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener);
}
