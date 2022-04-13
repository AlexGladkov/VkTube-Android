package com.mobiledeveloper.vktube.di

import android.content.Context
import com.mobiledeveloper.vktube.data.resources.StringsProvider
import com.mobiledeveloper.vktube.data.resources.StringsProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ResourcesModule {

    @Provides
    fun provideResources(@ApplicationContext context: Context): StringsProvider = StringsProviderImpl(context.resources)
}
