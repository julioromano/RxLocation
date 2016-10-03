package net.kjulio.rxlocation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;

/**
 * Hidden (no visible UI) activity to handle GoogleApiClient connection error resolution.
 */
public class ErrorResolutionActivity extends AppCompatActivity {

    private static final String EXTRA_CONNECTION_RESULT = "extraConnectionResult";
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 3013;

    // Other objects wait on this lock while ErrorResolutionActivity shows the error resolution UI.
    static final Object errorResolutionLock = new Object();

    static boolean errorResolutionInProgress = false;

    /**
     * ALWAYS START THIS ACTIVITY USING THIS HELPER METHOD.
     *
     * Precodnitions: This method must be called only after verifyng that
     * connectionResult.hasResult() == false.
     */
    static void resolveError(Context context, ConnectionResult connectionResult) {
        if (!errorResolutionInProgress) {
            Intent intent = new Intent(context, ErrorResolutionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_CONNECTION_RESULT, connectionResult);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This if handles the case in which the user rotated the device while already showing
        // this activity leading to it be recreated.
        if (!errorResolutionInProgress) {
            errorResolutionInProgress = true;
            ConnectionResult connectionResult = getIntent().getParcelableExtra(EXTRA_CONNECTION_RESULT);
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                notifyObservers();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            notifyObservers();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        errorResolutionInProgress = false;
    }

    private void notifyObservers() {
        synchronized (errorResolutionLock) {
            errorResolutionLock.notifyAll();
        }
    }
}
