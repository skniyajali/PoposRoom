package com.niyaj.poposroom.features.category.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int,

    val categoryName: String,

    val isAvailable: Boolean,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)


fun List<Category>.searchCategory(searchText: String): List<Category> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.categoryName.contains(searchText, true)
        }
    }else this
}