package com.niyaj.poposroom.features.order.di

import com.niyaj.poposroom.features.cart_order.data.dao.CartOrderDao
import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.order.data.dao.OrderDao
import com.niyaj.poposroom.features.order.data.repository.OrderRepositoryImpl
import com.niyaj.poposroom.features.order.domain.repository.OrderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {

    @Provides
    fun provideOrderDao(database: PoposDatabase) : OrderDao {
        return database.orderDao()
    }

    @Provides
    fun provideOrderRepository(
        orderDao: OrderDao,
        cartOrderDao: CartOrderDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): OrderRepository {
        return OrderRepositoryImpl(orderDao, cartOrderDao, ioDispatcher)
    }

}