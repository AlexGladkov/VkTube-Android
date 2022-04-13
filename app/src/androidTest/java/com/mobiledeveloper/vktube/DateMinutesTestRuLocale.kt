package com.mobiledeveloper.vktube

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mobiledeveloper.vktube.data.resources.StringsProvider
import com.mobiledeveloper.vktube.data.resources.StringsProviderImpl
import com.mobiledeveloper.vktube.utils.DateUtil
import com.mobiledeveloper.vktube.utils.NumberUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DateMinutesTestRuLocale {

    var currentTime = 0L
    val TEST_TAG = "Test_tag"

    private lateinit var appContext: Context
    private lateinit var stringsProvider: StringsProvider
    private lateinit var dateUtil: DateUtil
    private lateinit var numberUtil: NumberUtil

    @Before
    fun setup() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        currentTime = System.currentTimeMillis()
        stringsProvider = StringsProviderImpl(appContext.resources)
        dateUtil = DateUtil(stringsProvider)
        numberUtil = NumberUtil(stringsProvider)

    }

    @Test
    fun showMinutesTest1() {

        val minutesAgo = 1

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * minutesAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$minutesAgo минуту назад", timeAgoString)
    }

    @Test
    fun showMinutesTest2() {

        val minutesAgo = 2

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * minutesAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$minutesAgo минуты назад", timeAgoString)
    }

    @Test
    fun showMinutesTest5() {

        val minutesAgo = 5

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * minutesAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$minutesAgo минут назад", timeAgoString)
    }

    @Test
    fun showMinutesTest11() {

        val minutesAgo = 11

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * minutesAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$minutesAgo минут назад", timeAgoString)
    }

    @Test
    fun showMinutesTest12() {

        val minutesAgo = 12

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * minutesAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$minutesAgo минут назад", timeAgoString)
    }

    @Test
    fun showMinutesTest21() {

        val minutesAgo = 21

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * minutesAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$minutesAgo минуту назад", timeAgoString)
    }

    @Test
    fun showMinutesTest32() {

        val minutesAgo = 32

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * minutesAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$minutesAgo минуты назад", timeAgoString)
    }

    @Test
    fun showMinutesTest49() {

        val minutesAgo = 49

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * minutesAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$minutesAgo минут назад", timeAgoString)
    }
}