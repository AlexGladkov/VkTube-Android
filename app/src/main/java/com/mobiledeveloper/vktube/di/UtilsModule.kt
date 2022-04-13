package com.mobiledeveloper.vktube.di

import com.mobiledeveloper.vktube.data.resources.StringsProvider
import com.mobiledeveloper.vktube.utils.DateUtil
import com.mobiledeveloper.vktube.utils.NumberUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    // TODO use interface
    @Provides
    fun provideDateUtils(stringsProvider: StringsProvider): DateUtil = DateUtil(stringsProvider)

    // TODO use interface
    @Provides
    fun provideNumberUtils(stringsProvider: StringsProvider): NumberUtil = NumberUtil(stringsProvider)
}
