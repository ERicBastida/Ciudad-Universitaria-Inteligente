<<<<<<< HEAD:app/src/androidTest/java/com/eric/ciudaduniversitariainteligente/ExampleInstrumentedTest.java
package com.eric.ciudaduniversitariainteligente;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.eric.ciudaduniversitariainteligente", appContext.getPackageName());
    }
}
=======
package com.eric.tpfinal;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.eric.tpfinal", appContext.getPackageName());
    }
}
>>>>>>> 73afa956a024f75bf4c7b9dc0f531b4f776cf142:app/src/androidTest/java/com/eric/tpfinal/ExampleInstrumentedTest.java
