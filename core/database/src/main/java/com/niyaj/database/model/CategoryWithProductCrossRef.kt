package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.model.CategoryWithProducts
import kotlinx.collections.immutable.toImmutableList


@Entity(
    primaryKeys = ["productId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = arrayOf("productId"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = arrayOf("categoryId"),
            childColumns = arrayOf("categoryId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class CategoryWithProductCrossRef(
    @ColumnInfo(index = true)
    val categoryId: Int,

    @ColumnInfo(index = true)
    val productId: Int,
)


data class CategoryWithProductsDto(
    @Embedded
    val category: CategoryEntity,

    @Relation(
        parentColumn = "categoryId",
        entity = ProductEntity::class,
        entityColumn = "productId",
        associateBy = Junction(CategoryWithProductCrossRef::class)
    )
    val products: List<ProductEntity> = emptyList(),
)

fun CategoryWithProductsDto.asExternalModel(): CategoryWithProducts {
    return CategoryWithProducts(
        category = this.category.asExternalModel(),
        products = this.products.map { it.asExternalModel() }.toImmutableList()
    )
}