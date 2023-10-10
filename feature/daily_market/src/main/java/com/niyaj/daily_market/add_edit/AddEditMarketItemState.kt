package com.niyaj.daily_market.add_edit

data class AddEditMarketItemState(
    val itemType: String = "",

    val itemName: String = "",

    val itemMeasureUnit: String = "",

    val itemPrice: String? = null,

    val itemDesc: String? = null,
)
