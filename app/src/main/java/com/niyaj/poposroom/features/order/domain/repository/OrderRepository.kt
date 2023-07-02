package com.niyaj.poposroom.features.order.domain.repository

import com.niyaj.poposroom.features.charges.domain.model.Charges
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.order.domain.model.Order
import com.niyaj.poposroom.features.order.domain.model.OrderDetails
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun getAllOrders(date: String, searchText: String): Flow<List<Order>>

    suspend fun getAllCharges(): Flow<List<Charges>>

    suspend fun getOrderDetails(orderId: Int): Flow<OrderDetails>

    suspend fun deleteOrder(orderId: Int): Resource<Boolean>

    suspend fun markOrderAsProcessing(orderId: Int): Resource<Boolean>
}