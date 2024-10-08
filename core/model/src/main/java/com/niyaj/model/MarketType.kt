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

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketType(
    val typeId: Int = 0,

    val typeName: String,

    val typeDesc: String? = null,

    val supplierId: Int = 0,

    val listTypes: List<String> = emptyList(),

    val createdAt: Long,

    val updatedAt: Long? = null,
)

fun List<MarketType>.searchMarketType(searchText: String): List<MarketType> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.typeName.contains(searchText, ignoreCase = true)
        }
    } else {
        this
    }
}
