package com.niyaj.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class CartOrderWithAddOnAndCharges(
    val cartOrder: CartOrder,
    val addOnItems: ImmutableList<Int> = persistentListOf(),
    val charges: ImmutableList<Int> = persistentListOf(),
)