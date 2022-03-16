package com.mobiledeveloper.vktube.utils

import android.content.Context
import android.content.res.Resources

object NumberUtil {

    private const val ONE = 1
    private const val FEW = 2
    private const val MANY = 99

    fun formatNumberShort(number: Int, context: Context, idFormat:Int, idDescriptor:Int): String {

        val res=context.resources
        var i = 0
        var resultNumber = number

        while (resultNumber >= 1000) {
            resultNumber /= 1000
            i++
        }

        return res?.getQuantityString(idFormat, i, resultNumber) +" "+formatDescriptorNumber(i, resultNumber, res, idDescriptor)

    }

    private fun formatDescriptorNumber(count:Int, number: Int, res: Resources?, idDescriptor:Int): String? {

        return when (count) {
            0 -> return with(number) {
                when {
                    mod(10) == 1 -> res?.getQuantityString(idDescriptor, ONE)
                    mod(10) in 5..9 -> res?.getQuantityString(idDescriptor, MANY)
                    mod(10) in 2..4 && mod(100) !in 11..19 -> res?.getQuantityString(idDescriptor, FEW)
                    else -> res?.getQuantityString(idDescriptor, MANY)
                }
            }
            else -> res?.getQuantityString(idDescriptor, MANY)
        }
    }
}