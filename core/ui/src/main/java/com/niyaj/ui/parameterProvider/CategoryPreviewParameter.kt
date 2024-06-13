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

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.Category
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CategoryPreviewData.categories
import com.niyaj.ui.parameterProvider.CategoryPreviewData.categoryList

class CategoryPreviewParameter : PreviewParameterProvider<UiState<List<Category>>> {
    override val values: Sequence<UiState<List<Category>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Loading,
            UiState.Success(categoryList + categories),
        )
}

object CategoryPreviewData {

    val categoryList = listOf(
        Category(
            categoryId = 1,
            categoryName = "Electronics",
            isAvailable = true,
            createdAt = 1686374400000,
            updatedAt = 1686460800000,
        ),
        Category(
            categoryId = 2,
            categoryName = "Clothing",
            isAvailable = true,
            createdAt = 1686288000000,
            updatedAt = 1686374400000,
        ),
        Category(
            categoryId = 3,
            categoryName = "Books",
            isAvailable = true,
            createdAt = 1686201600000,
            updatedAt = 1686288000000,
        ),
        Category(
            categoryId = 4,
            categoryName = "Home & Garden",
            isAvailable = true,
            createdAt = 1686115200000,
            updatedAt = 1686201600000,
        ),
        Category(
            categoryId = 5,
            categoryName = "Sports & Outdoors",
            isAvailable = true,
            createdAt = 1686028800000,
            updatedAt = 1686115200000,
        ),
        Category(
            categoryId = 6,
            categoryName = "Beauty",
            isAvailable = false,
            createdAt = 1685942400000,
            updatedAt = 1686028800000,
        ),
        Category(
            categoryId = 7,
            categoryName = "Toys & Games",
            isAvailable = true,
            createdAt = 1685856000000,
            updatedAt = 1685942400000,
        ),
        Category(
            categoryId = 8,
            categoryName = "Automotive",
            isAvailable = true,
            createdAt = 1685769600000,
            updatedAt = 1685856000000,
        ),
        Category(
            categoryId = 9,
            categoryName = "Pet Supplies",
            isAvailable = true,
            createdAt = 1685683200000,
            updatedAt = 1685769600000,
        ),
        Category(
            categoryId = 10,
            categoryName = "Health & Personal Care",
            isAvailable = true,
            createdAt = 1685596800000,
            updatedAt = 1685683200000,
        ),
    )

    val categories = listOf(
        Category(
            categoryId = 11,
            categoryName = "Furniture",
            isAvailable = true,
            createdAt = 1686460800000,
            updatedAt = 1686547200000,
        ),
        Category(
            categoryId = 12,
            categoryName = "Kitchen & Dining",
            isAvailable = true,
            createdAt = 1686547200000,
            updatedAt = 1686633600000,
        ),
        Category(
            categoryId = 13,
            categoryName = "Patio & Garden",
            isAvailable = true,
            createdAt = 1686633600000,
            updatedAt = 1686720000000,
        ),
        Category(
            categoryId = 14,
            categoryName = "Art & Craft Supplies",
            isAvailable = true,
            createdAt = 1686720000000,
            updatedAt = 1686806400000,
        ),
        Category(
            categoryId = 15,
            categoryName = "Office Products",
            isAvailable = true,
            createdAt = 1686806400000,
            updatedAt = 1686892800000,
        ),
        Category(
            categoryId = 16,
            categoryName = "Musical Instruments",
            isAvailable = true,
            createdAt = 1686892800000,
            updatedAt = 1686979200000,
        ),
        Category(
            categoryId = 17,
            categoryName = "Camera & Photo",
            isAvailable = true,
            createdAt = 1686979200000,
            updatedAt = 1687065600000,
        ),
        Category(
            categoryId = 18,
            categoryName = "Software",
            isAvailable = true,
            createdAt = 1687065600000,
            updatedAt = 1687152000000,
        ),
        Category(
            categoryId = 19,
            categoryName = "Video Games",
            isAvailable = true,
            createdAt = 1687152000000,
            updatedAt = 1687238400000,
        ),
        Category(
            categoryId = 20,
            categoryName = "Luggage & Travel Gear",
            isAvailable = true,
            createdAt = 1687238400000,
            updatedAt = null,
        ),
    )
}
