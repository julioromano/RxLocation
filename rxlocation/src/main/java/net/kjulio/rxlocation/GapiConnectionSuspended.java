package net.kjulio.rxlocation;

public class GapiConnectionSuspended extends Exception {

    private final int suspensionCause;

    GapiConnectionSuspended(int suspensionCause) {
        this.suspensionCause = suspensionCause;
    }

    public int getSuspensionCause() {
        return suspensionCause;
    }
}
