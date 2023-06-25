package com.niyaj.poposroom.features.cart.presentation.dine_out

sealed class DineOutEvent {

    data class SelectDineOutOrder(val orderId: Int): DineOutEvent()

    object SelectAllDineOutOrder: DineOutEvent()

    data class IncreaseQuantity(val orderId: Int, val productId: Int): DineOutEvent()

    data class DecreaseQuantity(val orderId: Int, val productId: Int): DineOutEvent()

    data class UpdateAddOnItemInCart(val itemId: Int, val orderId: Int): DineOutEvent()

    data class PlaceDineOutOrder(val orderId: Int): DineOutEvent()

    object PlaceAllDineOutOrder: DineOutEvent()

    object RefreshDineOutOrder: DineOutEvent()
}
