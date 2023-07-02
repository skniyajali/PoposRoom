package com.niyaj.poposroom.features.order.presentation

import com.niyaj.poposroom.features.order.domain.model.Order

data class OrderState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = true,
)
