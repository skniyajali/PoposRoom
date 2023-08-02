package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.OrderRepositoryImpl
import com.niyaj.data.repository.OrderRepository
import com.niyaj.database.dao.CartOrderDao
import com.niyaj.database.dao.OrderDao
import com.niyaj.database.dao.SelectedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {

    @Provides
    fun provideOrderRepository(
        orderDao: OrderDao,
        cartOrderDao: CartOrderDao,
        selectedDao: SelectedDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): OrderRepository {
        return OrderRepositoryImpl(orderDao, cartOrderDao, selectedDao, ioDispatcher)
    }

}