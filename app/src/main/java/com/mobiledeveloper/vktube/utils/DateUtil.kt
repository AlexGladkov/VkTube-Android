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
        if (hours > 0) return getTimeWithDescriptor(hours, res, R.plurals.hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(passedTime).toInt()
        if (minutes > 0) return getTimeWithDescriptor(minutes, res, R.plurals.minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(passedTime).toInt()
        return getTimeWithDescriptor(seconds, res, R.plurals.seconds)
    }

    private fun getTimeWithDescriptor(value: Int, res: Resources, idDescriptor: Int): String {
        return with(value) {
            when {
                mod(10) == 1 -> res.getQuantityString(idDescriptor, ONE, value)
                mod(10) in 2..4 -> res.getQuantityString(idDescriptor, FEW, value)
                mod(10) in 5..9 || mod(10) == 0 -> res.getQuantityString(idDescriptor, MANY, value)
                else -> ""
            }
        }
    }

    private fun getDate(milliSeconds: Long): String {
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = milliSeconds

        return dateFormat.format(calendar.time)
    }
}