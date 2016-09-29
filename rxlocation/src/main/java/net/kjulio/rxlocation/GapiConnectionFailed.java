package net.kjulio.rxlocation;

import com.google.android.gms.common.ConnectionResult;

public class GapiConnectionFailed extends Exception {

    private final ConnectionResult connectionResult;

    GapiConnectionFailed(ConnectionResult connectionResult) {
        this.connectionResult = connectionResult;
    }

    public ConnectionResult getConnectionResult() {
        return connectionResult;
    }
}
