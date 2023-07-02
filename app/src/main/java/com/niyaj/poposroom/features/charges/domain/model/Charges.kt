package com.niyaj.poposroom.features.charges.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "charges")
data class Charges(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val chargesId: Int,

    val chargesName: String,

    val chargesPrice: Int,

    val isApplicable: Boolean,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)


fun List<Charges>.searchCharges(searchText: String): List<Charges> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.chargesName.contains(searchText, true) ||
                    it.chargesPrice.toString().contains(searchText, true)
        }
    }else this
}

data class ChargesPriceWithApplicable(var chargesPrice: Int, var isApplicable: Boolean)