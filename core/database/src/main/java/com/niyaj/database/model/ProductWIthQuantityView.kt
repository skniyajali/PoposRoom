/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.database.model

import androidx.room.DatabaseView
import com.niyaj.model.ProductWithQuantity

@DatabaseView(
    value = """
        SELECT product.categoryId, product.productId, productName, productPrice, COALESCE(cart.quantity, 0) as quantity
        FROM product
        LEFT JOIN cart ON product.productId = cart.productId 
        AND CASE WHEN (SELECT orderId FROM selected LIMIT 1) IS NOT NULL
        THEN cart.orderId = COALESCE((SELECT orderId FROM selected LIMIT 1), 0) ELSE 1 END
    """,
    viewName = "product_with_quantity",
)
data class ProductWIthQuantityView(
    val categoryId: Int,
    val productId: Int,
    val productName: String,
    val productPrice: Int,
    val quantity: Int = 0,
)

fun List<ProductWIthQuantityView>.asExternalModel(): List<ProductWithQuantity> {
    return this.map {
        ProductWithQuantity(
            categoryId = it.categoryId,
            productId = it.productId,
            productName = it.productName,
            productPrice = it.productPrice,
            quantity = it.quantity,
        )
    }
}