package com.niyaj.poposroom.features.address.di

import com.niyaj.poposroom.features.address.dao.AddressDao
import com.niyaj.poposroom.features.address.domain.use_cases.GetAllAddresses
import com.niyaj.poposroom.features.address.domain.validation.AddressValidationRepository
import com.niyaj.poposroom.features.address.domain.validation.AddressValidationRepositoryImpl
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
        addOnItemDao: AddressDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): AddressValidationRepository {
        return AddressValidationRepositoryImpl(addOnItemDao, ioDispatcher)
    }

    @Provides
    fun getAllAddresses(addressDao: AddressDao): GetAllAddresses {
        return GetAllAddresses(addressDao)
    }
}