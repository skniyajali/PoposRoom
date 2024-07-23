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
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    val productId: Int = 0,

    val categoryId: Int = 0,

    val productName: String = "",

    val productPrice: Int = 0,

    val productDescription: String = "",

    val productAvailability: Boolean = true,

    val tags: List<String> = emptyList(),

    val createdAt: Long,

    val updatedAt: Long? = null,
)

/**
 * Filter products
 */
fun List<Product>.filterProducts(searchText: String): List<Product> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.productName.contains(searchText, true) ||
                it.productPrice.toString().contains(searchText, true) ||
                it.productAvailability.toString().contains(searchText, true) ||
                it.productName.getCapitalWord().contains(searchText, true)
        }
    } else {
        this
    }
}
