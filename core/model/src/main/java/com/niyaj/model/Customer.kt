package com.niyaj.model

import androidx.compose.runtime.Stable
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Stable
data class Customer(
    val customerId: Int = 0,

    val customerPhone: String = "",

    val customerName: String? = null,

    val customerEmail: String? = null,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long? = null,
)


fun List<Customer>.searchCustomer(searchText: String): List<Customer> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.customerPhone.contains(searchText, true) ||
                    it.customerEmail.toString().contains(searchText, true) ||
                    it.customerName.toString().contains(searchText, true)
        }
    } else this
}