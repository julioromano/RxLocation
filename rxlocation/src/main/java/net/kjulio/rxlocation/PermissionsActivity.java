package net.kjulio.rxlocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;


/**
 * Hidden (no visible UI) activity to handle the Android M permission request dialog.
 */
public class PermissionsActivity extends AppCompatActivity {

    private final static int LOC_REQ_CODE = 4382;
    private final static String[] LOCATION_PERMISSIONS =
            {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    // Other objects wait on this lock while PermissionsActivity shows the permissions
    // request dialog.
    static final Object permissionsRequestLock = new Object();

    static boolean permissionsRequestInprogress = false;

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
    static void requestPermissions(Context context) {
        if (!permissionsRequestInprogress) {
            Intent intent = new Intent(context, PermissionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This if handles the case in which the user rotated the device while already showing
        // this activity leading to it be recreated.
        if (!permissionsRequestInprogress) {
            permissionsRequestInprogress = true;
            if (checkPermissions(this)) {
                notifyObservers();
                finish();
            } else {
                ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOC_REQ_CODE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        permissionsRequestInprogress = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOC_REQ_CODE) {
            notifyObservers();
            finish();
        }
    }

    private void notifyObservers() {
        synchronized (permissionsRequestLock) {
            permissionsRequestLock.notifyAll();
        }
    }
}
