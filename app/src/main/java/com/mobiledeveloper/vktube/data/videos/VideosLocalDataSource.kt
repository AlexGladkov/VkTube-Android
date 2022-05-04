package com.mobiledeveloper.vktube.data.videos

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobiledeveloper.vktube.R
import com.mobiledeveloper.vktube.ui.screens.feed.models.VideoCellModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class VideosLocalDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences by lazy {
        context.getSharedPreferences(context.getString(R.string.app_name), 0)
    }

    private val type = object : TypeToken<List<VideoCellModel>>() {}.type

    fun saveVideos(videos: List<VideoCellModel>) {
        val storedString = Gson().toJson(videos, type)

        sharedPreferences.edit().putString(VIDEOS_IDS_KEY, storedString).apply()
    }

    fun loadVideos(): List<VideoCellModel> {
        val storedString = sharedPreferences.getString(VIDEOS_IDS_KEY, "").orEmpty()

        return Gson().fromJson(storedString, type)
    }

    fun clearVideos() {
        val storedString = Gson().toJson(emptyList<VideoCellModel>(), type)

        sharedPreferences.edit().putString(VIDEOS_IDS_KEY, storedString).apply()
    }

    companion object {
        private const val VIDEOS_IDS_KEY = "videos_key"
    }
}