package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.MarketItemRepositoryImpl
import com.niyaj.data.repository.MarketItemRepository
import com.niyaj.data.repository.validation.MarketItemValidationRepository
import com.niyaj.database.dao.MarketItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object MarketListModule {

    @Provides
    fun provideMarketListValidationRepository(
        marketItemDao: MarketItemDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): MarketItemValidationRepository {
        return MarketItemRepositoryImpl(marketItemDao, ioDispatcher)
    }

    @Provides
    fun provideMarketListRepository(
        marketItemDao: MarketItemDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): MarketItemRepository {
        return MarketItemRepositoryImpl(marketItemDao, ioDispatcher)
    }
}