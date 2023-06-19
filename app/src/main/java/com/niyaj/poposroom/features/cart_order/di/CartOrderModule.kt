package com.niyaj.poposroom.features.cart_order.di

import com.niyaj.poposroom.features.address.data.dao.AddressDao
import com.niyaj.poposroom.features.cart_order.data.dao.CartOrderDao
import com.niyaj.poposroom.features.cart_order.data.repository.CartOrderRepositoryImpl
import com.niyaj.poposroom.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.poposroom.features.cart_order.domain.repository.CartOrderValidationRepository
import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.customer.data.dao.CustomerDao
import com.niyaj.poposroom.features.selected.data.dao.SelectedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CartOrderModule {

    @Provides
    fun provideCartOrderDao(database: PoposDatabase) : CartOrderDao {
        return database.cartOrderDao()
    }

    @Provides
    fun provideCartOrderRepository(
        cartOrderDao: CartOrderDao,
        customerDao: CustomerDao,
        addressDao: AddressDao,
        selectedDao: SelectedDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CartOrderRepository {
        return CartOrderRepositoryImpl(cartOrderDao, customerDao, addressDao, selectedDao, ioDispatcher)
    }

    @Provides
    fun provideCartOrderValidationRepository(
        cartOrderDao: CartOrderDao,
        customerDao: CustomerDao,
        addressDao: AddressDao,
        selectedDao: SelectedDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CartOrderValidationRepository {
        return CartOrderRepositoryImpl(cartOrderDao, customerDao, addressDao, selectedDao, ioDispatcher)
    }
}