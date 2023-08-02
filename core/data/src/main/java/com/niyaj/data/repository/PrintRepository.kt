package com.niyaj.data.repository

import com.niyaj.model.OrderDetails
import kotlinx.coroutines.flow.Flow

interface PrintRepository {

    suspend fun getOrderDetail(orderId: Int): OrderDetails

    suspend fun getOrderDetails(orderIds: List<Int>): Flow<List<OrderDetails>>
}