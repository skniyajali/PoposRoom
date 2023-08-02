package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.AddOnItemRepositoryImpl
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.data.repository.validation.AddOnItemValidationRepository
import com.niyaj.database.dao.AddOnItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AddOnItemModule {

    @Provides
    fun provideAddOnItemValidationRepository(
        addOnItemDao: AddOnItemDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): AddOnItemValidationRepository {
        return AddOnItemRepositoryImpl(addOnItemDao, ioDispatcher)
    }

    @Provides
    fun provideAddOnItemRepository(
        addOnItemDao: AddOnItemDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): AddOnItemRepository {
        return AddOnItemRepositoryImpl(addOnItemDao, ioDispatcher)
    }
}