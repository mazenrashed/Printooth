package me.splashco.universalbluethootprinting

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.util.Log
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.


        Log.d("TESTSTRING", Integer.parseInt("LF").toString())
        val appContext = InstrumentationRegistry.getTargetContext()

        assertEquals("me.splashco.universalbluethootprinting.test", appContext.packageName)
    }
}
