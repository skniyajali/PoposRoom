package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.ChargesRepositoryImpl
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.data.repository.validation.ChargesValidationRepository
import com.niyaj.database.dao.ChargesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ChargesModule {

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