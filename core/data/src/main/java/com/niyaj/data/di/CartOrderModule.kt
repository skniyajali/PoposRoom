package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.CartOrderRepositoryImpl
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.validation.CartOrderValidationRepository
import com.niyaj.database.dao.AddressDao
import com.niyaj.database.dao.CartDao
import com.niyaj.database.dao.CartOrderDao
import com.niyaj.database.dao.CartPriceDao
import com.niyaj.database.dao.CustomerDao
import com.niyaj.database.dao.SelectedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CartOrderModule {

    @Provides
    fun provideCartOrderRepository(
        cartDao: CartDao,
        cartOrderDao: CartOrderDao,
        customerDao: CustomerDao,
        addressDao: AddressDao,
        selectedDao: SelectedDao,
        cartPriceDao: CartPriceDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CartOrderRepository {
        return CartOrderRepositoryImpl(cartDao,cartOrderDao, customerDao, addressDao, selectedDao, cartPriceDao, ioDispatcher)
    }

    @Provides
    fun provideCartOrderValidationRepository(
        cartDao: CartDao,
        cartOrderDao: CartOrderDao,
        customerDao: CustomerDao,
        addressDao: AddressDao,
        selectedDao: SelectedDao,
        cartPriceDao: CartPriceDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CartOrderValidationRepository {
        return CartOrderRepositoryImpl(cartDao, cartOrderDao, customerDao, addressDao, selectedDao, cartPriceDao, ioDispatcher)
    }
}