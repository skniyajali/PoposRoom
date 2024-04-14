package com.niyaj.print

sealed interface PrintEvent {

    data class PrintOrder(val orderId: Int): PrintEvent

    data class PrintOrders(val orderIds: List<Int>): PrintEvent

    data class PrintDeliveryReport(val date: String): PrintEvent

    data class PrintAllExpenses(val date: String): PrintEvent
}