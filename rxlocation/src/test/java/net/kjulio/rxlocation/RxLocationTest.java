package net.kjulio.rxlocation;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.HandlerThread;

import com.google.android.gms.location.LocationRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import rx.android.schedulers.AndroidSchedulers;
import rx.observers.TestSubscriber;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxLocationTest {

    private Context context;
    private ShadowApplication application;
    private LocationRequest locationRequest;
    private TestSubscriber<Location> testSubscriber;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        application = Shadows.shadowOf(RuntimeEnvironment.application);
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        testSubscriber = TestSubscriber.create();

        application.grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    //    @Test
//    public void lastLocationConnectionFailed() {
//        // Failed run of lastLocation() observable.
//        // GoogleApiClientFactory.setMockConnectionFailure(true);
//        MockRxLocation.lastLocation(context)
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .toBlocking()
//                .subscribe(testSubscriber);
//        ShadowLooper.runUiThreadTasks();
//        Assert.assertTrue(testSubscriber.getOnErrorEvents().size() >= 1);
//    }

//    @Test
//    public void testLastLocation() {
//        MockRxLocation.lastLocation(context)
//                .subscribe(testSubscriber);
//        testSubscriber.awaitTerminalEvent();
//        testSubscriber.assertValueCount(1);
//        testSubscriber.assertCompleted();
//    }

    @Test
    public void testLastLocationUiThread() {
        MockRxLocation.lastLocation(context)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(testSubscriber);
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

//    @Test
//    public void testLastLocationHandlerThread() {
//        HandlerThread handlerThread = new HandlerThread("MockHandlerThread");
//        handlerThread.start();
//        MockRxLocation.lastLocation(context)
//                .subscribeOn(AndroidSchedulers.from(handlerThread.getLooper()))
//                .subscribe(testSubscriber);
////        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//        testSubscriber.awaitTerminalEvent();
//        testSubscriber.assertValueCount(1);
//        testSubscriber.assertCompleted();
//    }

//    @Test
//    public void testLastLocationNewThread() {
//        MockRxLocation.lastLocation(context)
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(testSubscriber);
//        testSubscriber.awaitTerminalEvent();
//        testSubscriber.assertValueCount(1);
//        testSubscriber.assertCompleted();
//    }

//    @Test
//    public void testLocationUpdates() {
//        RxLocation.locationUpdates(context, locationRequest)
//                .first()
//                .subscribe(testSubscriber);
//        testSubscriber.awaitTerminalEvent();
//        testSubscriber.assertValueCount(1);
//    }

//    @Test
//    public void testLocationUpdatesUiThread() {
//        RxLocation.locationUpdates(context, locationRequest)
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .first()
//                .subscribe(testSubscriber);
//        testSubscriber.awaitTerminalEvent();
//        testSubscriber.assertValueCount(1);
//    }

//    @Test
//    public void testLocationUpdatesNewThread() {
//        RxLocation.locationUpdates(context, locationRequest)
//                .subscribeOn(Schedulers.newThread())
//                .first()
//                .subscribe(testSubscriber);
//        testSubscriber.awaitTerminalEvent();
//        testSubscriber.assertValueCount(1);
//    }
}
