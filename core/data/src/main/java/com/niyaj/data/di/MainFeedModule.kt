package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.MainFeedRepositoryImpl
import com.niyaj.data.repository.MainFeedRepository
import com.niyaj.database.dao.MainFeedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object MainFeedModule {

    @Provides
    fun provideMainFeedRepository(
        mainFeedDao: MainFeedDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): MainFeedRepository {
        return MainFeedRepositoryImpl(mainFeedDao, ioDispatcher)
    }

}