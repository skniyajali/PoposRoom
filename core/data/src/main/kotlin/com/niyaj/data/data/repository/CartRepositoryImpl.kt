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

package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.toTimeSpan
import com.niyaj.data.repository.CartRepository
import com.niyaj.database.dao.CartDao
import com.niyaj.database.dao.CartOrderDao
import com.niyaj.database.dao.CartPriceDao
import com.niyaj.database.dao.SelectedDao
import com.niyaj.database.model.CartAddOnItemsEntity
import com.niyaj.database.model.CartChargesEntity
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartItemDto
import com.niyaj.database.model.CartPriceEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.SelectedEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartItem
import com.niyaj.model.CartProductItem
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderType
import com.niyaj.model.OrderWithCartItems
import com.niyaj.model.SELECTED_ID
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class CartRepositoryImpl(
    private val cartDao: CartDao,
    private val cartOrderDao: CartOrderDao,
    private val selectedDao: SelectedDao,
    private val cartPriceDao: CartPriceDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : CartRepository {

    override fun getAllAddOnItems(): Flow<List<AddOnItem>> {
        return cartDao.getAllAddOnItems().mapLatest { list ->
            list.map {
                it.asExternalModel()
            }
        }
    }

    override suspend fun getDeliveryPartners(): Flow<List<EmployeeNameAndId>> {
        return withContext(ioDispatcher) {
            cartOrderDao.getDeliveryPartners()
        }
    }

    override suspend fun getAllCartOrders(): Flow<List<OrderWithCartItems>> {
        return flow { }
    }

    override suspend fun getAllDineInCart(): Flow<List<CartItem>> {
        return withContext(ioDispatcher) {
            cartDao.getAllOrders(OrderType.DineIn).mapLatest(::mapCartOrdersToCartItemAsync)
        }
    }

    override suspend fun getAllDineOutCart(): Flow<List<CartItem>> {
        return withContext(ioDispatcher) {
            cartDao.getAllOrders(OrderType.DineOut).mapLatest(::mapCartOrdersToCartItemAsync)
        }
    }

    override suspend fun getCartItemByOrderId(orderId: Int): Flow<CartItem> {
        return withContext(ioDispatcher) {
            cartDao.getCartItemByOrderId(orderId).map(::mapCartOrderToCartItemAsync)
        }
    }

    override suspend fun addProductToCart(orderId: Int, productId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val cart = cartDao.getCartOrderById(orderId, productId)

                if (cart != null) {
                    val qty = cart.quantity + 1

                    val result = cartDao.updateQuantity(orderId, productId, qty)

                    if (result > 0) {
                        async(ioDispatcher) {
                            increaseCartProductPrice(orderId, productId)
                        }.await()
                    }

                    Resource.Success(result > 0)
                } else {
                    val newCartEntity = CartEntity(
                        orderId = orderId,
                        productId = productId,
                        quantity = 1,
                    )

                    val result = cartDao.addOrRemoveCartProduct(newCartEntity)

                    if (result > 0) {
                        async(ioDispatcher) {
                            increaseCartProductPrice(orderId, productId)
                        }.await()
                    }

                    Resource.Success(result > 0)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun removeProductFromCart(orderId: Int, productId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val cart = cartDao.getCartOrderById(orderId, productId)

                if (cart != null) {
                    if (cart.quantity == 1) {
                        val result = cartDao.deleteProductFromCart(orderId, productId)

                        if (result > 0) {
                            async(ioDispatcher) {
                                decreaseCartProductPrice(orderId, productId)
                            }.await()
                        }

                        Resource.Success(result > 0)
                    } else {
                        val qty = cart.quantity - 1

                        val result = cartDao.updateQuantity(orderId, productId, qty)

                        if (result > 0) {
                            async(ioDispatcher) {
                                decreaseCartProductPrice(orderId, productId)
                            }.await()
                        }

                        Resource.Success(result > 0)
                    }
                } else {
                    Resource.Error("Unable to find cart order")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun placeOrder(orderId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = cartOrderDao.placeOrder(orderId)

                async {
                    updateOrDeleteSelectedOrder()
                }.await()

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun placeAllOrder(orderIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = cartOrderDao.placeAllOrder(orderIds)

                async {
                    updateOrDeleteSelectedOrder()
                }.await()

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateAddOnItem(orderId: Int, itemId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val item = cartOrderDao.getCartAddOnItemById(orderId, itemId)

                val result = if (item != null) {
                    cartDao.deleteCartAddOnItem(orderId, itemId)

                    async(ioDispatcher) {
                        removeAddOnItemPrice(orderId, itemId)
                    }.await()
                } else {
                    cartDao.insertCartAddOnItem(CartAddOnItemsEntity(orderId, itemId)).toInt()

                    async(ioDispatcher) {
                        insertAddOnItemPrice(orderId, itemId)
                    }.await()
                }

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateCharges(orderId: Int, chargesId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val item = cartOrderDao.getCartChargesById(orderId, chargesId)

                val result = if (item != null) {
                    cartOrderDao.deleteCartCharges(orderId, chargesId)
                    removeChargesPrice(orderId, chargesId)
                } else {
                    cartOrderDao.insertCartCharge(CartChargesEntity(orderId, chargesId)).toInt()
                    insertChargesPrice(orderId, chargesId)
                }

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun updateDeliveryPartner(
        orderId: Int,
        deliveryPartnerId: Int,
    ): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val result = cartDao.updateDeliveryPartner(orderId, deliveryPartnerId)

                Resource.Success(result > 0)
            } catch (e: Exception) {
                Resource.Error(e.message.toString())
            }
        }
    }

    override suspend fun deleteProductFromCart(orderId: Int, productId: Int): Resource<Boolean> {
        return try {
            val result = cartDao.deleteProductFromCart(orderId, productId)

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error("Unable to delete cart")
        }
    }

    override suspend fun getProductQuantity(orderId: Int, productId: Int): Flow<Int> {
        return withContext(ioDispatcher) {
            cartDao.getProductQuantity(orderId, productId).distinctUntilChanged()
        }
    }

    private suspend fun mapCartOrdersToCartItemAsync(cartOrders: List<CartItemDto>): List<CartItem> {
        return withContext(ioDispatcher) {
            val productMap = withContext(ioDispatcher) {
                val productIds = cartOrders.flatMap { it.cartItems }.map { it.productId }.toSet()
                cartDao.getProductsById(productIds.toList())
                    .associateBy { it.productId } // Create map for efficient lookup by product ID
            }

            cartOrders
                .filter { it.cartItems.isNotEmpty() }
                .map { order ->
                    val cartProducts = order.cartItems.map { cartItem ->
                        val product = productMap[cartItem.productId] ?: ProductEntity()

                        CartProductItem(
                            productId = product.productId,
                            productName = product.productName,
                            productPrice = product.productPrice,
                            productQuantity = cartItem.quantity,
                        )
                    }

                    CartItem(
                        orderId = order.cartOrder.orderId,
                        orderType = order.cartOrder.orderType,
                        cartProducts = cartProducts.toImmutableList(),
                        addOnItems = order.addOnItems.toImmutableList(),
                        charges = order.charges.toImmutableList(),
                        customerPhone = order.customerPhone,
                        customerAddress = order.customerAddress,
                        updatedAt = (
                            order.cartOrder.updatedAt
                                ?: order.cartOrder.createdAt
                            ).toTimeSpan,
                        orderPrice = order.orderPrice.totalPrice,
                        deliveryPartnerId = order.cartOrder.deliveryPartnerId,
                    )
                }
        }
    }

    private suspend fun mapCartOrderToCartItemAsync(itemDto: CartItemDto): CartItem {
        return withContext(ioDispatcher) {
            val productMap = withContext(ioDispatcher) {
                val productIds = itemDto.cartItems.map { it.productId }.toSet()
                cartDao.getProductsById(productIds.toList())
                    .associateBy { it.productId } // Create map for efficient lookup by product ID
            }

            val cartProducts = itemDto.cartItems.map { cartItem ->
                val product = productMap[cartItem.productId] ?: ProductEntity()

                CartProductItem(
                    productId = product.productId,
                    productName = product.productName,
                    productPrice = product.productPrice,
                    productQuantity = cartItem.quantity,
                )
            }

            CartItem(
                orderId = itemDto.cartOrder.orderId,
                orderType = itemDto.cartOrder.orderType,
                cartProducts = cartProducts.toImmutableList(),
                addOnItems = itemDto.addOnItems.toImmutableList(),
                charges = itemDto.charges.toImmutableList(),
                customerPhone = itemDto.customerPhone,
                customerAddress = itemDto.customerAddress,
                updatedAt = (
                    itemDto.cartOrder.updatedAt
                        ?: itemDto.cartOrder.createdAt
                    ).toTimeSpan,
                orderPrice = itemDto.orderPrice.totalPrice,
                deliveryPartnerId = itemDto.cartOrder.deliveryPartnerId,
            )
        }
    }

    private suspend fun updateOrDeleteSelectedOrder() {
        withContext(ioDispatcher) {
            val lastId = cartOrderDao.getLastProcessingId()

            lastId?.let {
                selectedDao.insertOrUpdateSelectedOrder(
                    SelectedEntity(
                        selectedId = SELECTED_ID,
                        orderId = it,
                    ),
                )
            } ?: selectedDao.deleteSelectedOrder(SELECTED_ID)
        }
    }

    private suspend fun increaseCartProductPrice(orderId: Int, productId: Int): Int {
        return withContext(ioDispatcher) {
            val productPrice = async(ioDispatcher) {
                cartOrderDao.getProductPrice(productId)
            }.await()

            val cartPrice = async(ioDispatcher) {
                cartPriceDao.getCartPriceByOrderId(orderId)
            }.await()

            val cartBasePrice = cartPrice.basePrice + productPrice
            val cartTotalPrice = cartPrice.totalPrice + productPrice

            val updateCartPrice = CartPriceEntity(
                orderId = orderId,
                basePrice = cartBasePrice,
                discountPrice = cartPrice.discountPrice,
                totalPrice = cartTotalPrice,
                createdAt = System.currentTimeMillis().toString(),
            )

            cartPriceDao.updateCartPrice(updateCartPrice)
        }
    }

    private suspend fun decreaseCartProductPrice(orderId: Int, productId: Int): Int {
        return withContext(ioDispatcher) {
            val productPrice = async(ioDispatcher) {
                cartOrderDao.getProductPrice(productId)
            }.await()

            val cartPrice = async(ioDispatcher) {
                cartPriceDao.getCartPriceByOrderId(orderId)
            }.await()

            val cartBasePrice = cartPrice.basePrice - productPrice
            val cartTotalPrice = cartPrice.totalPrice - productPrice

            val updateCartPrice = CartPriceEntity(
                orderId = orderId,
                basePrice = cartBasePrice,
                discountPrice = cartPrice.discountPrice,
                totalPrice = cartTotalPrice,
                createdAt = System.currentTimeMillis().toString(),
            )

            cartPriceDao.updateCartPrice(updateCartPrice)
        }
    }

    private suspend fun insertAddOnItemPrice(orderId: Int, itemId: Int): Int {
        return withContext(ioDispatcher) {
            val addOnPrice = async(ioDispatcher) {
                cartDao.getAddOnPrice(itemId)
            }.await()

            val cartPrice = async(ioDispatcher) {
                cartPriceDao.getCartPriceByOrderId(orderId)
            }.await()

            val basePrice = cartPrice.basePrice + addOnPrice.itemPrice

            val discountPrice = if (!addOnPrice.isApplicable) {
                cartPrice.discountPrice + addOnPrice.itemPrice
            } else {
                cartPrice.discountPrice
            }

            val totalPrice = basePrice - discountPrice

            val priceEntity = CartPriceEntity(
                orderId = orderId,
                basePrice = basePrice,
                discountPrice = discountPrice,
                totalPrice = totalPrice,
                createdAt = System.currentTimeMillis().toString(),
            )

            cartPriceDao.updateCartPrice(priceEntity)
        }
    }

    private suspend fun removeAddOnItemPrice(orderId: Int, itemId: Int): Int {
        return withContext(ioDispatcher) {
            val addOnPrice = async(ioDispatcher) {
                cartDao.getAddOnPrice(itemId)
            }.await()

            val cartPrice = async(ioDispatcher) {
                cartPriceDao.getCartPriceByOrderId(orderId)
            }.await()

            val basePrice = cartPrice.basePrice - addOnPrice.itemPrice

            val discountPrice = if (!addOnPrice.isApplicable) {
                cartPrice.discountPrice - addOnPrice.itemPrice
            } else {
                cartPrice.discountPrice
            }

            val totalPrice = basePrice - discountPrice

            val priceEntity = CartPriceEntity(
                orderId = orderId,
                basePrice = basePrice,
                discountPrice = discountPrice,
                totalPrice = totalPrice,
                createdAt = System.currentTimeMillis().toString(),
            )

            cartPriceDao.updateCartPrice(priceEntity)
        }
    }

    private suspend fun insertChargesPrice(orderId: Int, chargesId: Int): Int {
        return withContext(ioDispatcher) {
            val chargesPrice = async(ioDispatcher) {
                cartDao.getChargesPrice(chargesId)
            }.await()

            val cartPrice = async(ioDispatcher) {
                cartPriceDao.getCartPriceByOrderId(orderId)
            }.await()

            val basePrice = cartPrice.basePrice + chargesPrice.chargesPrice

            val discountPrice = cartPrice.discountPrice

            val totalPrice = basePrice - discountPrice

            val priceEntity = CartPriceEntity(
                orderId = orderId,
                basePrice = basePrice,
                discountPrice = discountPrice,
                totalPrice = totalPrice,
            )

            cartPriceDao.updateCartPrice(priceEntity)
        }
    }

    private suspend fun removeChargesPrice(orderId: Int, chargesId: Int): Int {
        return withContext(ioDispatcher) {
            val chargesPrice = async(ioDispatcher) {
                cartDao.getChargesPrice(chargesId)
            }.await()

            val cartPrice = async(ioDispatcher) {
                cartPriceDao.getCartPriceByOrderId(orderId)
            }.await()

            val basePrice = cartPrice.basePrice - chargesPrice.chargesPrice

            val discountPrice = cartPrice.discountPrice

            val totalPrice = basePrice - discountPrice

            val priceEntity = CartPriceEntity(
                orderId = orderId,
                basePrice = basePrice,
                discountPrice = discountPrice,
                totalPrice = totalPrice,
            )

            cartPriceDao.updateCartPrice(priceEntity)
        }
    }
}
