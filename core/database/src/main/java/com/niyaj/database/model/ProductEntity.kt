package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.niyaj.model.Product
import java.util.Date

@Entity(
    tableName = "product",
    indices = [Index(value = ["productId"])],
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = arrayOf("categoryId"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val productId: Int = 0,

    @ColumnInfo(index = true)
    val categoryId: Int = 0,

    val productName: String = "",

    val productPrice: Int = 0,

    val productDescription: String = "",

    val productAvailability: Boolean = true,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null

)

fun ProductEntity.asExternalModel(): Product {
    return Product(
        productId = this.productId,
        categoryId = this.categoryId,
        productName = this.productName,
        productPrice = this.productPrice,
        productDescription = this.productDescription,
        productAvailability = this.productAvailability,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt?.time
    )
}