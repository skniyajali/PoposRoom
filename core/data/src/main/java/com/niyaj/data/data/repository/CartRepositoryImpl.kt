package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.toTimeSpan
import com.niyaj.data.repository.CartRepository
import com.niyaj.database.dao.CartDao
import com.niyaj.database.dao.CartOrderDao
import com.niyaj.database.dao.SelectedDao
import com.niyaj.database.model.CartAddOnItemsEntity
import com.niyaj.database.model.CartChargesEntity
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.OrderWithCartDto
import com.niyaj.database.model.SelectedEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartItem
import com.niyaj.model.CartItems
import com.niyaj.model.CartProductItem
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType
import com.niyaj.model.OrderWithCartItems
import com.niyaj.model.SELECTED_ID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class CartRepositoryImpl(
    private val cartDao: CartDao,
    private val cartOrderDao: CartOrderDao,
    private val selectedDao: SelectedDao,
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

                    Resource.Success(result > 0)

                } else {
                    val newCartEntity = CartEntity(
                        orderId = orderId,
                        productId = productId,
                        quantity = 1
                    )

                    val result = cartDao.addOrRemoveCartProduct(newCartEntity)

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

                        Resource.Success(result > 0)
                    } else {
                        val qty = cart.quantity - 1

                        val result = cartDao.updateQuantity(orderId, productId, qty)

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
                    cartOrderDao.deleteCartAddOnItem(orderId, itemId)
                } else {
                    cartOrderDao.insertCartAddOnItem(CartAddOnItemsEntity(orderId, itemId)).toInt()
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
                } else {
                    cartOrderDao.insertCartCharge(CartChargesEntity(orderId, chargesId)).toInt()
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

    private suspend fun countTotalPrice(
        orderId: Int,
        included: Boolean,
        orderType: OrderType,
    ): OrderPrice {
        var totalPrice = 0
        var discountPrice = 0

        withContext(ioDispatcher) {
            async(ioDispatcher) {
                val data = cartOrderDao.getCartAddOnItems(orderId)
                withContext(ioDispatcher) {
                    cartOrderDao.getAddOnPrice(data).forEach {
                        totalPrice += it.itemPrice

                        if (!it.isApplicable) {
                            discountPrice += it.itemPrice
                        }
                    }
                }
            }.await()

            async(ioDispatcher) {
                cartOrderDao.getCartCharges(orderId).forEach { data ->
                    totalPrice += data
                }
            }.await()

            async(ioDispatcher) {
                if (included) {
                    cartOrderDao.getAllChargesPrice().forEach {
                        if (it.isApplicable && orderType == OrderType.DineOut) {
                            totalPrice += it.chargesPrice
                        }
                    }
                }
            }.await()

            async(ioDispatcher) {
                val data = cartOrderDao.getCartProductsByOrderId(orderId)

                data.cartItems.forEach {
                    val result = cartOrderDao.getProductPriceAndQuantity(
                        data.cartOrder.orderId,
                        it.productId
                    )

                    totalPrice += result.productPrice.times(it.quantity)
                }


            }.await()
        }

        return OrderPrice(totalPrice, discountPrice)
    }


    private suspend fun mapCartOrderToCartItem(cartOrders: List<OrderWithCartDto>): List<CartItem> {
        return coroutineScope {
            cartOrders.map { order ->

                val addOnItems = async {
                    cartOrderDao.getCartAddOnItemsId(order.cartOrder.orderId)
                }

                val charges = async(ioDispatcher) {
                    cartOrderDao.getCartChargesId(order.cartOrder.orderId)
                }

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

                val addressName = async(ioDispatcher) {
                    if (order.cartOrder.orderType != OrderType.DineIn) {
                        cartDao.getAddressById(order.cartOrder.addressId)
                    } else null
                }

                val customerPhone = async(ioDispatcher) {
                    if (order.cartOrder.orderType != OrderType.DineIn) {
                        cartDao.getCustomerById(order.cartOrder.customerId)
                    } else null
                }

                val orderPrice = addOnItems.await().combine(charges.await()) { _, _ ->
                    countTotalPrice(
                        order.cartOrder.orderId,
                        order.cartOrder.doesChargesIncluded,
                        order.cartOrder.orderType
                    )
                }

                CartItem(
                    orderId = order.cartOrder.orderId,
                    orderType = order.cartOrder.orderType,
                    cartProducts = cartProducts.await(),
                    addOnItems = addOnItems.await().distinctUntilChanged(),
                    charges = charges.await().distinctUntilChanged(),
                    customerPhone = customerPhone.await(),
                    customerAddress = addressName.await(),
                    updatedAt = (order.cartOrder.updatedAt ?: order.cartOrder.createdAt).toTimeSpan,
                    orderPrice = orderPrice
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun mapCartOrderToCartItems(cartOrders: List<OrderWithCartDto>): List<CartItems> {
        return coroutineScope {
            cartOrders.map { order ->

                var totalPrice = 0
                var discountPrice = 0

                val addOnItems = async {
                    cartOrderDao.getCartAddOnItemsId(order.cartOrder.orderId).mapLatest { list ->
                        withContext(ioDispatcher) {
                            cartOrderDao.getAddOnPrice(list).forEach {
                                if (it.isApplicable) {
                                    totalPrice += it.itemPrice
                                } else {
                                    discountPrice += it.itemPrice
                                }
                            }
                        }
                        list
                    }.stateIn(
                        scope = this,
                        started = SharingStarted.WhileSubscribed(),
                        initialValue = emptyList()
                    )
                }

                val charges = async(ioDispatcher) {
                    cartOrderDao.getCartChargesId(order.cartOrder.orderId).mapLatest { list ->
                        withContext(ioDispatcher) {
                            if (order.cartOrder.doesChargesIncluded) {
                                cartOrderDao.getAllChargesPrice().forEach {
                                    if (it.isApplicable && order.cartOrder.orderType == OrderType.DineOut) {
                                        totalPrice += it.chargesPrice
                                    }
                                }
                            }
                        }

                        list
                    }.stateIn(
                        scope = this,
                        started = SharingStarted.WhileSubscribed(),
                        initialValue = emptyList()
                    )
                }

                val cartProducts = async(ioDispatcher) {
                    order.cartItems.map { cartItem ->
                        val product = cartDao.getProductById(cartItem.productId)

                        totalPrice += product.productPrice.times(cartItem.quantity)

                        CartProductItem(
                            productId = product.productId,
                            productName = product.productName,
                            productPrice = product.productPrice,
                            productQuantity = cartItem.quantity
                        )
                    }
                }

                val addressName = async(ioDispatcher) {
                    if (order.cartOrder.orderType != OrderType.DineIn) {
                        cartDao.getAddressById(order.cartOrder.addressId)
                    } else null
                }

                val customerPhone = async(ioDispatcher) {
                    if (order.cartOrder.orderType != OrderType.DineIn) {
                        cartDao.getCustomerById(order.cartOrder.customerId)
                    } else null
                }

                CartItems(
                    orderId = order.cartOrder.orderId,
                    orderType = order.cartOrder.orderType,
                    cartProducts = cartProducts.await(),
                    addOnItems = addOnItems.await().value,
                    charges = charges.await().value,
                    customerPhone = customerPhone.await(),
                    customerAddress = addressName.await(),
                    updatedAt = (order.cartOrder.updatedAt ?: order.cartOrder.createdAt).toTimeSpan,
                    orderPrice = OrderPrice(
                        totalPrice = totalPrice,
                        discountPrice = discountPrice
                    )
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

}