package com.mobiledeveloper.vktube.utils

import android.content.Context
import android.content.res.Resources

object NumberUtil {

    private const val ONE=1
    private const val FEW=2
    private const val MANY=99

    fun formatNumberShort(number: Int, context: Context, idFormat:Int, idDescriptor:Int): String {

        val res=context.resources
        var i = 0
        var resultNumber = number

        while (resultNumber >= 1000) {
            resultNumber /= 1000
            i++
        }

        return when (i) {
            0 -> res?.getQuantityString(idFormat, ONE, resultNumber) +" "+
                    formatDescriptorNumber(i, resultNumber, res, idDescriptor)
            1 -> res?.getQuantityString(idFormat, FEW, resultNumber) +" "+
                    formatDescriptorNumber(i, resultNumber, res, idDescriptor)
            2 -> res?.getQuantityString(idFormat, MANY, resultNumber) +" "+
                    formatDescriptorNumber(i, resultNumber, res, idDescriptor)
            else -> resultNumber.toString() +" "+
                    formatDescriptorNumber(i, resultNumber, res, idDescriptor)
        }
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