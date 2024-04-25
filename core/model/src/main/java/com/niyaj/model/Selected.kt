package com.niyaj.model

const val SELECTED_ID = "33333333"

data class Selected(
    val selectedId: String = SELECTED_ID,

    val orderId: Int = 0,
)
