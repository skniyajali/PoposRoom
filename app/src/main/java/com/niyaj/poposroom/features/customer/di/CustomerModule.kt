package com.niyaj.poposroom.features.customer.di

import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.customer.data.dao.CustomerDao
import com.niyaj.poposroom.features.customer.data.repository.CustomerRepositoryImpl
import com.niyaj.poposroom.features.customer.domain.repository.CustomerRepository
import com.niyaj.poposroom.features.customer.domain.repository.CustomerValidationRepository
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
        customerDao: CustomerDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CustomerValidationRepository {
        return CustomerRepositoryImpl(customerDao, ioDispatcher)
    }

    @Provides
    fun provideCustomerRepository(
        customerDao: CustomerDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CustomerRepository {
        return CustomerRepositoryImpl(customerDao, ioDispatcher)
    }

}