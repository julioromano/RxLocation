package net.kjulio.rxlocation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;


class MockGoogleApiClient extends GoogleApiClient {

    private final Context context;
    private final Handler handler;
    private final GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    private final GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener;
    private final boolean testConnectionFailure;

    MockGoogleApiClient(Context context, Handler handler, ConnectionCallbacks connectionCallbacks,
                        OnConnectionFailedListener onConnectionFailedListener, boolean testConnectionFailure) {
        this.context = context;
        this.handler = handler;
        this.connectionCallbacks = connectionCallbacks;
        this.onConnectionFailedListener = onConnectionFailedListener;
        this.testConnectionFailure = testConnectionFailure;
    }

    /**
     * GoogleApiClient Methods
     */

    @Override
    public boolean hasConnectedApi(@NonNull Api<?> api) {
        throw new UnsupportedOperationException("Stub!");
    }

    @NonNull
    @Override
    public ConnectionResult getConnectionResult(@NonNull Api<?> api) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void connect() {
        if (testConnectionFailure) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onConnectionFailedListener.onConnectionFailed(new ConnectionResult(0));
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectionCallbacks.onConnected(new Bundle());
                }
            });
        }
    }

    @Override
    public ConnectionResult blockingConnect() {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public ConnectionResult blockingConnect(long l, @NonNull TimeUnit timeUnit) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void disconnect() {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void reconnect() {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public PendingResult<Status> clearDefaultAccountAndReconnect() {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void stopAutoManage(@NonNull FragmentActivity fragmentActivity) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public boolean isConnected() {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public boolean isConnecting() {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void registerConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public boolean isConnectionCallbacksRegistered(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void unregisterConnectionCallbacks(@NonNull ConnectionCallbacks connectionCallbacks) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void registerConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public boolean isConnectionFailedListenerRegistered(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void unregisterConnectionFailedListener(@NonNull OnConnectionFailedListener onConnectionFailedListener) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void dump(String s, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strings) {
        throw new UnsupportedOperationException("Stub!");
    }
}
