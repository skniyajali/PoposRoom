package com.niyaj.address.add_edit

data class AddEditAddressState(
    val shortName: String = "",
    val shortNameError: String? = null,

    val addressName: String = "",
    val addressNameError: String? = null
)
