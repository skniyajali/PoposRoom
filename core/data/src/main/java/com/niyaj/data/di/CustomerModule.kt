package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.CustomerRepositoryImpl
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.data.repository.validation.CustomerValidationRepository
import com.niyaj.database.dao.CustomerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CustomerModule {

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