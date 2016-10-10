package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLooper;

import rx.observers.TestSubscriber;

@RunWith(RobolectricTestRunner.class)
public class RxLocationTest {

    private Context context;
    private TestSubscriber<Location> testSubscriber;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        testSubscriber = TestSubscriber.create();
    }

    @Test
    public void connectionSuccessful() {
        // Successful run of lastLocation() observable.
        MockRxLocation.lastLocation(context).toBlocking().subscribe(testSubscriber);
        ShadowLooper.runUiThreadTasks();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

    @Test
    public void connectionFailed() {
        // Failed run of lastLocation() observable.
        // GoogleApiClientFactory.setMockConnectionFailure(true);
        MockRxLocation.lastLocation(context).toBlocking().subscribe(testSubscriber);
        ShadowLooper.runUiThreadTasks();
        Assert.assertTrue(testSubscriber.getOnErrorEvents().size() >= 1);
    }

//    @Test
//    public void testLocationUpdates() {
//        // Successful run of locationUpdates() observable.
//        LocationRequest defaultLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_NO_POWER);
//        MockRxLocation.locationUpdates(context, defaultLocationRequest)
//                .first().toBlocking().subscribe(testSubscriber);
//        testSubscriber.assertValueCount(1);
//    }
}
