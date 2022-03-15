package com.mobiledeveloper.vktube.utils

import android.content.Context
import com.mobiledeveloper.vktube.R

object NumberUtil {

    fun formatNumber(number:Int,context: Context): String {
        var i=0

        var resultNumber=number

        while(resultNumber>=1000){
            resultNumber /= 1000
            i++
        }

        return when (i){
            0-> "$resultNumber ${context.resources.getString(R.string.views)}"
            1-> "$resultNumber ${context.resources.getString(R.string.thousand)} ${context.resources.getString(R.string.views)}"
            2-> "$resultNumber ${context.resources.getString(R.string.million)} ${context.resources.getString(R.string.views)}"
            else-> resultNumber.toString()
        }
    }
}