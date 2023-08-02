package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.Charges
import java.util.Date

@Entity(tableName = "charges")
data class ChargesEntity(
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

fun ChargesEntity.asExternalModel(): Charges {
    return Charges(
        chargesId = this.chargesId,
        chargesName = this.chargesName,
        chargesPrice = this.chargesPrice,
        isApplicable = this.isApplicable,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}