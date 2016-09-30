package net.kjulio.rxlocation;

import com.google.android.gms.common.ConnectionResult;

class GapiConnectionFailedException extends Exception {

    private final ConnectionResult connectionResult;

    GapiConnectionFailedException(ConnectionResult connectionResult) {
        this.connectionResult = connectionResult;
    }

    @Override
    public String getMessage() {
        return connectionResult.toString();
    }
}
