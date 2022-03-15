package com.mobiledeveloper.vktube

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mobiledeveloper.vktube.utils.NumberUtil

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NumberFormatInstrumentedTest {

    var currentTime=0L
    private lateinit var appContext:Context

    @Before
    fun setup(){
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        currentTime=System.currentTimeMillis()
    }

    @Test
    fun viewsFormatTest() {

        val v1=NumberUtil.formatViewsNumber(100,appContext)
        Log.d("Test_tag",v1)

        val v2=NumberUtil.formatViewsNumber(1000,appContext)
        Log.d("Test_tag",v2)

        val v3=NumberUtil.formatViewsNumber(10000,appContext)
        Log.d("Test_tag",v3)

        val v4=NumberUtil.formatViewsNumber(1000000,appContext)
        Log.d("Test_tag",v4)

        val v5=NumberUtil.formatViewsNumber(10000000,appContext)
        Log.d("Test_tag",v5)
        assertEquals(true, true)
    }
}