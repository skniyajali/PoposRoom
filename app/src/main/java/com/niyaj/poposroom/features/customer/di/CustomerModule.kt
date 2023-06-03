package com.niyaj.poposroom.features.customer.di

import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.customer.dao.CustomerDao
import com.niyaj.poposroom.features.customer.domain.use_cases.GetAllCustomers
import com.niyaj.poposroom.features.customer.domain.validation.CustomerValidationRepository
import com.niyaj.poposroom.features.customer.domain.validation.CustomerValidationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CustomerModule {

    @Provides
    fun provideCustomerDao(database: PoposDatabase) : CustomerDao {
        return database.customerDao()
    }

    @Provides
    fun provideCustomerValidationRepository(
        addOnItemDao: CustomerDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CustomerValidationRepository {
        return CustomerValidationRepositoryImpl(addOnItemDao, ioDispatcher)
    }

    @Provides
    fun getAllCustomer(customerDao: CustomerDao): GetAllCustomers {
        return GetAllCustomers(customerDao)
    }
}