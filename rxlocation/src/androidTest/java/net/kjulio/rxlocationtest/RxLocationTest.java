package net.kjulio.rxlocationtest;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.kjulio.rxlocation.RxLocation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rx.android.schedulers.AndroidSchedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class RxLocationTest {

    private Context context; // Context of the app under test.
    private RxLocation rxLocation; // Class under test.

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        rxLocation = RxLocation.newInstance(context);
    }

    @Test
    public void appPackageName() {
        assertEquals("net.kjulio.rxlocation.test", context.getPackageName());
    }

    @Test
    public void testBasicLocation1() {
        Location location = rxLocation.locationSingle().observeOn(AndroidSchedulers.mainThread()).toBlocking().value();
        assertNotNull(location);
    }

    @Test
    public void testBasicLocation2() {
        Location location = rxLocation.locationObservable().observeOn(AndroidSchedulers.mainThread()).toBlocking().first();
        assertNotNull(location);
    }

    @Test
    public void testBasicLocation3() {
        Location location = rxLocation.lastLocation();
        assertNotNull(location);
    }

    @After
    public void tearDown() {
        context = null;
        rxLocation = null;
    }

}
