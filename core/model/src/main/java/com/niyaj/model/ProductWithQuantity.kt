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

package com.niyaj.model

import com.niyaj.model.utils.getCapitalWord

data class ProductWithQuantity(
    val categoryId: Int,
    val productId: Int,
    val productName: String,
    val productPrice: Int,
    val quantity: Int = 0,
)

fun List<ProductWithQuantity>.filterByCategory(categoryId: Int): List<ProductWithQuantity> {
    return this.filter {
        if (categoryId != 0) {
            it.categoryId == categoryId
        } else {
            true
        }
    }
}

fun List<ProductWithQuantity>.filterBySearch(searchText: String): List<ProductWithQuantity> {
    return this.filter {
        if (searchText.isNotEmpty()) {
            it.productName.contains(searchText, true) ||
                it.productPrice.toString().contains(searchText, true) ||
                it.productName.getCapitalWord().contains(searchText, true)
        } else {
            true
        }
    }
}
