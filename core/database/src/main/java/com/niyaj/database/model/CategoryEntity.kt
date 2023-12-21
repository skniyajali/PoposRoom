package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.niyaj.model.Category
import java.util.Date

@Entity(tableName = "category", indices = [Index(value = ["categoryId"])])
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,

    val categoryName: String,

    val isAvailable: Boolean,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun CategoryEntity.asExternalModel(): Category {
    return Category(
        categoryId = this.categoryId,
        categoryName = this.categoryName,
        isAvailable = this.isAvailable,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt?.time
    )
}