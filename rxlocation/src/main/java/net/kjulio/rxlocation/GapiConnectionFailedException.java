package net.kjulio.rxlocation;

import com.google.android.gms.common.ConnectionResult;

public class GapiConnectionFailedException extends Exception {

    private final ConnectionResult connectionResult;

    public GapiConnectionFailedException(ConnectionResult connectionResult) {
        this.connectionResult = connectionResult;
    }

    @Override
    public String getMessage() {
        return connectionResult.toString();
    }
}
