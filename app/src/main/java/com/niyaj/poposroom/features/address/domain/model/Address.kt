package com.niyaj.poposroom.features.address.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Address(
    @PrimaryKey(autoGenerate = true)
    val addressId: Int,

    val addressName: String,

    val shortName: String,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)


fun List<Address>.searchAddress(searchText: String): List<Address> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.addressName.contains(searchText, true) ||
                    it.shortName.toString().contains(searchText, true)
        }
    }else this
}
