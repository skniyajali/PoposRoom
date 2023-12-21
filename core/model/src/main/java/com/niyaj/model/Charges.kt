package com.niyaj.model

import androidx.compose.runtime.Stable
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Stable
data class Charges(
    val chargesId: Int,

    val chargesName: String,

    val chargesPrice: Int,

    val isApplicable: Boolean,

    val createdAt: Long,

    val updatedAt: Long? = null,
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