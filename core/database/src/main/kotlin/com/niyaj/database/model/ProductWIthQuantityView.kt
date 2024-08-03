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
import androidx.room.DatabaseView
import com.niyaj.model.ProductWithQuantity

@DatabaseView(
    value = """
        WITH selected_order AS (
            SELECT orderId FROM selected LIMIT 1
        )
        SELECT DISTINCT p.categoryId, p.productId, p.productName,
            p.productPrice, COALESCE(c.quantity, 0) as quantity, p.tags
        FROM product p
        LEFT JOIN (
            SELECT productId, quantity
            FROM cart
            WHERE orderId = (SELECT orderId FROM selected_order)
        ) c ON p.productId = c.productId
    """,
    viewName = "product_with_quantity",
)
data class ProductWIthQuantityView(
    val categoryId: Int,

    @ColumnInfo(index = true)
    val productId: Int,

    @ColumnInfo(index = true)
    val productName: String,

    val productPrice: Int,

    val quantity: Int = 0,

    val tags: List<String> = emptyList(),
)

fun List<ProductWIthQuantityView>.asExternalModel(): List<ProductWithQuantity> {
    return this.map {
        ProductWithQuantity(
            categoryId = it.categoryId,
            productId = it.productId,
            productName = it.productName,
            productPrice = it.productPrice,
            quantity = it.quantity,
            tags = it.tags,
        )
    }
}
