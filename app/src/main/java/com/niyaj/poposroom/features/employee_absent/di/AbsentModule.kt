package com.niyaj.poposroom.features.employee_absent.di

import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.employee_absent.data.dao.AbsentDao
import com.niyaj.poposroom.features.employee_absent.data.repository.AbsentRepositoryImpl
import com.niyaj.poposroom.features.employee_absent.domain.repository.AbsentRepository
import com.niyaj.poposroom.features.employee_absent.domain.repository.AbsentValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AbsentModule {

    @Provides
    fun provideAbsentDao(database: PoposDatabase) : AbsentDao {
        return database.absentDao()
    }

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