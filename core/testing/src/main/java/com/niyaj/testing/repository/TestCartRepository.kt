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

package com.niyaj.testing.repository

import android.system.Os.remove
import androidx.annotation.VisibleForTesting
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.CartRepository
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartItem
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderType
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestCartRepository : CartRepository {

    /**
     * The backing fields for testing
     */
    private val cartItems = MutableStateFlow(mutableListOf<CartItem>())
    private val addOnItems = MutableStateFlow(mutableListOf<AddOnItem>())
    private val deliveryPartners = MutableStateFlow(mutableListOf<EmployeeNameAndId>())
    private val cartProducts = mutableListOf<CartProductItem>()
    private val charges = MutableStateFlow(mutableListOf<Charges>())

    override fun getAllAddOnItems(): Flow<List<AddOnItem>> = addOnItems

    override suspend fun getDeliveryPartners(): Flow<List<EmployeeNameAndId>> = deliveryPartners

    override suspend fun getAllDineInCart(): Flow<List<CartItem>> =
        cartItems.map { it.filter { item -> item.orderType == OrderType.DineIn } }

    override suspend fun getAllDineOutCart(): Flow<List<CartItem>> =
        cartItems.map { it.filter { item -> item.orderType == OrderType.DineOut } }

    override suspend fun getCartItemByOrderId(orderId: Int): Flow<CartItem> {
        return cartItems.map { list ->
            list.find { it.orderId == orderId } ?: CartItem()
        }
    }

    override suspend fun addProductToCart(orderId: Int, productId: Int): Resource<Boolean> {
        val updatedItems = cartItems.value.map { cartItem ->
            if (cartItem.orderId == orderId) {
                val updatedProducts = cartItem.cartProducts.toMutableList()
                val existingProduct = updatedProducts.find { it.productId == productId }
                val product = cartProducts.find { it.productId == productId }

                require(product != null) { "Product not found" }

                if (existingProduct != null) {
                    val index = updatedProducts.indexOf(existingProduct)

                    updatedProducts[index] = existingProduct.copy(
                        productQuantity = existingProduct.productQuantity + 1,
                    )
                } else {
                    updatedProducts.add(product.copy(productQuantity = 1))
                }

                cartItem.copy(
                    cartProducts = updatedProducts.toImmutableList(),
                    orderPrice = cartItem.orderPrice + product.productPrice,
                )
            } else {
                cartItem
            }
        }
        cartItems.value = updatedItems.toMutableList()
        return Resource.Success(true)
    }

    override suspend fun removeProductFromCart(orderId: Int, productId: Int): Resource<Boolean> {
        val updatedItems = cartItems.value.map { cartItem ->
            if (cartItem.orderId == orderId) {
                val updatedProducts = cartItem.cartProducts.toMutableList()
                val existingProduct = updatedProducts.find { it.productId == productId }
                val product = cartProducts.find { it.productId == productId }

                require(product != null) { "Product not found" }

                if (existingProduct != null) {
                    if (existingProduct.productQuantity > 1) {
                        val index = updatedProducts.indexOf(existingProduct)
                        updatedProducts[index] =
                            existingProduct.copy(productQuantity = existingProduct.productQuantity - 1)
                    } else {
                        updatedProducts.remove(existingProduct)
                    }
                    cartItem.copy(
                        cartProducts = updatedProducts.toImmutableList(),
                        orderPrice = cartItem.orderPrice - product.productPrice,
                    )
                } else {
                    cartItem
                }
            } else {
                cartItem
            }
        }
        cartItems.value = updatedItems.toMutableList()
        return Resource.Success(true)
    }

    override suspend fun placeOrder(orderId: Int): Resource<Boolean> {
        return Resource.Success(cartItems.value.removeIf { it.orderId == orderId })
    }

    override suspend fun placeAllOrder(orderIds: List<Int>): Resource<Boolean> {
        return Resource.Success(cartItems.value.removeIf { it.orderId in orderIds })
    }

    override suspend fun updateAddOnItem(orderId: Int, itemId: Int): Resource<Boolean> {
        val updatedItems = cartItems.value.map { cartItem ->
            if (cartItem.orderId == orderId) {
                val updatedAddOns = cartItem.addOnItems.toMutableList()
                val addOnItem = addOnItems.value.find { it.itemId == itemId }

                if (addOnItem != null) {
                    if (itemId in updatedAddOns) {
                        // Remove add-on if it exists
                        updatedAddOns.remove(itemId)
                        cartItem.copy(
                            addOnItems = updatedAddOns.toImmutableList(),
                            orderPrice = cartItem.orderPrice - addOnItem.itemPrice,
                        )
                    } else {
                        // Add add-on if it doesn't exist
                        updatedAddOns.add(itemId)
                        cartItem.copy(
                            addOnItems = updatedAddOns.toImmutableList(),
                            orderPrice = cartItem.orderPrice + addOnItem.itemPrice,
                        )
                    }
                } else {
                    cartItem
                }
            } else {
                cartItem
            }
        }
        cartItems.value = updatedItems.toMutableList()
        return Resource.Success(true)
    }

    override suspend fun updateCharges(orderId: Int, chargesId: Int): Resource<Boolean> {
        val updatedItems = cartItems.value.map { cartItem ->
            if (cartItem.orderId == orderId) {
                val updatedCharges = cartItem.charges.toMutableList()
                val charge = charges.value.find { it.chargesId == chargesId }

                if (charge != null) {
                    if (chargesId in updatedCharges) {
                        // Remove charge if it exists
                        updatedCharges.remove(chargesId)
                        cartItem.copy(
                            charges = updatedCharges.toImmutableList(),
                            orderPrice = cartItem.orderPrice - charge.chargesPrice,
                        )
                    } else {
                        // Add charge if it doesn't exist
                        updatedCharges.add(chargesId)
                        cartItem.copy(
                            charges = updatedCharges.toImmutableList(),
                            orderPrice = cartItem.orderPrice + charge.chargesPrice,
                        )
                    }
                } else {
                    cartItem
                }
            } else {
                cartItem
            }
        }
        cartItems.value = updatedItems.toMutableList()
        return Resource.Success(true)
    }

    override suspend fun updateDeliveryPartner(
        orderId: Int,
        deliveryPartnerId: Int,
    ): Resource<Boolean> {
        val updatedItems = cartItems.value.map { cartItem ->
            if (cartItem.orderId == orderId) {
                cartItem.copy(deliveryPartnerId = deliveryPartnerId)
            } else {
                cartItem
            }
        }
        cartItems.value = updatedItems.toMutableList()
        return Resource.Success(true)
    }

    override suspend fun deleteProductFromCart(orderId: Int, productId: Int): Resource<Boolean> {
        val updatedItems = cartItems.value.map { cartItem ->
            if (cartItem.orderId == orderId) {
                val updatedProducts = cartItem.cartProducts.filter { it.productId != productId }
                val removedProduct = cartItem.cartProducts.find { it.productId == productId }
                cartItem.copy(
                    cartProducts = updatedProducts.toImmutableList(),
                    orderPrice = cartItem.orderPrice - (removedProduct?.productQuantity ?: 0),
                )
            } else {
                cartItem
            }
        }
        cartItems.value = updatedItems.toMutableList()
        return Resource.Success(true)
    }

    override suspend fun getProductQuantity(orderId: Int, productId: Int): Flow<Int> {
        return cartItems.map { list ->
            list.find { it.orderId == orderId }?.cartProducts?.find {
                it.productId == productId
            }?.productQuantity ?: 0
        }
    }

    @VisibleForTesting
    fun updateCartItems(items: List<CartItem>) {
        cartItems.update { items.toMutableList() }
    }

    @VisibleForTesting
    fun updateAddOnItems(items: List<AddOnItem>) {
        addOnItems.update { items.toMutableList() }
    }

    @VisibleForTesting
    fun updateDeliveryPartners(items: List<EmployeeNameAndId>) {
        deliveryPartners.update { items.toMutableList() }
    }

    @VisibleForTesting
    fun updateCartProducts(items: List<CartProductItem>) {
        cartProducts.clear()
        cartProducts.addAll(items)
    }

    @VisibleForTesting
    fun updateCharges(items: List<Charges>) {
        charges.update { items.toMutableList() }
    }
}
