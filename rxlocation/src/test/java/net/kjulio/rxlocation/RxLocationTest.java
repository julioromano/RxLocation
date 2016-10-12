package net.kjulio.rxlocation;

import android.content.Context;
import android.location.Location;
import android.os.HandlerThread;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowLooper;

import rx.android.schedulers.AndroidSchedulers;
import rx.observers.TestSubscriber;

import static java.lang.Thread.sleep;

@RunWith(RobolectricTestRunner.class)
public class RxLocationTest {

    private Context context;
    private TestSubscriber<Location> testSubscriber;
    private HandlerThread handlerThread;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        testSubscriber = TestSubscriber.create();
        handlerThread = new HandlerThread("testHandlerThread");
        handlerThread.start();
    }

    @Test
    public void connectionSuccessful() {
        // Successful run of lastLocation() observable.
        MockRxLocation.lastLocation(context)
                .subscribeOn(AndroidSchedulers.from(handlerThread.getLooper()))
                .toBlocking()
                .subscribe(testSubscriber);
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//        ((ShadowLooper) ShadowExtractor.extract(handlerThread.getLooper())).runToEndOfTasks();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testSubscriber.assertValueCount(1);
        testSubscriber.assertCompleted();
    }

//    @Test
//    public void connectionFailed() {
//        // Failed run of lastLocation() observable.
//        // GoogleApiClientFactory.setMockConnectionFailure(true);
//        MockRxLocation.lastLocation(context)
//                .subscribeOn(AndroidSchedulers.mainThread())
////                .toBlocking()
//                .subscribe(testSubscriber);
//        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
//        Assert.assertTrue(testSubscriber.getOnErrorEvents().size() >= 1);
//    }

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
