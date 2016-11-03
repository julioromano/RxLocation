package net.kjulio.rxlocationtest;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.location.LocationRequest;

import net.kjulio.rxlocation.RxLocation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RxLocationAndroidTest {

    private Context context; // Context of the app under test.
    private TestObserver<Location> testObserver;
    private LocationRequest locationRequest;
    private RxLocation rxLocation;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        testObserver = TestObserver.create();
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        rxLocation = new RxLocation(context);
    }

    @Test
    public void appPackageName() {
        assertEquals("net.kjulio.rxlocation.test", context.getPackageName());
    }

    @Test
    public void testLastLocation() {
        rxLocation.lastLocation()
                .subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();
    }

    @Test
    public void testLastLocationUiThread() {
        rxLocation.lastLocation()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();
    }

    @Test
    public void testLastLocationNewThread() {
        rxLocation.lastLocation()
                .subscribeOn(Schedulers.newThread())
                .subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertValueCount(1);
        testObserver.assertComplete();
    }

    @Test
    public void testLocationUpdates() {
        rxLocation.locationUpdates(locationRequest)
                .firstElement()
                .subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertValueCount(1);
    }

    @Test
    public void testLocationUpdatesUiThread() {
        rxLocation.locationUpdates(locationRequest)
                .subscribeOn(AndroidSchedulers.mainThread())
                .firstElement()
                .subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertValueCount(1);
    }

    @Test
    public void testLocationUpdatesNewThread() {
        rxLocation.locationUpdates(locationRequest)
                .subscribeOn(Schedulers.newThread())
                .firstElement()
                .subscribe(testObserver);
        testObserver.awaitTerminalEvent();
        testObserver.assertValueCount(1);
    }

}
