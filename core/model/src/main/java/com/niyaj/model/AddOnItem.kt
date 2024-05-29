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
data class AddOnItem(
    val itemId: Int,

    val itemName: String,

    val itemPrice: Int,

    val isApplicable: Boolean,

    val createdAt: Long,

    val updatedAt: Long? = null,
)

data class AddOnPriceWithApplicable(val itemPrice: Int, val isApplicable: Boolean)

fun List<AddOnItem>.searchAddOnItem(searchText: String): List<AddOnItem> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.itemName.contains(searchText, true) ||
                it.itemPrice.toString().contains(searchText, true)
        }
    } else {
        this
    }
}
