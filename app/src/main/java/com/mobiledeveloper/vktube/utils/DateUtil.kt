package com.mobiledeveloper.vktube.utils

import android.content.Context
import android.content.res.Resources
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.utils.Constants.FEW
import com.mobiledeveloper.vktube.utils.Constants.MANY
import com.mobiledeveloper.vktube.utils.Constants.ONE
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtil {
    private const val UI_DATE_FORMAT = "dd MMM yyyy"

    private val dateFormat by lazy {
        SimpleDateFormat(UI_DATE_FORMAT, Locale.getDefault())
    }

    fun getTimeAgo(unixTime: Int, context: Context): String {
        val res = context.resources

        val time = unixTime * 1000L
        val passedTime = System.currentTimeMillis() - time

        val days = TimeUnit.MILLISECONDS.toDays(passedTime).toInt()
        if (days > 0) return getDate(time)

        val hours = TimeUnit.MILLISECONDS.toHours(passedTime).toInt()
        if (hours > 0) return res.getQuantityString(R.plurals.hours, hours, hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(passedTime).toInt()
        if (minutes > 0) return res.getQuantityString(R.plurals.minutes, minutes, minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(passedTime).toInt()
        return res.getQuantityString(R.plurals.seconds, seconds, seconds)
    }


    private fun getDate(milliSeconds: Long): String {
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = milliSeconds

        return dateFormat.format(calendar.time)
    }
}