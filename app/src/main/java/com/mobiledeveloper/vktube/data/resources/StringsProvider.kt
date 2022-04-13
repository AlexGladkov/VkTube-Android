package com.mobiledeveloper.vktube.data.resources

import androidx.annotation.PluralsRes

interface StringsProvider {
    fun getQuantityString(@PluralsRes res: Int, quantity: Int, vararg params: Any): String
}
