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

import com.niyaj.model.ChargesNameAndPrice
import com.niyaj.model.DeliveryReport
import com.niyaj.model.OrderDetails
import com.niyaj.model.Profile
import kotlinx.coroutines.flow.Flow

interface PrintRepository {

    suspend fun getOrderDetails(orderId: Int): OrderDetails

    suspend fun getDeliveryReports(date: String, partnerId: Int? = null): List<DeliveryReport>

    suspend fun getCharges(): List<ChargesNameAndPrice>

    fun getProfileInfo(restaurantId: Int): Flow<Profile>
}
