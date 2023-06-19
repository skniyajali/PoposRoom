package com.niyaj.poposroom.features.selected.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrderEntity
import com.niyaj.poposroom.features.selected.domain.utils.SelectedTestTag.SELECTED_ID

@Entity(
    tableName = "selected",
    foreignKeys = [
        ForeignKey(
            entity = CartOrderEntity::class,
            parentColumns = arrayOf("cartOrderId"),
            childColumns = arrayOf("cartOrderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class Selected(
    @PrimaryKey
    @ColumnInfo(index = true)
    val selectedId: String = SELECTED_ID,

    @ColumnInfo(index = true)
    val cartOrderId: Int = 0,
)
