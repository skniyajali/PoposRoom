package com.niyaj.model

import java.util.Date

data class Charges(
    val chargesId: Int,

    val chargesName: String,

    val chargesPrice: Int,

    val isApplicable: Boolean,

    val createdAt: Date,

    val updatedAt: Date? = null,
)


fun List<Charges>.searchCharges(searchText: String): List<Charges> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.chargesName.contains(searchText, true) ||
                    it.chargesPrice.toString().contains(searchText, true)
        }
    } else this
}

data class ChargesPriceWithApplicable(val chargesPrice: Int, val isApplicable: Boolean)