package com.niyaj.poposroom.features.product.di

import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.product.data.dao.ProductDao
import com.niyaj.poposroom.features.product.data.repository.ProductRepositoryImpl
import com.niyaj.poposroom.features.product.domain.repository.ProductRepository
import com.niyaj.poposroom.features.product.domain.repository.ProductValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ProductModule {

    @Provides
    fun provideProductDao(database: PoposDatabase) : ProductDao {
        return database.productDao()
    }

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