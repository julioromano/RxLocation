package net.kjulio.rxlocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Hidden (no visible UI) activity to handle the Android M permission request dialog.
 */
public class PermissionsActivity extends AppCompatActivity {

    private static final int LOC_REQ_CODE = 4382;
    private static final String[] LOCATION_PERMISSIONS =
            {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private static final AtomicBoolean permissionsRequestInprogress = new AtomicBoolean();

    static boolean checkPermissions(Context context) {
        int coarseLocPerm = ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocPerm = ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION);

        return (coarseLocPerm == PackageManager.PERMISSION_GRANTED &&
                fineLocPerm == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * ALWAYS START THIS ACTIVITY USING THIS HELPER METHOD.
     */
    static void requestPermissions(Context context, BaseHelper baseHelper) {

        // Register the calling BaseHelper with the global lock to notify
        // it when PermissionsRequestActivity has finished.
        PermissionsRequestLock.getInstance().addListener(baseHelper);

        if (!permissionsRequestInprogress.get()) {
            Intent intent = new Intent(context, PermissionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
        if (!permissionsRequestInprogress.getAndSet(true)) {
            if (checkPermissions(this)) {
                notifyListenersAndDie();
            } else {
                ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOC_REQ_CODE);
            }
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOC_REQ_CODE) {
            notifyListenersAndDie();
        }
    }

    private void notifyListenersAndDie() {
        PermissionsRequestLock.getInstance().notifyListeners();
        permissionsRequestInprogress.set(false);
        finish();
    }
}
