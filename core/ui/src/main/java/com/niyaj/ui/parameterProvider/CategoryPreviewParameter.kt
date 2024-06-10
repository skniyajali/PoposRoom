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
import com.niyaj.model.Category
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CategoryPreviewData.categoryList

class CategoryPreviewParameter : PreviewParameterProvider<UiState<List<Category>>> {
    override val values: Sequence<UiState<List<Category>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Loading,
            UiState.Success(categoryList),
        )
}

object CategoryPreviewData {

    val categoryList = listOf(
        Category(
            categoryId = 1,
            categoryName = "Electronics",
            isAvailable = true,
            createdAt = 1686374400000,
            updatedAt = 1686460800000
        ),
        Category(
            categoryId = 2,
            categoryName = "Clothing",
            isAvailable = true,
            createdAt = 1686288000000,
            updatedAt = 1686374400000
        ),
        Category(
            categoryId = 3,
            categoryName = "Books",
            isAvailable = true,
            createdAt = 1686201600000,
            updatedAt = 1686288000000
        ),
        Category(
            categoryId = 4,
            categoryName = "Home & Garden",
            isAvailable = true,
            createdAt = 1686115200000,
            updatedAt = 1686201600000
        ),
        Category(
            categoryId = 5,
            categoryName = "Sports & Outdoors",
            isAvailable = true,
            createdAt = 1686028800000,
            updatedAt = 1686115200000
        ),
        Category(
            categoryId = 6,
            categoryName = "Beauty",
            isAvailable = false,
            createdAt = 1685942400000,
            updatedAt = 1686028800000
        ),
        Category(
            categoryId = 7,
            categoryName = "Toys & Games",
            isAvailable = true,
            createdAt = 1685856000000,
            updatedAt = 1685942400000
        ),
        Category(
            categoryId = 8,
            categoryName = "Automotive",
            isAvailable = true,
            createdAt = 1685769600000,
            updatedAt = 1685856000000
        ),
        Category(
            categoryId = 9,
            categoryName = "Pet Supplies",
            isAvailable = true,
            createdAt = 1685683200000,
            updatedAt = 1685769600000
        ),
        Category(
            categoryId = 10,
            categoryName = "Health & Personal Care",
            isAvailable = true,
            createdAt = 1685596800000,
            updatedAt = 1685683200000
        )
    )
}