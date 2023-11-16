package com.niyaj.model

data class CartOrderWithAddOnAndCharges(
    val cartOrder: CartOrder,
    val addOnItems: List<Int> = emptyList(),
    val charges: List<Int> = emptyList()
)