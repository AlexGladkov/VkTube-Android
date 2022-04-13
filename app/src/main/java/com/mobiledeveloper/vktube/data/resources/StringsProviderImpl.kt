package com.mobiledeveloper.vktube.data.resources

import android.content.res.Resources
import androidx.annotation.PluralsRes
import javax.inject.Inject

class StringsProviderImpl @Inject constructor(
    private val resources: Resources
) : StringsProvider {
    override fun getQuantityString(@PluralsRes res: Int, quantity: Int, vararg params: Any): String =
        resources.getQuantityString(res, quantity, *params)
}
