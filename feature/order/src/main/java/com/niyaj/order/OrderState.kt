package com.niyaj.order

import com.niyaj.model.Order

data class OrderState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
)
