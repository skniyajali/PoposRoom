package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.AddOnItem
import java.util.Date

@Entity(tableName = "addonitem")
data class AddOnItemEntity(
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


fun AddOnItemEntity.asExternalModel(): AddOnItem {
    return AddOnItem(
        itemId = this.itemId,
        itemName = this.itemName,
        itemPrice = this.itemPrice,
        isApplicable = this.isApplicable,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}