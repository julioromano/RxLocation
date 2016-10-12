package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Base class for all helpers, manages the GoogleApiClient connection.
 */
abstract class BaseHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final HandlerThread handlerThread;
    private final Handler handler;
    private final Context context;
    final Subscriber<? super Location> subscriber;
    final GoogleApiClient googleApiClient;

    BaseHelper(Context context, GoogleApiClientFactory googleApiClientFactory,
               Subscriber<? super Location> subscriber) {
        handlerThread = new HandlerThread("BaseHelperHandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        this.context = context;
        this.subscriber = subscriber;
        this.googleApiClient = googleApiClientFactory.create(context, handler, this, this);
        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                close();
            }
        }));
        handler.post(new Runnable() {
            @Override
            public void run() {
                googleApiClient.connect();
            }
        });
    }

    private void close() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onGooglePlayServicesDisconnecting();
                googleApiClient.disconnect();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            handlerThread.quitSafely();
        } else {
            handlerThread.quit();
        }
        handlerThread.interrupt();
    }

    abstract void onLocationPermissionsGranted();

    abstract void onGooglePlayServicesDisconnecting();

    void onLocationPermissionDialogDismissed() {
        // when globalLock is released by PermissionsActivity recheck permissions and go on.
        if (PermissionsActivity.checkPermissions(context)) {
            onLocationPermissionsGranted();
        } else {
            subscriber.onError(new SecurityException("Location permission not granted."));
        }
    }

    void onErrorResolutionActivityDismissed() {
        // when globalLock is released by ErrorResolutionActivity recheck permissions and go on.
        // TODO: Is it safe to call again connect() inside onConnectionFailed() ?
        // Maybe we should use a counter variable to avoid recalling googleApiClient.connect()
        // twice and instead execute subscriber.onError();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (PermissionsActivity.checkPermissions(context)) {
            onLocationPermissionsGranted();
        } else {
            // Spawn permission request activity
            PermissionsActivity.requestPermissions(context, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Method's body left empty as we deem it's the best behavior.
        //
        // http://stackoverflow.com/a/27350444/972721
        // onConnectionSuspended() is called for example when a user intentionally disables or
        // uninstalls Google Play Services.
        //
        // http://stackoverflow.com/a/26147518/972721
        // After the library calls onConnectionSuspended() it will automatically try to reconnect,
        // if it fails onConnectionFailed() will be invoked otherwise normal operation will resume.
        //
        // Shall we propagate the onConnectionSuspended() as onError() or shall we just ignore it?
        // If we propagate it the subscription will be cancelled when a connection suspension
        // occurs, if we ignore it the subscription will stay and upon successful reconnection the
        // observable will go on emitting items. If the reconnection fails onConnectionFailed()
        // will be called and our subscription will be cancelled.
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            // Spawn error resolution activity
            ErrorResolutionActivity.resolveError(context, this, connectionResult);
        }  else {
            // TODO: Show the default UI when there is no resolution.
            // https://developers.google.com/android/guides/api-client
            subscriber.onError(new GapiConnectionFailedException(connectionResult));
        }
    }
}
