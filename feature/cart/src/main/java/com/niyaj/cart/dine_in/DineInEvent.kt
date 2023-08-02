package com.niyaj.cart.dine_in

sealed class DineInEvent {

    data class SelectDineInOrder(val orderId: Int) : DineInEvent()

    data object SelectAllDineInOrder : DineInEvent()

    data class IncreaseQuantity(val orderId: Int, val productId: Int) : DineInEvent()

    data class DecreaseQuantity(val orderId: Int, val productId: Int) : DineInEvent()

    data class UpdateAddOnItemInCart(val itemId: Int, val orderId: Int) : DineInEvent()

    data class PlaceDineInOrder(val orderId: Int) : DineInEvent()

    data object PlaceAllDineInOrder : DineInEvent()

    data object RefreshDineInOrder : DineInEvent()
}
