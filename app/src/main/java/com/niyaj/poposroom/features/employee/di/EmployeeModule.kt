package com.niyaj.poposroom.features.employee.di

import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.employee.dao.EmployeeDao
import com.niyaj.poposroom.features.employee.domain.use_cases.GetAllEmployee
import com.niyaj.poposroom.features.employee.domain.validation.EmployeeValidationRepository
import com.niyaj.poposroom.features.employee.domain.validation.EmployeeValidationRepositoryImpl
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
        addOnItemDao: EmployeeDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): EmployeeValidationRepository {
        return EmployeeValidationRepositoryImpl(addOnItemDao, ioDispatcher)
    }

    @Provides
    fun getAllEmployee(employeeDao: EmployeeDao): GetAllEmployee {
        return GetAllEmployee(employeeDao)
    }
}