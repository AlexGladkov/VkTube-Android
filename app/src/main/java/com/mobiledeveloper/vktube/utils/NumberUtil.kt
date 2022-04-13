package com.mobiledeveloper.vktube.utils

import com.mobiledeveloper.vktube.data.resources.StringsProvider
import com.mobiledeveloper.vktube.utils.Constants.FEW
import com.mobiledeveloper.vktube.utils.Constants.MANY
import com.mobiledeveloper.vktube.utils.Constants.ONE
import javax.inject.Inject

class NumberUtil @Inject constructor(
    private val provider: StringsProvider
) {

    fun formatNumberShort(
        number: Int,
        idFormat: Int,
        idDescriptor: Int
    ): String {

        var i = 0
        var resultNumber = number

        while (resultNumber >= 1000) {
            resultNumber /= 1000
            i++
        }
        return try {
            provider.getQuantityString(idFormat, i, resultNumber) + " " + formatDescriptorNumber(
                i,
                resultNumber,
                provider,
                idDescriptor
            )
        } catch (e: Exception) {
            ""
        }
    }

    private fun formatDescriptorNumber(
        count: Int,
        number: Int,
        stringsProvider: StringsProvider,
        idDescriptor: Int
    ): String = when (count) {
        0 -> with(number) {
            when {
                mod(10) == 1 -> stringsProvider.getQuantityString(idDescriptor, ONE)
                mod(10) in 5..9 -> stringsProvider.getQuantityString(idDescriptor, MANY)
                mod(10) in 2..4 && mod(100) !in 11..19 -> stringsProvider.getQuantityString(idDescriptor, FEW)
                else -> stringsProvider.getQuantityString(idDescriptor, MANY)
            }
        }
        else -> stringsProvider.getQuantityString(idDescriptor, MANY)
    }
}
