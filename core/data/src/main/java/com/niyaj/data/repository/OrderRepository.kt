package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Charges
import com.niyaj.model.Order
import com.niyaj.model.OrderDetails
import com.niyaj.model.OrderType
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun getAllOrders(date: String, searchText: String): Flow<List<Order>>

    suspend fun getAllOrders(date: String, orderType: OrderType, searchText: String): Flow<List<Order>>

    suspend fun getAllCharges(): Flow<List<Charges>>

    suspend fun getOrderDetails(orderId: Int): Flow<OrderDetails>

    suspend fun deleteOrder(orderId: Int): Resource<Boolean>

    suspend fun markOrderAsProcessing(orderId: Int): Resource<Boolean>
}