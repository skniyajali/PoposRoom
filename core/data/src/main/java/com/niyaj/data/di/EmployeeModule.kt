package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.EmployeeRepositoryImpl
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.data.repository.EmployeeValidationRepository
import com.niyaj.database.dao.EmployeeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object EmployeeModule {

    @Provides
    fun provideEmployeeValidationRepository(
        employeeDao: EmployeeDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): EmployeeValidationRepository {
        return EmployeeRepositoryImpl(employeeDao, ioDispatcher)
    }

    @Provides
    fun provideEmployeeRepository(
        employeeDao: EmployeeDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): EmployeeRepository {
        return EmployeeRepositoryImpl(employeeDao, ioDispatcher)
    }
}