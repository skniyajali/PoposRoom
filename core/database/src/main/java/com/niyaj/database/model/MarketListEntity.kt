package com.niyaj.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.MarketList

@Entity(tableName = "market_list")
data class MarketListEntity(

    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,

    val itemType: String,

    val itemName: String,

    val itemPrice: String? = null,

    val itemDescription: String? = null,

    val itemMeasureUnit: String,

    val createdAt: Long,

    val updatedAt: Long? = null
)


fun MarketListEntity.asExternalModel(): MarketList {
    return MarketList(
        itemId = itemId,
        itemType = itemType,
        itemName = itemName,
        itemPrice = itemPrice,
        itemDescription = itemDescription,
        itemMeasureUnit = itemMeasureUnit,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}