package com.niyaj.poposroom.features.charges.presentation.add_edit

data class AddEditChargesState(
    val chargesName: String = "",
    val chargesPrice: Int = 0,
    val chargesApplicable: Boolean = false,
)
