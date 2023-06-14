package com.niyaj.poposroom.features.product.domain.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.poposroom.features.category.domain.model.Category


@Entity(
    primaryKeys = ["productId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = arrayOf("productId"),
            childColumns = arrayOf("productId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Category::class,
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
    val productId: Int
)


data class CategoryWithProduct(
    @Embedded
    val category: Category,

    @Relation(
        parentColumn = "categoryId",
        entity = Product::class,
        entityColumn = "productId",
        associateBy = Junction(CategoryWithProductCrossRef::class)
    )
    val products: List<Product> = emptyList()
)