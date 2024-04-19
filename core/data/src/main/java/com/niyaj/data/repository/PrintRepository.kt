package com.niyaj.data.repository

import com.niyaj.model.ChargesNameAndPrice
import com.niyaj.model.DeliveryReport
import com.niyaj.model.OrderDetails
import com.niyaj.model.Profile
import kotlinx.coroutines.flow.Flow

interface PrintRepository {

    suspend fun getOrderDetails(orderId: Int): OrderDetails

    suspend fun getDeliveryReports(date: String): List<DeliveryReport>

    suspend fun getCharges(): List<ChargesNameAndPrice>

    fun getProfileInfo(restaurantId: Int): Flow<Profile>
}