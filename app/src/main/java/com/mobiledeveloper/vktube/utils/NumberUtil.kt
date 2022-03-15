package com.mobiledeveloper.vktube.utils

import android.content.Context
import com.mobiledeveloper.vktube.R

object NumberUtil {

    private const val ONE=1
    private const val FEW=2
    private const val MANY=99

    fun formatViewsNumber(number: Int, context: Context): String {
        var i = 0
        var resultNumber = number

        while (resultNumber >= 1000) {
            resultNumber /= 1000
            i++
        }

        return when (i) {
            0 -> context?.resources?.getQuantityString(R.plurals.views, ONE, resultNumber) ?: resultNumber.toString()
            1 -> context?.resources?.getQuantityString(R.plurals.views, FEW, resultNumber) ?: resultNumber.toString()
            2 -> context?.resources?.getQuantityString(R.plurals.views, MANY, resultNumber) ?: resultNumber.toString()
            else -> resultNumber.toString()
        }
    }
}