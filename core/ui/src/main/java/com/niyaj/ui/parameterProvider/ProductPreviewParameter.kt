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

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.ProductWithQuantity
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ProductPreviewData.productWithQuantityList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class ProductWithQuantityStatePreviewParameter: PreviewParameterProvider<UiState<ImmutableList<ProductWithQuantity>>> {
    override val values: Sequence<UiState<ImmutableList<ProductWithQuantity>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(
                productWithQuantityList.toImmutableList()
            )
        )
}

object ProductPreviewData {

    val productWithQuantityList = listOf(
        ProductWithQuantity(
            categoryId = 1,
            productId = 101,
            productName = "Apple",
            productPrice = 50,
            quantity = 10
        ),
        ProductWithQuantity(
            categoryId = 1,
            productId = 102,
            productName = "Banana",
            productPrice = 30,
            quantity = 15
        ),
        ProductWithQuantity(
            categoryId = 2,
            productId = 201,
            productName = "Milk",
            productPrice = 80,
            quantity = 5
        ),
        ProductWithQuantity(
            categoryId = 2,
            productId = 202,
            productName = "Cheese",
            productPrice = 100,
            quantity = 8
        ),
        ProductWithQuantity(
            categoryId = 3,
            productId = 301,
            productName = "Shirt",
            productPrice = 500,
            quantity = 3
        ),
        ProductWithQuantity(
            categoryId = 3,
            productId = 302,
            productName = "Jeans",
            productPrice = 800,
            quantity = 2
        ),
        ProductWithQuantity(
            categoryId = 4,
            productId = 401,
            productName = "Headphones",
            productPrice = 1200,
            quantity = 6
        ),
        ProductWithQuantity(
            categoryId = 4,
            productId = 402,
            productName = "Smartwatch",
            productPrice = 2000,
            quantity = 4
        ),
        ProductWithQuantity(
            categoryId = 5,
            productId = 501,
            productName = "Sofa",
            productPrice = 10000,
            quantity = 1
        ),
        ProductWithQuantity(
            categoryId = 5,
            productId = 502,
            productName = "Bed",
            productPrice = 12000,
            quantity = 2
        )
    )
}