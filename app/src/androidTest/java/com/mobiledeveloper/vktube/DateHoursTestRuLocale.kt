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
class DateHoursTestRuLocale {

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
    fun showHoursTest1() {

        val hoursAgo = 1

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * 60 * hoursAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$hoursAgo час назад", timeAgoString)
    }

    @Test
    fun showHoursTest2() {

        val hoursAgo = 2

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * 60 * hoursAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$hoursAgo часа назад", timeAgoString)
    }

    @Test
    fun showHoursTest5() {

        val hoursAgo = 5

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * 60 * hoursAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$hoursAgo часов назад", timeAgoString)
    }

    @Test
    fun showHoursTest11() {

        val hoursAgo = 11

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * 60 * hoursAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$hoursAgo часов назад", timeAgoString)
    }

    @Test
    fun showHoursTest12() {

        val hoursAgo = 12

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * 60 * hoursAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$hoursAgo часов назад", timeAgoString)
    }

    @Test
    fun showHoursTest21() {

        val hoursAgo = 21

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * 60 * hoursAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$hoursAgo час назад", timeAgoString)
    }

    @Test
    fun showHoursTest22() {

        val hoursAgo = 22

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - 60 * 60 * hoursAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$hoursAgo часа назад", timeAgoString)
    }
}