package com.niyaj.poposroom.features.employee.di

import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.employee.data.dao.EmployeeDao
import com.niyaj.poposroom.features.employee.data.repository.EmployeeRepositoryImpl
import com.niyaj.poposroom.features.employee.domain.repository.EmployeeRepository
import com.niyaj.poposroom.features.employee.domain.repository.EmployeeValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object EmployeeModule {

    @Provides
    fun provideEmployeeDao(database: PoposDatabase) : EmployeeDao {
        return database.employeeDao()
    }

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