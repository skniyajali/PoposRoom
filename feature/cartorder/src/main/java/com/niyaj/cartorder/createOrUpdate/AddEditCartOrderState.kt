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

package com.niyaj.cartorder.createOrUpdate

import androidx.compose.runtime.mutableStateListOf
import com.niyaj.model.Address
import com.niyaj.model.Customer
import com.niyaj.model.OrderType

data class AddEditCartOrderState(
    val orderType: OrderType = OrderType.DineIn,
    val doesChargesIncluded: Boolean = false,
    val address: Address = Address(),
    val customer: Customer = Customer(),
    val deliveryPartnerId: Int = 0,
    val selectedAddOnItems: MutableList<Int> = mutableStateListOf(),
    val selectedCharges: MutableList<Int> = mutableStateListOf(),
)
