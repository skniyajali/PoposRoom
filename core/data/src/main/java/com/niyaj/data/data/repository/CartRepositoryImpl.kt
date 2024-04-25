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
import com.niyaj.database.model.SelectedEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartItem
import com.niyaj.model.CartProductItem
import com.niyaj.model.OrderType
import com.niyaj.model.OrderWithCartItems
import com.niyaj.model.SELECTED_ID
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
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

    override suspend fun getAllCartOrders(): Flow<List<OrderWithCartItems>> {
        return flow { }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllDineInCart(): Flow<List<CartItem>> {
        return withContext(ioDispatcher) {
            cartDao.getAllOrders(OrderType.DineIn).mapLatest { list ->
                val data = list.filter { it.cartItems.isNotEmpty() }

                mapCartOrderToCartItem(data)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllDineOutCart(): Flow<List<CartItem>> {
        return withContext(ioDispatcher) {
            cartDao.getAllOrders(OrderType.DineOut).mapLatest { list ->
                val data = list.filter { it.cartItems.isNotEmpty() }

                mapCartOrderToCartItem(data)
            }
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
                        quantity = 1
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

    private suspend fun mapCartOrderToCartItem(cartOrders: List<CartItemDto>): List<CartItem> {
        return coroutineScope {
            cartOrders.map { order ->
                val cartProducts = async(ioDispatcher) {
                    order.cartItems.map { cartItem ->
                        val product = cartDao.getProductById(cartItem.productId)

                        CartProductItem(
                            productId = product.productId,
                            productName = product.productName,
                            productPrice = product.productPrice,
                            productQuantity = cartItem.quantity
                        )
                    }
                }

                CartItem(
                    orderId = order.cartOrder.orderId,
                    orderType = order.cartOrder.orderType,
                    cartProducts = cartProducts.await().toImmutableList(),
                    addOnItems = order.addOnItems.toImmutableList(),
                    charges = order.charges.toImmutableList(),
                    customerPhone = order.customerPhone,
                    customerAddress = order.customerAddress,
                    updatedAt = (order.cartOrder.updatedAt ?: order.cartOrder.createdAt).toTimeSpan,
                    orderPrice = order.orderPrice.totalPrice
                )
            }
        }
    }

    private suspend fun updateOrDeleteSelectedOrder() {
        withContext(ioDispatcher) {
            val lastId = cartOrderDao.getLastProcessingId()

            lastId?.let {
                selectedDao.insertOrUpdateSelectedOrder(
                    SelectedEntity(
                        selectedId = SELECTED_ID,
                        orderId = it
                    )
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
                createdAt = System.currentTimeMillis().toString()
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
                createdAt = System.currentTimeMillis().toString()
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
            } else cartPrice.discountPrice

            val totalPrice = basePrice - discountPrice

            val priceEntity = CartPriceEntity(
                orderId = orderId,
                basePrice = basePrice,
                discountPrice = discountPrice,
                totalPrice = totalPrice,
                createdAt = System.currentTimeMillis().toString()
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
            } else cartPrice.discountPrice

            val totalPrice = basePrice - discountPrice

            val priceEntity = CartPriceEntity(
                orderId = orderId,
                basePrice = basePrice,
                discountPrice = discountPrice,
                totalPrice = totalPrice,
                createdAt = System.currentTimeMillis().toString()
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
                totalPrice = totalPrice
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
                totalPrice = totalPrice
            )

            cartPriceDao.updateCartPrice(priceEntity)
        }
    }
}