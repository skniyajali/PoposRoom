package com.niyaj.poposroom.features.addon_item.di

import com.niyaj.poposroom.features.addon_item.dao.AddOnItemDao
import com.niyaj.poposroom.features.addon_item.domain.use_cases.GetAllAddOnItems
import com.niyaj.poposroom.features.addon_item.domain.validation.AddOnItemValidationRepository
import com.niyaj.poposroom.features.addon_item.domain.validation.AddOnItemValidationRepositoryImpl
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

//    @Binds
//    fun bindsAddOnValidationRepository(
//        validationRepositoryImpl: AddOnItemValidationRepositoryImpl
//    ): AddOnItemValidationRepository

    @Provides
    fun provideAddOnItemValidationRepository(
        addOnItemDao: AddOnItemDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): AddOnItemValidationRepository {
        return AddOnItemValidationRepositoryImpl(addOnItemDao, ioDispatcher)
    }

    @Provides
    fun getAllAddOnItems(addOnItemDao: AddOnItemDao): GetAllAddOnItems {
        return GetAllAddOnItems(addOnItemDao)
    }
}