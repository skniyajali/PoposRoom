package com.niyaj.cartorder.add_edit

import com.niyaj.model.Address
import com.niyaj.model.Customer
import com.niyaj.model.OrderType

sealed interface AddEditCartOrderEvent {

    data class OrderTypeChanged(val orderType: OrderType): AddEditCartOrderEvent

    data class AddressNameChanged(val addressName: String): AddEditCartOrderEvent

    data class AddressChanged(val address: Address): AddEditCartOrderEvent

    data class CustomerPhoneChanged(val customerPhone: String): AddEditCartOrderEvent

    data class CustomerChanged(val customer: Customer): AddEditCartOrderEvent

    data object DoesChargesIncluded: AddEditCartOrderEvent

    data class SelectAddOnItem(val itemId: Int): AddEditCartOrderEvent

    data class SelectCharges(val chargesId: Int): AddEditCartOrderEvent

    data class CreateOrUpdateCartOrder(val cartOrderId: Int = 0): AddEditCartOrderEvent
}