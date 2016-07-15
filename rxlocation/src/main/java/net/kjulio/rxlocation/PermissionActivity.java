package net.kjulio.rxlocation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

/**
 * Hidden (no visible UI) activity to handle the Android M permission request dialog.
 */
public class PermissionActivity extends AppCompatActivity {

    private final static int LOC_REQ_CODE = 4382;
    private final static String[] LOCATION_PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    static void requestPermissions(Context context) {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("PermissionActivity onCreate().");

        if (RxLocationImpl.checkPermissions(this)) {
            Timber.d("Location permissions already granted.");
            finish();
        } else {
            Timber.d("Location permissions not granted: showing request dialog.");
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOC_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOC_REQ_CODE && grantResults.length == 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Timber.d("Location permissions granted.");
        } else {
            Timber.d("Location permissions not granted.");
        }
        synchronized (RxLocationImpl.globalLock) {
            RxLocationImpl.globalLock.notifyAll();
        }
        finish();
    }
}
