package com.mobiledeveloper.vktube.utils

import android.content.Context
import com.mobiledeveloper.vktube.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtil {


    private const val UI_DATE_FORMAT = "dd MMM yyyy"
    private const val ONE = 1
    private const val FEW = 2
    private const val MANY = 99

    fun getTimeAgo(unixTime: Int, context: Context): String {

            val res = context.resources

            val nowTime = System.currentTimeMillis()
            val time = unixTime * 1000L

            val seconds = TimeUnit.MILLISECONDS.toSeconds(nowTime - time).toInt()
            val minutes = TimeUnit.MILLISECONDS.toMinutes(nowTime - time).toInt()
            val hours = TimeUnit.MILLISECONDS.toHours(nowTime - time).toInt()
            val days = TimeUnit.MILLISECONDS.toDays(nowTime - time).toInt()

            return when {
                days > 0 -> getDate(time, UI_DATE_FORMAT)
                hours > 0 -> when(hours){
                    1 -> res.getQuantityString(R.plurals.hours, ONE, hours) ?: hours.toString()
                    21 -> res.getQuantityString(R.plurals.hours, ONE, hours) ?: hours.toString()
                    in 2..4 -> res.getQuantityString(R.plurals.hours, FEW, hours) ?: hours.toString()
                    in 22..23 -> res.getQuantityString(R.plurals.hours, FEW, hours) ?: hours.toString()
                    else -> res.getQuantityString(R.plurals.hours, MANY, hours) ?: hours.toString()
                }
                minutes > 0 -> with(minutes) {
                    when {
                        equals(1) -> res.getQuantityString(R.plurals.minutes, ONE, minutes) ?: minutes.toString()
                        toInt() in 5..19 -> res.getQuantityString(R.plurals.minutes, MANY, minutes) ?: minutes.toString()
                        mod(10) == 0 -> res.getQuantityString(R.plurals.minutes, MANY, minutes) ?: minutes.toString()
                        mod(10) == 1 -> res.getQuantityString(R.plurals.minutes, ONE, minutes) ?: minutes.toString()
                        mod(10) in 2..4 -> res.getQuantityString(R.plurals.minutes, FEW, minutes) ?: minutes.toString()
                        mod(10) in 5..9 -> res.getQuantityString(R.plurals.minutes, MANY, minutes) ?: minutes.toString()
                        else -> ""
                    }
                }
                else -> with(seconds) {
                    when {
                        equals(1) -> res.getQuantityString(R.plurals.seconds, ONE, seconds) ?: seconds.toString()
                        toInt() in 5..19 -> res.getQuantityString(R.plurals.seconds, MANY, seconds) ?: seconds.toString()
                        mod(10) == 0 -> res.getQuantityString(R.plurals.seconds, MANY, seconds) ?: seconds.toString()
                        mod(10) == 1 -> res.getQuantityString(R.plurals.seconds, ONE, seconds) ?: seconds.toString()
                        mod(10) in 2..4 -> res.getQuantityString(R.plurals.seconds, FEW, seconds) ?: seconds.toString()
                        mod(10) in 5..9 -> res.getQuantityString(R.plurals.seconds, MANY, seconds) ?: seconds.toString()
                        else -> ""
                    }
                }
            }

    }

    private fun getDate(milliSeconds: Long, dateFormat: String): String {
        return try {
            val formatter = SimpleDateFormat(dateFormat,Locale.getDefault())
            val calendar: Calendar = Calendar.getInstance()

            calendar.timeInMillis = milliSeconds
            formatter.format(calendar.time)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}