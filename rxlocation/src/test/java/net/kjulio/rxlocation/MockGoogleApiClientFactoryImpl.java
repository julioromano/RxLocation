package net.kjulio.rxlocation;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.common.api.GoogleApiClient;

import net.kjulio.rxlocation.internal.GoogleApiClientFactory;


class MockGoogleApiClientFactoryImpl implements GoogleApiClientFactory {

    MockGoogleApiClientFactoryImpl() {}

    @Override
    public GoogleApiClient create(Context context, Handler handler,
                                  GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                                  GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        return new MockGoogleApiClient(context, handler, connectionCallbacks,
                onConnectionFailedListener, false);
    }
}
