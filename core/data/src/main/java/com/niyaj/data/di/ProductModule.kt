package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.ProductRepositoryImpl
import com.niyaj.data.repository.ProductRepository
import com.niyaj.data.repository.validation.ProductValidationRepository
import com.niyaj.database.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ProductModule {

    @Provides
    fun provideProductValidationRepository(
        productDao: ProductDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): ProductValidationRepository {
        return ProductRepositoryImpl(productDao, ioDispatcher)
    }

    @Provides
    fun provideProductRepository(
        productDao: ProductDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): ProductRepository {
        return ProductRepositoryImpl(productDao, ioDispatcher)
    }
}