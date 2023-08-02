package com.niyaj.model

import java.util.Date

data class Address(
    val addressId: Int = 0,

    val addressName: String = "",

    val shortName: String = "",

    val createdAt: Date = Date(),

    val updatedAt: Date? = null,
)

fun List<Address>.searchAddress(searchText: String): List<Address> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.addressName.contains(searchText, true) ||
                    it.shortName.toString().contains(searchText, true)
        }
    } else this
}
