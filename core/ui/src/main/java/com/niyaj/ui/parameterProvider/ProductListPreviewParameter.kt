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
import com.niyaj.common.utils.getStartDateLong
import com.niyaj.model.OrderType
import com.niyaj.model.Product
import com.niyaj.model.ProductWiseOrder
import com.niyaj.model.ProductWithQuantity
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ProductPreviewData.productList
import com.niyaj.ui.parameterProvider.ProductPreviewData.productWithQuantityList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.util.Date

class ProductWithQuantityStatePreviewParameter : PreviewParameterProvider<UiState<ImmutableList<ProductWithQuantity>>> {
    override val values: Sequence<UiState<ImmutableList<ProductWithQuantity>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(
                productWithQuantityList.toImmutableList(),
            ),
        )
}

class ProductListPreviewParameter : PreviewParameterProvider<UiState<List<Product>>> {
    override val values: Sequence<UiState<List<Product>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(productList),
        )
}

class ProductWiseOrderPreviewParameter : PreviewParameterProvider<UiState<List<ProductWiseOrder>>> {
    override val values: Sequence<UiState<List<ProductWiseOrder>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(ProductPreviewData.productWiseOrders),
        )
}

class ProductPreviewParameter : PreviewParameterProvider<UiState<Product>> {
    override val values: Sequence<UiState<Product>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(productList.random()),
        )
}

object ProductPreviewData {

    val productList = listOf(
        Product(
            productId = 1,
            categoryId = 1,
            productName = "Chicken Biryani",
            productPrice = 250,
            productDescription = "Fragrant basmati rice cooked with tender chicken pieces and aromatic spices",
            tags = listOf("Popular", "New"),
            createdAt = 1642537200000,
            updatedAt = null,
        ),
        Product(
            productId = 2,
            categoryId = 1,
            productName = "Beef Lasagna",
            productPrice = 300,
            productDescription = "Layers of pasta, ground beef, and cheese, baked to perfection",
            createdAt = 1642537200000,
            updatedAt = 1643146800000,
        ),
        Product(
            productId = 3,
            categoryId = 1,
            productName = "Vegetable Stir-Fry",
            productPrice = 180,
            productDescription = "A medley of fresh vegetables sautéed with garlic and soy sauce",
            tags = listOf("Best", "New"),
            createdAt = 1642537200000,
            updatedAt = null,
        ),
        Product(
            productId = 4,
            categoryId = 2,
            productName = "Samosa",
            productPrice = 60,
            productDescription = "Crispy fried pastry filled with spiced potato and peas",
            tags = listOf("Top", "Best"),
            createdAt = 1642537200000,
            updatedAt = null,
        ),
        Product(
            productId = 5,
            categoryId = 2,
            productName = "Bruschetta",
            productPrice = 80,
            productDescription = "Grilled bread topped with fresh tomatoes, basil, and olive oil",
            tags = listOf("Popular", "New"),
            createdAt = 1642537200000,
            updatedAt = 1643146800000,
        ),
        Product(
            productId = 6,
            categoryId = 2,
            productName = "Nachos",
            productPrice = 120,
            productDescription = "Crispy tortilla chips topped with melted cheese, salsa, and guacamole",
            createdAt = 1642537200000,
            updatedAt = null,
        ),
        Product(
            productId = 7,
            categoryId = 3,
            productName = "Gulab Jamun",
            productPrice = 100,
            productDescription = "Warm milk-solid balls soaked in rose-flavored sugar syrup",
            createdAt = 1642537200000,
            updatedAt = null,
        ),
        Product(
            productId = 8,
            categoryId = 3,
            productName = "Tiramisu",
            productPrice = 150,
            productDescription = "Layers of coffee-soaked ladyfingers and creamy mascarpone cheese",
            createdAt = 1642537200000,
            updatedAt = 1643146800000,
        ),
        Product(
            productId = 9,
            categoryId = 3,
            productName = "Chocolate Lava Cake",
            productPrice = 180,
            productDescription = "Decadent chocolate cake with a molten chocolate center",
            createdAt = 1642537200000,
            updatedAt = null,
        ),
        Product(
            productId = 10,
            categoryId = 4,
            productName = "Masala Chai",
            productPrice = 80,
            productDescription = "Fragrant black tea infused with aromatic spices and milk",
            createdAt = 1642537200000,
            updatedAt = null,
        ),
        Product(
            productId = 11,
            categoryId = 4,
            productName = "Smoothie",
            productPrice = 120,
            productDescription = "Fresh fruit blended with yogurt and honey",
            createdAt = 1642537200000,
            updatedAt = 1643146800000,
        ),
        Product(
            productId = 12,
            categoryId = 4,
            productName = "Iced Coffee",
            productPrice = 100,
            productDescription = "Chilled coffee with milk and sweetener",
            createdAt = 1642537200000,
            updatedAt = null,
        ),
        Product(
            productId = 13,
            categoryId = 5,
            productName = "Garlic Bread",
            productPrice = 60,
            productDescription = "Freshly baked bread loaf with garlic and herbs",
            createdAt = 1642537200000,
            updatedAt = null,
        ),
        Product(
            productId = 14,
            categoryId = 5,
            productName = "Croissant",
            productPrice = 40,
            productDescription = "Flaky, buttery pastry with a crispy exterior and soft interior",
            createdAt = 1642537200000,
            updatedAt = 1643146800000,
        ),
        Product(
            productId = 15,
            categoryId = 5,
            productName = "Bagel",
            productPrice = 30,
            productDescription = "Chewy ring-shaped bread with a crispy crust",
            createdAt = 1642537200000,
            updatedAt = null,
        ),
    )

