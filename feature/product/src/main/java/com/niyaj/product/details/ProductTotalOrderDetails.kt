package com.niyaj.product.details

data class ProductTotalOrderDetails(
    val totalAmount: String = "0",
    val dineInAmount: String = "0",
    val dineInQty: Int = 0,
    val dineOutAmount: String = "0",
    val dineOutQty: Int = 0,
    val mostOrderItemDate: String = "",
    val mostOrderQtyDate: String = "",
    val datePeriod: Pair<String, String> = Pair("", "")
)