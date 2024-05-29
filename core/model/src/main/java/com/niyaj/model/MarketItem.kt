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

import com.niyaj.model.utils.toDateString
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MarketItem(
    val itemId: Int = 0,

    val itemName: String,

    val itemType: MarketTypeIdAndName,

    val itemMeasureUnit: MeasureUnit,

    val itemPrice: String? = null,

    val itemDescription: String? = null,

    val createdAt: Long = System.currentTimeMillis(),

    val updatedAt: Long? = null,
)

fun List<MarketItem>.searchMarketItems(searchText: String): List<MarketItem> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.itemType.typeName.contains(searchText, true) ||
                it.itemName.contains(searchText, true) ||
                it.itemPrice?.contains(searchText, true) == true ||
                it.itemDescription?.contains(searchText, true) == true ||
                it.itemMeasureUnit.unitName.contains(searchText, true) ||
                it.createdAt.toDateString.contains(searchText, true) ||
                it.updatedAt?.toDateString?.contains(searchText, true) == true
        }
    } else {
        this
    }
}
