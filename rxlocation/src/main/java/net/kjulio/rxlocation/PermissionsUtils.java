package net.kjulio.rxlocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;


class PermissionsUtils {

    private PermissionsUtils() {}

    static boolean checkPermissions(Context context) {
        int coarseLocPerm = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocPerm = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION);

        return (coarseLocPerm == PackageManager.PERMISSION_GRANTED &&
                fineLocPerm == PackageManager.PERMISSION_GRANTED);
    }

}
