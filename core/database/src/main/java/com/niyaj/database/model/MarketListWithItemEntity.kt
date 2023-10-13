package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.niyaj.model.MarketListType
import com.niyaj.model.MarketListWithItem
import com.niyaj.model.MarketListWithItems

@Entity(
    tableName = "market_list_with_item",
    foreignKeys = [
        ForeignKey(
            entity = MarketListEntity::class,
            parentColumns = arrayOf("marketId"),
            childColumns = arrayOf("marketId"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MarketItemEntity::class,
            parentColumns = arrayOf("itemId"),
            childColumns = arrayOf("itemId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MarketListWithItemEntity(
    @PrimaryKey(autoGenerate = true)
    val listId: Int = 0,

    @ColumnInfo(index = true)
    val marketId: Int,

    @ColumnInfo(index = true)
    val itemId: Int,

    val itemQuantity: Double,

    val marketListType: MarketListType,
)


data class MarketListWithItemsDto(
    @Embedded
    val marketList: MarketListEntity,

    @Relation(
        parentColumn = "marketId",
        entityColumn = "marketId",
        entity = MarketListWithItemEntity::class
    )
    val marketItems: List<MarketListWithItemEntity>,
)

fun MarketListWithItemEntity.asExternalModel(): MarketListWithItem {
    return MarketListWithItem(
        listId = listId,
        marketId = marketId,
        itemId = itemId,
        itemQuantity = itemQuantity,
        marketListType = marketListType
    )
}


fun MarketListWithItemsDto.asExternalModel(): MarketListWithItems {
    return MarketListWithItems(
        marketList = this.marketList.asExternalModel(),
        items = this.marketItems.map { it.asExternalModel() }
    )
}
