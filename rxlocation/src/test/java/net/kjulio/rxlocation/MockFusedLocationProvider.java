package net.kjulio.rxlocation;

import android.app.PendingIntent;
import android.location.Location;
import android.os.Looper;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;


class MockFusedLocationProvider implements FusedLocationProviderApi {


    @Override
    public Location getLastLocation(GoogleApiClient googleApiClient) {
        // TODO
        return null;
    }

    @Override
    public LocationAvailability getLocationAvailability(GoogleApiClient googleApiClient) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public PendingResult<Status> requestLocationUpdates(GoogleApiClient googleApiClient, LocationRequest locationRequest, LocationListener locationListener) {
        // TODO
        return null;
    }

    @Override
    public PendingResult<Status> requestLocationUpdates(GoogleApiClient googleApiClient, LocationRequest locationRequest, LocationListener locationListener, Looper looper) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public PendingResult<Status> requestLocationUpdates(GoogleApiClient googleApiClient, LocationRequest locationRequest, LocationCallback locationCallback, Looper looper) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public PendingResult<Status> requestLocationUpdates(GoogleApiClient googleApiClient, LocationRequest locationRequest, PendingIntent pendingIntent) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public PendingResult<Status> removeLocationUpdates(GoogleApiClient googleApiClient, LocationListener locationListener) {
        // TODO
        return null;
    }

    @Override
    public PendingResult<Status> removeLocationUpdates(GoogleApiClient googleApiClient, PendingIntent pendingIntent) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public PendingResult<Status> removeLocationUpdates(GoogleApiClient googleApiClient, LocationCallback locationCallback) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public PendingResult<Status> setMockMode(GoogleApiClient googleApiClient, boolean b) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public PendingResult<Status> setMockLocation(GoogleApiClient googleApiClient, Location location) {
        throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public PendingResult<Status> flushLocations(GoogleApiClient googleApiClient) {
        throw new UnsupportedOperationException("Stub!");
    }
}
