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

import com.niyaj.model.Address
import com.niyaj.model.Customer
import com.niyaj.model.OrderType

sealed interface AddEditCartOrderEvent {

    data class OrderTypeChanged(val orderType: OrderType) : AddEditCartOrderEvent

    data class AddressNameChanged(val addressName: String) : AddEditCartOrderEvent

    data class AddressChanged(val address: Address) : AddEditCartOrderEvent

    data class CustomerPhoneChanged(val customerPhone: String) : AddEditCartOrderEvent

    data class CustomerChanged(val customer: Customer) : AddEditCartOrderEvent

    data object DoesChargesIncluded : AddEditCartOrderEvent

    data class SelectAddOnItem(val itemId: Int) : AddEditCartOrderEvent

    data class SelectCharges(val chargesId: Int) : AddEditCartOrderEvent

    data class SelectDeliveryPartner(val partnerId: Int): AddEditCartOrderEvent

    data object CreateOrUpdateCartOrder : AddEditCartOrderEvent
}
