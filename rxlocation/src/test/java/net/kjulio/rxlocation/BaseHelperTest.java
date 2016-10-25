package net.kjulio.rxlocation;

import android.Manifest;
import android.content.Context;
import android.location.Location;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import rx.Subscriber;
import rx.observers.TestSubscriber;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BaseHelperTest {

    private Context context;
    private MockGoogleApiClientFactoryImpl factory;
    private TestSubscriber<Location> testSubscriber;
    private ShadowApplication application;

    private MyBaseHelper baseHelper;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
        factory = new MockGoogleApiClientFactoryImpl();
        testSubscriber = TestSubscriber.create();
        application = Shadows.shadowOf(RuntimeEnvironment.application);

        baseHelper = new MyBaseHelper(context, factory, testSubscriber);
    }

    @Test
    public void start() throws Exception {
        // TODO: What to test here?
    }

    @Test
    public void stop() throws Exception {
        // TODO: What to test here?
    }

    @Test
    public void onLocationPermissionsGranted() throws Exception {
        baseHelper.onLocationPermissionsGranted();
        Assert.assertEquals(1, baseHelper.onLocationPermissionsGrantedCount[0]);
        Assert.assertEquals(0, baseHelper.onGooglePlayServicesDisconnectingCount[0]);
    }

    @Test
    public void onGooglePlayServicesDisconnecting() throws Exception {
        baseHelper.onGooglePlayServicesDisconnecting();
        Assert.assertEquals(0, baseHelper.onLocationPermissionsGrantedCount[0]);
        Assert.assertEquals(1, baseHelper.onGooglePlayServicesDisconnectingCount[0]);
    }

    @Test
    public void onLocationPermissionDialogDismissedWithPermission() throws Exception {
        application.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);

        baseHelper.onLocationPermissionDialogDismissed();
        Assert.assertEquals(1, baseHelper.onLocationPermissionsGrantedCount[0]);
        Assert.assertEquals(0, baseHelper.onGooglePlayServicesDisconnectingCount[0]);
    }

    @Test
    public void onLocationPermissionDialogDismissedWithoutPermission() throws Exception {
        application.denyPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);

        baseHelper.onLocationPermissionDialogDismissed();
        Assert.assertEquals(0, baseHelper.onLocationPermissionsGrantedCount[0]);
        Assert.assertEquals(0, baseHelper.onGooglePlayServicesDisconnectingCount[0]);
    }

    @Test
    public void onErrorResolutionActivityDismissed() throws Exception {
        // TODO: What to test here?
    }

    @Test
    public void onConnectedWithPermission() throws Exception {
        application.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);

        baseHelper.onConnected(null);
        Assert.assertEquals(1, baseHelper.onLocationPermissionsGrantedCount[0]);
        Assert.assertEquals(0, baseHelper.onGooglePlayServicesDisconnectingCount[0]);
    }

    @Test
    public void onConnectedWithoutPermission() throws Exception {
        application.denyPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);

        baseHelper.onConnected(null);
        Assert.assertEquals(0, baseHelper.onLocationPermissionsGrantedCount[0]);
        Assert.assertEquals(0, baseHelper.onGooglePlayServicesDisconnectingCount[0]);
    }

    @Test
    public void onConnectionSuspended() throws Exception {
        baseHelper.onConnectionSuspended(0);
        Assert.assertEquals(0, baseHelper.onLocationPermissionsGrantedCount[0]);
        Assert.assertEquals(0, baseHelper.onGooglePlayServicesDisconnectingCount[0]);
    }

    @Test
    public void onConnectionFailed() throws Exception {
        // TODO: What to test here?
    }

    static class MyBaseHelper extends BaseHelper {

        final int[] onLocationPermissionsGrantedCount = {0};
        final int[] onGooglePlayServicesDisconnectingCount = {0};

        MyBaseHelper(Context context, GoogleApiClientFactory googleApiClientFactory,
                     Subscriber<? super Location> subscriber) {
            super(context, googleApiClientFactory, subscriber);
        }

        @Override
        void onLocationPermissionsGranted() {
            onLocationPermissionsGrantedCount[0]++;
        }

        @Override
        void onGooglePlayServicesDisconnecting() {
            onGooglePlayServicesDisconnectingCount[0]++;
        }
    }
}
