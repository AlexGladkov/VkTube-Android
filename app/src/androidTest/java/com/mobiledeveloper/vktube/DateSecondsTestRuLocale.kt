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
class DateSecondsTestRuLocale {

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
    fun showSecondsTest1() {

        val secondsAgo = 1

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - secondsAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$secondsAgo секунду назад", timeAgoString)
    }

    @Test
    fun showSecondsTest2() {

        val secondsAgo = 2

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - secondsAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$secondsAgo секунды назад", timeAgoString)
    }

    @Test
    fun showSecondsTest5() {

        val secondsAgo = 5

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - secondsAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$secondsAgo секунд назад", timeAgoString)
    }

    @Test
    fun showSecondsTest11() {

        val secondsAgo = 11

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - secondsAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$secondsAgo секунд назад", timeAgoString)
    }

    @Test
    fun showSecondsTest12() {

        val secondsAgo = 12

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - secondsAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$secondsAgo секунд назад", timeAgoString)
    }

    @Test
    fun showSecondsTest21() {

        val secondsAgo = 21

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - secondsAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$secondsAgo секунду назад", timeAgoString)
    }

    @Test
    fun showSecondsTest32() {

        val secondsAgo = 32

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - secondsAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$secondsAgo секунды назад", timeAgoString)
    }

    @Test
    fun showSecondsTest49() {

        val secondsAgo = 49

        val timeAgoString =
            dateUtil.getTimeAgo((currentTime / 1000).toInt() - secondsAgo)

        Log.d(TEST_TAG, timeAgoString)

        assertEquals("$secondsAgo секунд назад", timeAgoString)
    }
}