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
import com.niyaj.model.CartItem
import com.niyaj.model.CartProductItem
import com.niyaj.model.OrderType
import com.niyaj.ui.parameterProvider.CartPreviewParameterData.dineInCartItems
import com.niyaj.ui.parameterProvider.CartPreviewParameterData.dineOutCartItems
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock

class CartItemsPreviewParameter : PreviewParameterProvider<List<CartItem>> {
    override val values: Sequence<List<CartItem>>
        get() = sequenceOf(
             dineOutCartItems.take(5),
            dineInCartItems.take(5)
        )
}

class CartItemPreviewParameter : PreviewParameterProvider<CartItem> {
    override val values: Sequence<CartItem>
        get() = sequenceOf(
            dineOutCartItems.first(),
            dineInCartItems.last()
        )
}

object CartPreviewParameterData {
    // Define sample data
    val sampleCartProductItems = listOf(
        CartProductItem(
            productId = 1,
            productName = "Burger",
            productPrice = 500,
            productQuantity = 2
        ),
        CartProductItem(
            productId = 2,
            productName = "Pizza",
            productPrice = 800,
            productQuantity = 1
        ),
        CartProductItem(
            productId = 3,
            productName = "Soda",
            productPrice = 100,
            productQuantity = 3
        ),
        CartProductItem(
            productId = 4,
            productName = "Fries",
            productPrice = 200,
            productQuantity = 2
        ),
        CartProductItem(
            productId = 5,
            productName = "Salad",
            productPrice = 300,
            productQuantity = 1
        ),
        CartProductItem(
            productId = 6,
            productName = "Chicken Wings",
            productPrice = 600,
            productQuantity = 1
        ),
        CartProductItem(
            productId = 7,
            productName = "Ice Cream",
            productPrice = 150,
            productQuantity = 2
        ),
        CartProductItem(
            productId = 8,
            productName = "Steak",
            productPrice = 1200,
            productQuantity = 1
        ),
        CartProductItem(
            productId = 9,
            productName = "Pasta",
            productPrice = 700,
            productQuantity = 1
        ),
        CartProductItem(
            productId = 10,
            productName = "Coffee",
            productPrice = 250,
            productQuantity = 2
        )
    )

    private val chargesId = listOf(1, 2, 3, 4, 5)
    private val addOnIds = listOf(1, 2, 3, 4, 5)

    // Current timestamp in milliseconds
    val currentTimeMillis = Clock.System.now().toEpochMilliseconds()

    // Sample list for DineIn orders
    val dineInCartItems = listOf(
        CartItem(
            orderId = 1,
            orderType = OrderType.DineIn,
            cartProducts = sampleCartProductItems.shuffled().take(5).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(3).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 1600L,
        ),
        CartItem(
            orderId = 2,
            orderType = OrderType.DineIn,
            cartProducts = sampleCartProductItems.shuffled().take(3).toImmutableList(),
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 1700L,
        ),
        CartItem(
            orderId = 3,
            orderType = OrderType.DineIn,
            cartProducts = sampleCartProductItems.shuffled().take(2).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(2).toImmutableList(),
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 1800L,
        ),
        CartItem(
            orderId = 4,
            orderType = OrderType.DineIn,
            cartProducts = sampleCartProductItems.shuffled().take(4).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 1900L,
        ),
        CartItem(
            orderId = 5,
            orderType = OrderType.DineIn,
            cartProducts = sampleCartProductItems.shuffled().take(2).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(3).toImmutableList(),
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 2000L,
        ),
        CartItem(
            orderId = 6,
            orderType = OrderType.DineIn,
            cartProducts = sampleCartProductItems.shuffled().take(3).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 2100L,
        ),
        CartItem(
            orderId = 7,
            orderType = OrderType.DineIn,
            cartProducts = sampleCartProductItems.shuffled().take(4).toImmutableList(),
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 2200L,
        ),
        CartItem(
            orderId = 8,
            orderType = OrderType.DineIn,
            cartProducts = sampleCartProductItems.shuffled().take(3).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(3).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 2300L,
        ),
        CartItem(
            orderId = 9,
            orderType = OrderType.DineIn,
            cartProducts = sampleCartProductItems.shuffled().take(2).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 2400L,
        ),
        CartItem(
            orderId = 10,
            orderType = OrderType.DineIn,
            cartProducts = sampleCartProductItems.shuffled().take(5).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(1).toImmutableList(),
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 2500L,
        ),
    )

    // Sample list for DineOut orders
    val dineOutCartItems = listOf(
        CartItem(
            orderId = 11,
            orderType = OrderType.DineOut,
            cartProducts = sampleCartProductItems.shuffled().take(4).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(3).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            customerPhone = "1234567891",
            customerAddress = "123 Main St, City 1",
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 1600L,
            deliveryPartnerId = 1,
        ),
        CartItem(
            orderId = 12,
            orderType = OrderType.DineOut,
            cartProducts = sampleCartProductItems.shuffled().take(1).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(4).toImmutableList(),
            customerPhone = "1234567892",
            customerAddress = "123 Main St, City 2",
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 1700L,
            deliveryPartnerId = 2,
        ),
        CartItem(
            orderId = 13,
            orderType = OrderType.DineOut,
            cartProducts = sampleCartProductItems.shuffled().take(2).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            customerPhone = "1234567893",
            customerAddress = "123 Main St, City 3",
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 1800L,
            deliveryPartnerId = 3,
        ),
        CartItem(
            orderId = 14,
            orderType = OrderType.DineOut,
            cartProducts = sampleCartProductItems.shuffled().take(2).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(3).toImmutableList(),
            customerPhone = "1234567894",
            customerAddress = "123 Main St, City 4",
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 1900L,
            deliveryPartnerId = 4,
        ),
        CartItem(
            orderId = 15,
            orderType = OrderType.DineOut,
            cartProducts = sampleCartProductItems.shuffled().take(3).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(1).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            customerPhone = "1234567895",
            customerAddress = "123 Main St, City 5",
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 2000L,
            deliveryPartnerId = 5,
        ),
        CartItem(
            orderId = 16,
            orderType = OrderType.DineOut,
            cartProducts = sampleCartProductItems.shuffled().take(5).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(2).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            customerPhone = "1234567896",
            customerAddress = "123 Main St, City 6",
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 2100L,
            deliveryPartnerId = 6,
        ),
        CartItem(
            orderId = 17,
            orderType = OrderType.DineOut,
            cartProducts = sampleCartProductItems.shuffled().take(4).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(4).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            customerPhone = "1234567897",
            customerAddress = "123 Main St, City 7",
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 2200L,
            deliveryPartnerId = 7,
        ),
        CartItem(
            orderId = 18,
            orderType = OrderType.DineOut,
            cartProducts = sampleCartProductItems.shuffled().take(3).toImmutableList(),
            addOnItems = addOnIds.shuffled().take(3).toImmutableList(),
            charges = chargesId.shuffled().take(2).toImmutableList(),
            customerPhone = "1234567898",
            customerAddress = "123 Main St, City 8",
            updatedAt = currentTimeMillis.toString(),
            orderPrice = 2300L,
            deliveryPartnerId = 8,
        ),
    )
}