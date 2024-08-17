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

data class MarketItemAndQuantity(
    val listWithTypeId: Int = 0,

    val itemId: Int,

    val itemName: String,

    val typeName: String,

    val unitName: String,

    val unitValue: Double,

    val itemPrice: String? = null,

    val listType: String,

    val itemQuantity: Double? = null,
)

fun List<MarketItemAndQuantity>.searchMarketType(searchText: String): List<MarketItemAndQuantity> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.itemName.contains(searchText, true) ||
                it.itemName.getCapitalWord().contains(searchText, true) ||
                it.itemPrice?.contains(searchText, true) == true
        }
    } else {
        this
    }
}