    val productWithQuantityList = listOf(
        ProductWithQuantity(
            categoryId = 1,
            productId = 101,
            productName = "Apple",
            productPrice = 50,
            quantity = 10,
        ),
        ProductWithQuantity(
            categoryId = 1,
            productId = 102,
            productName = "Banana",
            productPrice = 30,
            quantity = 15,
        ),
        ProductWithQuantity(
            categoryId = 2,
            productId = 201,
            productName = "Milk",
            productPrice = 80,
            quantity = 5,
        ),
        ProductWithQuantity(
            categoryId = 2,
            productId = 202,
            productName = "Cheese",
            productPrice = 100,
            quantity = 8,
        ),
        ProductWithQuantity(
            categoryId = 3,
            productId = 301,
            productName = "Shirt",
            productPrice = 500,
            quantity = 3,
        ),
        ProductWithQuantity(
            categoryId = 3,
            productId = 302,
            productName = "Jeans",
            productPrice = 800,
            quantity = 2,
        ),
        ProductWithQuantity(
            categoryId = 4,
            productId = 401,
            productName = "Headphones",
            productPrice = 1200,
            quantity = 6,
        ),
        ProductWithQuantity(
            categoryId = 4,
            productId = 402,
            productName = "Smartwatch",
            productPrice = 2000,
            quantity = 4,
        ),
        ProductWithQuantity(
            categoryId = 5,
            productId = 501,
            productName = "Sofa",
            productPrice = 10000,
            quantity = 1,
        ),
        ProductWithQuantity(
            categoryId = 5,
            productId = 502,
            productName = "Bed",
            productPrice = 12000,
            quantity = 2,
        ),
    )

    val productWiseOrders = listOf(
        ProductWiseOrder(
            orderId = 1,
            orderedDate = Date(getStartDateLong - 86400000),
            orderType = OrderType.DineIn,
            quantity = 2,
            customerPhone = null,
            customerAddress = null,
        ),
        ProductWiseOrder(
            orderId = 2,
            orderedDate = Date(getStartDateLong),
            orderType = OrderType.DineOut,
            quantity = 1,
            customerPhone = "1234567890",
            customerAddress = "123 Main St, City",
        ),
        ProductWiseOrder(
            orderId = 3,
            orderedDate = Date(getStartDateLong - 172800000),
            orderType = OrderType.DineIn,
            quantity = 3,
            customerPhone = null,
            customerAddress = null,
        ),
        ProductWiseOrder(
            orderId = 4,
            orderedDate = Date(getStartDateLong),
            orderType = OrderType.DineOut,
            quantity = 2,
            customerPhone = "9876543210",
            customerAddress = "456 Oak Rd, Town",
        ),
        ProductWiseOrder(
            orderId = 5,
            orderedDate = Date(getStartDateLong),
            orderType = OrderType.DineIn,
            quantity = 1,
            customerPhone = null,
            customerAddress = null,
        ),
        ProductWiseOrder(
            orderId = 6,
            orderedDate = Date(getStartDateLong - 86400000),
            orderType = OrderType.DineOut,
            quantity = 4,
            customerPhone = "5555555555",
            customerAddress = "789 Elm St, Village",
        ),
        ProductWiseOrder(
            orderId = 7,
            orderedDate = Date(getStartDateLong),
            orderType = OrderType.DineIn,
            quantity = 2,
            customerPhone = null,
            customerAddress = null,
        ),
        ProductWiseOrder(
            orderId = 8,
            orderedDate = Date(getStartDateLong - 172800000),
            orderType = OrderType.DineOut,
            quantity = 3,
            customerPhone = "1010101010",
            customerAddress = "246 Pine Ln, Suburb",
        ),
        ProductWiseOrder(
            orderId = 9,
            orderedDate = Date(getStartDateLong),
            orderType = OrderType.DineIn,
            quantity = 1,
            customerPhone = null,
            customerAddress = null,
        ),
        ProductWiseOrder(
            orderId = 10,
            orderedDate = Date(getStartDateLong - 86400000),
            orderType = OrderType.DineOut,
            quantity = 2,
            customerPhone = "8888888888",
            customerAddress = "135 Cedar Ave, City",
        ),
    )
}
