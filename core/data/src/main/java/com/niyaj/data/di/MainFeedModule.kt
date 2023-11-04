package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.HomeRepositoryImpl
import com.niyaj.data.repository.HomeRepository
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
    ): HomeRepository {
        return HomeRepositoryImpl(mainFeedDao, ioDispatcher)
    }

}