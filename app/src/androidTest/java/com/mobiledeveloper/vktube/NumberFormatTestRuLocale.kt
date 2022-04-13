package com.mobiledeveloper.vktube

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mobiledeveloper.vktube.data.resources.StringsProvider
import com.mobiledeveloper.vktube.data.resources.StringsProviderImpl
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
class NumberFormatTestRuLocale {

    var currentTime = 0L
    val TEST_TAG = "Test_tag"

    private lateinit var appContext: Context
    private lateinit var stringsProvider: StringsProvider
    private lateinit var numberUtil: NumberUtil

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        currentTime = System.currentTimeMillis()
        stringsProvider = StringsProviderImpl(appContext.resources)
        numberUtil = NumberUtil(stringsProvider)

    }
    @Test
    fun numberFormatTest1() {

        val result = numberUtil.formatNumberShort(
            1,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("1 просмотр", result)
    }

    @Test
    fun numberFormatTest2() {

        val result = numberUtil.formatNumberShort(
            101,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("101 просмотр", result)
    }

    @Test
    fun numberFormatTest3() {

        val result = numberUtil.formatNumberShort(
            102,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("102 просмотра", result)
    }

    @Test
    fun numberFormatTest4() {

        val result = numberUtil.formatNumberShort(
            105,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("105 просмотров", result)
    }

    @Test
    fun numberFormatTest5() {

        val result = numberUtil.formatNumberShort(
            112,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("112 просмотров", result)
    }

    @Test
    fun numberFormatTest6() {

        val result = numberUtil.formatNumberShort(
            122,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("122 просмотра", result)
    }

    @Test
    fun numberFormatTest7() {

        val result = numberUtil.formatNumberShort(
            125,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("125 просмотров", result)
    }

    @Test
    fun numberFormatTest8() {

        val result = numberUtil.formatNumberShort(
            1250,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("1 тыс. просмотров", result)
    }

    @Test
    fun numberFormatTest9() {

        val result = numberUtil.formatNumberShort(
            1250000,
            R.plurals.number_short_format,
            R.plurals.views
        )
        Log.d(TEST_TAG, result)

        assertEquals("1 млн. просмотров", result)
    }
}