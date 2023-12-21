package com.niyaj.model

import androidx.compose.runtime.Stable
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Stable
data class Address(
    val addressId: Int = 0,

    val addressName: String = "",

    val shortName: String = "",

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long? = null,
)

fun List<Address>.searchAddress(searchText: String): List<Address> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.addressName.contains(searchText, true) ||
                    it.shortName.toString().contains(searchText, true)
        }
    } else this
}
