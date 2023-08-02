package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.niyaj.model.Selected

@Entity(
    tableName = "selected",
    foreignKeys = [
        ForeignKey(
            entity = CartOrderEntity::class,
            parentColumns = arrayOf("orderId"),
            childColumns = arrayOf("orderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class SelectedEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val selectedId: String,

    @ColumnInfo(index = true)
    val orderId: Int,
)


fun SelectedEntity.asExternalModel(): Selected {
    return Selected(
        selectedId = this.selectedId,
        orderId = this.orderId
    )
}