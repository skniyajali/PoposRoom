package com.niyaj.poposroom.features.main_feed.di

import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.main_feed.data.dao.MainFeedDao
import com.niyaj.poposroom.features.main_feed.data.repository.MainFeedRepositoryImpl
import com.niyaj.poposroom.features.main_feed.domain.repository.MainFeedRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object MainFeedModule {

    @Provides
    fun provideMainFeedDao(database: PoposDatabase) : MainFeedDao {
        return database.mainFeedDao()
    }

    @Provides
    fun provideMainFeedRepository(
        mainFeedDao: MainFeedDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): MainFeedRepository {
        return MainFeedRepositoryImpl(mainFeedDao, ioDispatcher)
    }

}