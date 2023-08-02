package com.niyaj.charges.add_edit

data class AddEditChargesState(
    val chargesName: String = "",
    val chargesPrice: Int = 0,
    val chargesApplicable: Boolean = false,
)
