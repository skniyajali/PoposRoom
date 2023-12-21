package com.niyaj.model

import androidx.compose.runtime.Stable

const val SELECTED_ID = "33333333"

@Stable
data class Selected(
    val selectedId: String = SELECTED_ID,

    val orderId: Int = 0,
)
