package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.AbsentRepositoryImpl
import com.niyaj.data.repository.AbsentRepository
import com.niyaj.data.repository.validation.AbsentValidationRepository
import com.niyaj.database.dao.AbsentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AbsentModule {

    @Provides
    fun provideAbsentValidationRepository(
        absentDao: AbsentDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): AbsentValidationRepository {
        return AbsentRepositoryImpl(absentDao, ioDispatcher)
    }

    @Provides
    fun provideAbsentRepository(
        absentDao: AbsentDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): AbsentRepository {
        return AbsentRepositoryImpl(absentDao, ioDispatcher)
    }
}