package com.niyaj.daily_market.add_edit

import com.niyaj.model.MeasureUnit

data class AddEditMarketItemState(
    val itemType: String = "",

    val itemName: String = "",

    val itemMeasureUnit: MeasureUnit = MeasureUnit(),

    val itemPrice: String? = null,

    val itemDesc: String? = null,
)
