package com.mobiledeveloper.vktube.utils

import android.content.Context
import com.mobiledeveloper.vktube.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtil {

    private const val UI_DATE_FORMAT = "dd MMM yyyy"

    fun getTimeAgo(unixTime:Int,context:Context?=null):String{
        try {
            val nowTime=System.currentTimeMillis()
            val time=unixTime*1000L

            val seconds = TimeUnit.MILLISECONDS.toSeconds(nowTime - time).toInt()
            val minutes = TimeUnit.MILLISECONDS.toMinutes(nowTime - time).toInt()
            val hours = TimeUnit.MILLISECONDS.toHours(nowTime - time).toInt()
            val days = TimeUnit.MILLISECONDS.toDays(nowTime - time).toInt()

            return when {
                days > 0 -> getDate(time, UI_DATE_FORMAT)
                hours > 0 -> when(hours){
                    1->hours.toString()+" "+context?.resources?.getString(R.string.hour_ago)
                    21->hours.toString()+" "+context?.resources?.getString(R.string.hour_ago)
                    in 2..5->hours.toString()+" "+context?.resources?.getString(R.string.hours_ago_2_3_4)
                    in 22..23->hours.toString()+" "+context?.resources?.getString(R.string.hours_ago_2_3_4)
                    else -> hours.toString()+" "+context?.resources?.getString(R.string.hours_ago)
                }
                minutes > 0 -> with(minutes) {
                    when {
                        equals(1) -> minutes.toString() + " " + context?.resources?.getString(R.string.ru_1_minute_ago)
                        toInt() in 5..19 -> minutes.toString() + " " + context?.resources?.getString(R.string.ru_10_minute_ago)
                        mod(10) == 0 -> minutes.toString() + " " + context?.resources?.getString(R.string.ru_10_minute_ago)
                        mod(10) == 1 -> minutes.toString() + " " + context?.resources?.getString(R.string.ru_1_minute_ago)
                        mod(10) in 2..4 -> minutes.toString() + " " + context?.resources?.getString(R.string.ru_2_minutes_ago)
                        mod(10) in 5..9 -> minutes.toString() + " " + context?.resources?.getString(R.string.ru_10_minute_ago)
                        else -> ""
                    }
                }
                else -> with(seconds) {
                    when {
                        equals(1) -> seconds.toString() + " " + context?.resources?.getString(R.string.ru_1_second_ago)
                        toInt() in 5..19 -> seconds.toString() + " " + context?.resources?.getString(R.string.ru_10_seconds_ago)
                        mod(10) == 0 -> seconds.toString() + " " + context?.resources?.getString(R.string.ru_10_seconds_ago)
                        mod(10) == 1 -> seconds.toString() + " " + context?.resources?.getString(R.string.ru_1_second_ago)
                        mod(10) in 2..4 -> seconds.toString() + " " + context?.resources?.getString(R.string.ru_2_seconds_ago)
                        mod(10) in 5..9 -> seconds.toString() + " " + context?.resources?.getString(R.string.ru_10_seconds_ago)
                        else -> ""
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return ""
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