package com.niyaj.poposroom.features.address.di

import com.niyaj.poposroom.features.address.data.dao.AddressDao
import com.niyaj.poposroom.features.address.data.repository.AddressRepositoryImpl
import com.niyaj.poposroom.features.address.domain.repository.AddressRepository
import com.niyaj.poposroom.features.address.domain.repository.AddressValidationRepository
import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AddressModule {

    @Provides
    fun provideAddressDao(database: PoposDatabase) : AddressDao {
        return database.addressDao()
    }

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