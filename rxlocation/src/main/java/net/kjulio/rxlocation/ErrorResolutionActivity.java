package net.kjulio.rxlocation;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hidden (no visible UI) activity to handle GoogleApiClient connection error resolution.
 */
public class ErrorResolutionActivity extends AppCompatActivity {

    private static final String EXTRA_CONNECTION_RESULT = "extraConnectionResult";
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 3013;

    private static final AtomicBoolean errorResolutionInProgress = new AtomicBoolean();

    /**
     * ALWAYS START THIS ACTIVITY USING THIS HELPER METHOD.
     *
     * Precodnitions: This method must be called only after verifyng that
     * connectionResult.hasResult() == false.
     */
    static void resolveError(Context context, BaseHelper baseHelper,
                             ConnectionResult connectionResult) {

        // Register the calling BaseHelper with the global lock to notify
        // it when ErrorResolutionActivity has finished.
        ErrorResolutionLock.getInstance().addListener(baseHelper);

        if (!errorResolutionInProgress.get()) {
            Intent intent = new Intent(context, ErrorResolutionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_CONNECTION_RESULT, connectionResult);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activity recreation due to configChanges has been disabled in the manifest, no need
        // to handle it here.

        // Handles the case in which 2 concurrent invocation of requestPermissions() launched
        // this activity.
        if (!errorResolutionInProgress.getAndSet(true)) {
            ConnectionResult connectionResult = getIntent().getParcelableExtra(EXTRA_CONNECTION_RESULT);
            try {
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                notifyListenersAndDie();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            notifyListenersAndDie();
        }
    }

    private void notifyListenersAndDie() {
        ErrorResolutionLock.getInstance().notifyListeners();
        errorResolutionInProgress.set(false);
        finish();
    }
}
