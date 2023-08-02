package com.niyaj.customer.add_edit

data class AddEditCustomerState(
    val customerPhone: String = "",
    val customerName: String? = null,
    val customerEmail: String? = null,
)
