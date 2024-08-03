/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
            onUpdate = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = arrayOf("categoryId"),
            childColumns = arrayOf("categoryId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
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
        associateBy = Junction(CategoryWithProductCrossRef::class),
    )
    val products: List<ProductEntity> = emptyList(),
)

fun CategoryWithProductsDto.asExternalModel(): CategoryWithProducts {
    return CategoryWithProducts(
        category = this.category.asExternalModel(),
        products = this.products.map { it.asExternalModel() }.toImmutableList(),
    )
}
