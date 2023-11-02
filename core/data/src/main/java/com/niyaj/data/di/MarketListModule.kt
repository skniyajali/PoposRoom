package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.MarketListRepositoryImpl
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.database.dao.MarketItemDao
import com.niyaj.database.dao.MarketListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object MarketListModule {

    @Provides
    fun provideMarketListRepository(
        marketListDao: MarketListDao,
        marketItemDao: MarketItemDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): MarketListRepository {
        return MarketListRepositoryImpl(marketListDao, marketItemDao, ioDispatcher)
    }
}