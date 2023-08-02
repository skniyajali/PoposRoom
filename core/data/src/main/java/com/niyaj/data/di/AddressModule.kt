package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.AddressRepositoryImpl
import com.niyaj.data.repository.AddressRepository
import com.niyaj.data.repository.validation.AddressValidationRepository
import com.niyaj.database.dao.AddressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AddressModule {

    @Provides
    fun provideAddressValidationRepository(
        addressDao: AddressDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): AddressValidationRepository {
        return AddressRepositoryImpl(addressDao, ioDispatcher)
    }

    @Provides
    fun provideAddressRepository(
        addressDao: AddressDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): AddressRepository {
        return AddressRepositoryImpl(addressDao, ioDispatcher)
    }
}