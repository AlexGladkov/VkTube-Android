package com.mobiledeveloper.di

import android.content.Context
import com.mobiledeveloper.vktube.data.videos.VideosDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RoomModule {
    @Provides
    fun provideVideosDatabase(@ApplicationContext context:Context): VideosDatabase {
        return VideosDatabase.getDatabase(context)
    }
}