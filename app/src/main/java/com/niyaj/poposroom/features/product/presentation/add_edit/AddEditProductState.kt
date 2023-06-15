package com.niyaj.poposroom.features.product.presentation.add_edit

data class AddEditProductState(
    val productName: String = "",
    val productPrice: String = "",
    val productDesc: String = "",
    val productAvailability: Boolean = true,
)
