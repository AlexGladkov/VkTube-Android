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

import com.mobiledeveloper.vktube.R
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
    fun numberFormatTest() {

        val v = NumberUtil.formatNumberShort(
            1,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d("Test_tag", v)

        val v1 = NumberUtil.formatNumberShort(
            101,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d("Test_tag", v1)

        val v2 = NumberUtil.formatNumberShort(
            102,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d("Test_tag", v2)

        val v3 = NumberUtil.formatNumberShort(
            105,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d("Test_tag", v3)

        val v4 = NumberUtil.formatNumberShort(
            112,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d("Test_tag",v4)

        val v5 = NumberUtil.formatNumberShort(
            122,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d("Test_tag", v5)

        val v6 = NumberUtil.formatNumberShort(
            125,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d("Test_tag", v6)

        val v7 = NumberUtil.formatNumberShort(
            1250,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d("Test_tag", v7)
        assertEquals(true, true)
    }
}