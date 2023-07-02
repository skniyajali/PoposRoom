package com.niyaj.poposroom.features.order.presentation

sealed class OrderEvent {

    data class DeleteOrder(val orderId: Int) : OrderEvent()

    data class MarkedAsProcessing(val orderId: Int): OrderEvent()

    data class SelectDate(val date: String): OrderEvent()

    object PrintDeliveryReport : OrderEvent()
}