package com.mobiledeveloper.vktube

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mobiledeveloper.vktube.utils.NumberUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NumberFormatInstrumentedTest {

    var currentTime = 0L
    val TEST_TAG = "Test_tag"

    private lateinit var appContext: Context

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        currentTime = System.currentTimeMillis()
    }

    @Test
    fun numberFormatTest1() {

        val result = NumberUtil.formatNumberShort(
            1,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("1 просмотр", result)
    }

    @Test
    fun numberFormatTest2() {

        val result = NumberUtil.formatNumberShort(
            101,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("101 просмотр", result)
    }

    @Test
    fun numberFormatTest3() {

        val result = NumberUtil.formatNumberShort(
            102,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("102 просмотра", result)
    }

    @Test
    fun numberFormatTest4() {

        val result = NumberUtil.formatNumberShort(
            105,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("105 просмотров", result)
    }

    @Test
    fun numberFormatTest5() {

        val result = NumberUtil.formatNumberShort(
            112,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("112 просмотров", result)
    }

    @Test
    fun numberFormatTest6() {

        val result = NumberUtil.formatNumberShort(
            122,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("122 просмотра", result)
    }

    @Test
    fun numberFormatTest7() {

        val result = NumberUtil.formatNumberShort(
            125,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("125 просмотров", result)
    }

    @Test
    fun numberFormatTest8() {

        val result = NumberUtil.formatNumberShort(
            1250,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("1 тыс. просмотров", result)
    }

    @Test
    fun numberFormatTest9() {

        val result = NumberUtil.formatNumberShort(
            1250000,
            appContext,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("1 млн. просмотров", result)
    }
}