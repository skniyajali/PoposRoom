/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Charges
import com.niyaj.model.DeliveryReport
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.Order
import com.niyaj.model.OrderDetails
import com.niyaj.model.TotalDeliveryPartnerOrder
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun getDineInOrders(date: String, searchText: String): Flow<List<Order>>

    suspend fun getDineOutOrders(date: String, searchText: String): Flow<List<Order>>

    suspend fun getDeliveryPartners(): Flow<List<EmployeeNameAndId>>

    suspend fun updateDeliveryPartner(orderId: Int, deliveryPartnerId: Int): Resource<Boolean>

    suspend fun getDeliveryPartnerOrders(date: String): Flow<List<TotalDeliveryPartnerOrder>>

    suspend fun getPartnerDeliveryReports(
        date: String,
        partnerId: Int? = null,
        searchText: String,
    ): Flow<List<DeliveryReport>>

    suspend fun getAllCharges(): Flow<List<Charges>>

    suspend fun getOrderDetails(orderId: Int): Flow<OrderDetails>

    suspend fun deleteOrder(orderId: Int): Resource<Boolean>

    suspend fun markOrderAsProcessing(orderId: Int): Resource<Boolean>
}
