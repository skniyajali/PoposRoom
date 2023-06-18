package com.niyaj.poposroom.features.cart_order.presentation.add_edit

import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.cart_order.domain.utils.CartOrderType
import com.niyaj.poposroom.features.customer.domain.model.Customer

sealed interface AddEditCartOrderEvent {

    data class OrderTypeChanged(val orderType: CartOrderType): AddEditCartOrderEvent

    data class AddressNameChanged(val addressName: String): AddEditCartOrderEvent

    data class AddressChanged(val address: Address): AddEditCartOrderEvent

    data class CustomerPhoneChanged(val customerPhone: String): AddEditCartOrderEvent

    data class CustomerChanged(val customer: Customer): AddEditCartOrderEvent

    object DoesChargesIncluded: AddEditCartOrderEvent

    data class CreateOrUpdateCartOrder(val cartOrderId: Int = 0): AddEditCartOrderEvent
}