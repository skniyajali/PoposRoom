package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MeasureUnit(
    val unitId: Int = 0,

    val unitName: String = "",

    val unitValue: Double = 0.5,
)


fun List<MeasureUnit>.searchMeasureUnit(searchText: String): List<MeasureUnit> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.unitName.contains(searchText, true) ||
                    it.unitValue.toString().contains(searchText, true)
        }
    } else this
}