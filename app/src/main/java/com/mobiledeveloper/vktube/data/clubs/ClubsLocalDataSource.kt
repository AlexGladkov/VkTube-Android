package com.mobiledeveloper.vktube.data.clubs

import android.content.Context
import com.mobiledeveloper.vktube.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ClubsLocalDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.app_name), 0)
    }

    fun saveClubsIds(ids: List<Long>) {
        val storedString = ids.joinToString(separator = SEPARATOR)

        sharedPreferences.edit()
            .putString(CLUBS_IDS_KEY, storedString)
            .apply()
    }

    fun loadClubsIds(): List<Long> {
        val storedString = sharedPreferences.getString(CLUBS_IDS_KEY, "").orEmpty()

        return storedString.split(SEPARATOR).mapNotNull { it.toLongOrNull() }
    }

    fun saveIgnoreList(ids: List<Long>) {
        val storedString = ids.joinToString(separator = SEPARATOR)

        sharedPreferences.edit()
            .putString(CLUBS_IGNORED_KEY, storedString)
            .apply()
    }

    fun loadIgnoreList(): List<Long> {
        val storedString = sharedPreferences.getString(CLUBS_IGNORED_KEY, "").orEmpty()

        return storedString.split(SEPARATOR).mapNotNull { it.toLongOrNull() }
    }

    companion object {
        private const val SEPARATOR = ","
        private const val CLUBS_IDS_KEY = "clubs_key"
        private const val CLUBS_IGNORED_KEY = "ignored_key"
    }
}