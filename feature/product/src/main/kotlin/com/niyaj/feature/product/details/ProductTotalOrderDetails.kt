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

package com.niyaj.feature.product.details

data class ProductTotalOrderDetails(
    val totalAmount: String = "0",
    val dineInAmount: String = "0",
    val dineInQty: Int = 0,
    val dineOutAmount: String = "0",
    val dineOutQty: Int = 0,
    val mostOrderItemDate: String = "",
    val mostOrderQtyDate: String = "",
    val datePeriod: Pair<String, String> = Pair("", ""),
)
