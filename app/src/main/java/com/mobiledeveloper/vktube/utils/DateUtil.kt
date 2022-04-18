package com.mobiledeveloper.vktube.utils

import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.data.resources.StringsProvider
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DateUtil @Inject constructor(
    private val stringsProvider: StringsProvider
) {
    private val dateFormat by lazy {
        SimpleDateFormat(UI_DATE_FORMAT, Locale.getDefault())
    }

    fun getTimeAgo(unixTime: Int): String {

        val time = unixTime * 1000L
        val passedTime = System.currentTimeMillis() - time

        val days = TimeUnit.MILLISECONDS.toDays(passedTime).toInt()
        if (days > 0) return getDate(time)

        val hours = TimeUnit.MILLISECONDS.toHours(passedTime).toInt()
        if (hours > 0) return stringsProvider.getQuantityString(R.plurals.hours, hours, hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(passedTime).toInt()
        if (minutes > 0) return stringsProvider.getQuantityString(R.plurals.minutes, minutes, minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(passedTime).toInt()
        return stringsProvider.getQuantityString(R.plurals.seconds, seconds, seconds)
    }

    private fun getDate(milliSeconds: Long): String {
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = milliSeconds

        return dateFormat.format(calendar.time)
    }

    private companion object {
        const val UI_DATE_FORMAT = "dd MMM yyyy"
    }
}