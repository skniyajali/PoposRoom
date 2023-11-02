package com.niyaj.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.niyaj.database.util.ListConverter
import com.niyaj.model.MarketList

@Entity(tableName = "market_list")
data class MarketListEntity(

    @PrimaryKey(autoGenerate = true)
    val marketId: Int = 0,

    val marketDate: Long,

    val createdAt: Long,

    val updatedAt: Long? = null,

    @TypeConverters(ListConverter::class)
    val whitelistItems: List<Int> = emptyList(),
)


fun MarketListEntity.asExternalModel(): MarketList {
    return MarketList(
        marketId = marketId,
        marketDate = marketDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
        whitelistItems = whitelistItems,
    )
}