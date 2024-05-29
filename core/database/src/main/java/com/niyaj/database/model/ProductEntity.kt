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
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.niyaj.model.Product
import java.util.Date

@Entity(
    tableName = "product",
    indices = [Index(value = ["productId"])],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = arrayOf("categoryId"),
            childColumns = arrayOf("categoryId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
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
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,

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
        updatedAt = this.updatedAt?.time,
    )
}
