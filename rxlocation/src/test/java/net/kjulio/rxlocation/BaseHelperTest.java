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

import rx.observers.TestSubscriber;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BaseHelperTest {

    private Context context;
    private MockGoogleApiClientFactoryImpl factory;
    private TestSubscriber<Location> testSubscriber;
    private ShadowApplication application;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        factory = new MockGoogleApiClientFactoryImpl();
        testSubscriber = TestSubscriber.create();
        application = Shadows.shadowOf(RuntimeEnvironment.application);
    }

    @Test
    public void connectionSuccessful() {
        application.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);

        final int[] count = {0};

        BaseHelper baseHelper = new BaseHelper(context, factory, testSubscriber) {

            @Override
            void onLocationPermissionsGranted() {
                count[0]++;
            }

            @Override
            void onGooglePlayServicesDisconnecting() {
                throw new RuntimeException("Shouldn't reach here.");
            }
        };

        baseHelper.onConnected(null);
        Assert.assertEquals(1, count[0]);
    }

    @Test
    public void permissionNotGranted() {
        application.denyPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);

        final int[] count = {0};

        BaseHelper baseHelper = new BaseHelper(context, factory, testSubscriber) {

            @Override
            void onLocationPermissionsGranted() {
                count[0]++;
            }

            @Override
            void onGooglePlayServicesDisconnecting() {
                throw new RuntimeException("Shouldn't reach here.");
            }
        };

        baseHelper.onConnected(null);
        Assert.assertEquals(0, count[0]);
    }
}
