package com.niyaj.poposroom.features.addon_item.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "addonitem")
data class AddOnItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val itemId: Int,

    val itemName: String,

    val itemPrice: Int,

    val isApplicable: Boolean,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)


fun List<AddOnItem>.searchAddOnItem(searchText: String): List<AddOnItem> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.itemName.contains(searchText, true) ||
                    it.itemPrice.toString().contains(searchText, true)
        }
    }else this
}

data class AddOnPriceWithApplicable(var itemPrice: Int, var isApplicable: Boolean)