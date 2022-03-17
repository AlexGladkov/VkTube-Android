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
            hours > 0 -> getTimeWithDescriptor(hours, res, R.plurals.hours)
            minutes > 0 -> getTimeWithDescriptor(minutes, res, R.plurals.minutes)
            else -> getTimeWithDescriptor(seconds, res, R.plurals.seconds)
        }
    }

    private fun getTimeWithDescriptor(value: Int, res: Resources, idDescriptor: Int): String {
        return "$value"
//        return with(value) {
//            when {
//                mod(10) == 1 -> res.getQuantityString(idDescriptor, ONE, value)
//                mod(10) in 2..4 -> res.getQuantityString(idDescriptor, FEW, value)
//                mod(10) in 5..9 || mod(10) == 0 -> res.getQuantityString(idDescriptor, MANY, value)
//                else -> ""
//            }
//        }
    }

    private fun getDate(milliSeconds: Long, dateFormat: String): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = milliSeconds

        return formatter.format(calendar.time)
    }
}