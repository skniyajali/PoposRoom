package com.niyaj.poposroom.features.charges.di

import com.niyaj.poposroom.features.charges.dao.ChargesDao
import com.niyaj.poposroom.features.charges.domain.use_cases.GetAllCharges
import com.niyaj.poposroom.features.charges.domain.validation.ChargesValidationRepository
import com.niyaj.poposroom.features.charges.domain.validation.ChargesValidationRepositoryImpl
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
object ChargesModule {

    @Provides
    fun provideChargesDao(database: PoposDatabase) : ChargesDao {
        return database.chargesDao()
    }

    @Provides
    fun provideChargesValidationRepository(
        addOnItemDao: ChargesDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): ChargesValidationRepository {
        return ChargesValidationRepositoryImpl(addOnItemDao, ioDispatcher)
    }

    @Provides
    fun getAllCharges(addOnItemDao: ChargesDao): GetAllCharges {
        return GetAllCharges(addOnItemDao)
    }
}