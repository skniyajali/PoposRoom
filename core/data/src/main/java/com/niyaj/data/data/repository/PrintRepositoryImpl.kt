package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.repository.PrintRepository
import com.niyaj.database.dao.CartOrderDao
import com.niyaj.database.dao.PrintDao
import com.niyaj.model.OrderDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class PrintRepositoryImpl(
    private val printDao: PrintDao,
    private val cartOrderDao: CartOrderDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : PrintRepository {

    override suspend fun getOrderDetail(orderId: Int): OrderDetails {
        TODO("Not yet implemented")
    }

    override suspend fun getOrderDetails(orderIds: List<Int>): Flow<List<OrderDetails>> {
        TODO("Not yet implemented")
    }
}