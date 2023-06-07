package com.niyaj.poposroom.features.charges.di

import com.niyaj.poposroom.features.charges.data.dao.ChargesDao
import com.niyaj.poposroom.features.charges.data.repository.ChargesRepositoryImpl
import com.niyaj.poposroom.features.charges.domain.repository.ChargesRepository
import com.niyaj.poposroom.features.charges.domain.repository.ChargesValidationRepository
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
        chargesDao: ChargesDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): ChargesValidationRepository {
        return ChargesRepositoryImpl(chargesDao, ioDispatcher)
    }

    @Provides
    fun provideChargesRepository(
        chargesDao: ChargesDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): ChargesRepository {
        return ChargesRepositoryImpl(chargesDao, ioDispatcher)
    }
}