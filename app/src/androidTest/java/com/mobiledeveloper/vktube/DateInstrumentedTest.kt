package com.mobiledeveloper.vktube

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mobiledeveloper.vktube.utils.DateUtil

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
class DateInstrumentedTest {

    var currentTime=0L
    val TEST_TAG="Test_tag"

    private lateinit var appContext:Context

    @Before
    fun setup(){
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        currentTime=System.currentTimeMillis()
    }

    @Test
    fun showDateTest() {

        val date=DateUtil.getTimeAgo((currentTime/1000).toInt()-2*24*60*60,appContext)

        Log.d(TEST_TAG,date)
        assertEquals(true, true)
    }

    @Test
    fun showSecondsTest() {

        for (i in 1..60){
            val v=DateUtil.getTimeAgo((currentTime/1000).toInt()-i,appContext)
            Log.d(TEST_TAG,v)
        }

        assertEquals(true, true)
    }

    @Test
    fun showMinutesTest() {

        for (i in 1..60){
            val v=DateUtil.getTimeAgo((currentTime/1000).toInt()-i*60,appContext)
            Log.d(TEST_TAG,v)
        }

        assertEquals(true, true)
    }

    @Test
    fun showHoursTest() {

        for (i in 1..24){
            val v=DateUtil.getTimeAgo((currentTime/1000).toInt()-i*60*60,appContext)
            Log.d(TEST_TAG,v)
        }

        assertEquals(true, true)
    }
}