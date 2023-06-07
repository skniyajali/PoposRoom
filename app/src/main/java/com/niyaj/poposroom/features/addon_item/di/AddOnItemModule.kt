package com.niyaj.poposroom.features.addon_item.di

import com.niyaj.poposroom.features.addon_item.data.dao.AddOnItemDao
import com.niyaj.poposroom.features.addon_item.data.repository.AddOnItemRepositoryImpl
import com.niyaj.poposroom.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.poposroom.features.addon_item.domain.repository.AddOnItemValidationRepository
import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AddOnItemModule {

    @Provides
    fun provideAddOnItemDao(database: PoposDatabase) : AddOnItemDao {
        return database.addOnItemDao()
    }

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