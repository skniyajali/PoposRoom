package com.niyaj.poposroom.features.order.data.repository

import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.cart.domain.model.CartProductItem
import com.niyaj.poposroom.features.cart.domain.model.OrderPrice
import com.niyaj.poposroom.features.cart.domain.model.OrderWithCart
import com.niyaj.poposroom.features.cart_order.data.dao.CartOrderDao
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrder
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderType
import com.niyaj.poposroom.features.charges.domain.model.Charges
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.calculateEndDate
import com.niyaj.poposroom.features.common.utils.calculateStartDate
import com.niyaj.poposroom.features.common.utils.getEndDateLong
import com.niyaj.poposroom.features.common.utils.getStartDateLong
import com.niyaj.poposroom.features.customer.domain.model.Customer
import com.niyaj.poposroom.features.order.data.dao.OrderDao
import com.niyaj.poposroom.features.order.domain.model.Order
import com.niyaj.poposroom.features.order.domain.model.OrderDetails
import com.niyaj.poposroom.features.order.domain.model.searchOrder
import com.niyaj.poposroom.features.order.domain.repository.OrderRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class OrderRepositoryImpl(
    private val orderDao: OrderDao,
    private val cartOrderDao: CartOrderDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : OrderRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllOrders(date: String, searchText: String): Flow<List<Order>> {
        return withContext(ioDispatcher) {
            val startDate = if (date.isNotEmpty()) {
                calculateStartDate(date)
            } else getStartDateLong

            val endDate = if (date.isNotEmpty()) {
                calculateEndDate(date)
            }else getEndDateLong

            orderDao.getAllOrders(startDate, endDate).mapLatest { list ->
                mapCartItemToOrder(list)
            }.mapLatest {
                it.searchOrder(searchText)
            }
        }
    }

    override suspend fun getAllCharges(): Flow<List<Charges>> {
        return withContext(ioDispatcher) {
            orderDao.getAllCharges()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getOrderDetails(orderId: Int): Flow<OrderDetails> {
        return withContext(ioDispatcher) {
            orderDao.getOrderDetails(orderId).mapLatest {
                mapCartItemToOrderDetails(it)
            }
        }
    }

    override suspend fun deleteOrder(orderId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = cartOrderDao.deleteCartOrder(orderId)

                Resource.Success(result > 0)
            }
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun markOrderAsProcessing(orderId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = cartOrderDao.markAsProcessing(orderId)

                Resource.Success(result > 0)
            }
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    private suspend fun mapCartItemToOrder(cartOrders: List<OrderWithCart>): List<Order> {
        return withContext(ioDispatcher) {
            cartOrders.map { order ->
                var totalPrice = 0
                val discountPrice = 0

                async(ioDispatcher) {
                    order.cartItems.map { cartItem ->
                        val productPrice = orderDao.getProductPriceById(cartItem.productId)

                        totalPrice += productPrice.times(cartItem.quantity)
                    }
                }.await()

                val addressName = async(ioDispatcher) {
                    if (order.cartOrder.orderType != OrderType.DineIn) {
                        orderDao.getAddressNameById(order.cartOrder.addressId)
                    } else null
                }

                val customerPhone = async(ioDispatcher) {
                    if (order.cartOrder.orderType != OrderType.DineIn) {
                        orderDao.getCustomerPhoneById(order.cartOrder.customerId)
                    } else null
                }

                Order(
                    orderId = order.cartOrder.orderId,
                    orderType = order.cartOrder.orderType,
                    customerPhone = customerPhone.await(),
                    customerAddress = addressName.await(),
                    orderDate = (order.cartOrder.updatedAt ?: order.cartOrder.createdAt),
                    orderPrice = OrderPrice(totalPrice, discountPrice)
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun mapCartItemToOrderDetails(order: OrderWithCart): OrderDetails {
        return withContext(ioDispatcher) {
            var totalPrice = 0
            val discountPrice = 0

            val addOnItems = async(ioDispatcher) {
                cartOrderDao.getCartAddOnItemsId(order.cartOrder.orderId).mapLatest { list ->
                    list.map {
                        withContext(ioDispatcher){
                            cartOrderDao.getAddOnItemById(it)
                        }
                    }
                }
            }

            val charges = async(ioDispatcher) {
                cartOrderDao.getCartChargesId(order.cartOrder.orderId).mapLatest { list ->
                    list.map {
                        cartOrderDao.getChargesById(it)
                    }
                }
            }

            val cartProducts = async(ioDispatcher) {
                order.cartItems.map { cartItem ->
                    val product = orderDao.getProductById(cartItem.productId)

                    totalPrice += product.productPrice.times(cartItem.quantity)

                    CartProductItem(
                        productId = product.productId,
                        productName = product.productName,
                        productPrice = product.productPrice,
                        productQuantity = cartItem.quantity
                    )
                }
            }

            val address = async(ioDispatcher) {
                if (order.cartOrder.orderType != OrderType.DineIn) {
                    orderDao.getAddressById(order.cartOrder.addressId)
                } else Address()
            }

            val customer = async(ioDispatcher) {
                if (order.cartOrder.orderType != OrderType.DineIn) {
                    orderDao.getCustomerById(order.cartOrder.customerId)
                } else Customer()
            }

            val cartOrder = CartOrder(
                orderId = order.cartOrder.orderId,
                orderType = order.cartOrder.orderType,
                orderStatus = order.cartOrder.orderStatus,
                doesChargesIncluded = order.cartOrder.doesChargesIncluded,
                customer = customer.await(),
                address = address.await(),
                createdAt = order.cartOrder.createdAt,
                updatedAt = order.cartOrder.updatedAt
            )

            OrderDetails(
                cartOrder = cartOrder,
                cartProducts = cartProducts.await(),
                addOnItems = addOnItems.await().distinctUntilChanged(),
                charges = charges.await().distinctUntilChanged(),
                orderPrice = countTotalPrice(
                    cartOrder.orderId,
                    cartOrder.doesChargesIncluded,
                    cartOrder.orderType
                )
            )
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
                val data = cartOrderDao.getCartCharges(orderId)

                cartOrderDao.getChargesPrice(data).forEach {
                    totalPrice += it.chargesPrice
                }
            }.await()

            async(ioDispatcher) {
                if (included) {
                    cartOrderDao.getAllChargesPrice().forEach { it ->
                        if (it.isApplicable && orderType == OrderType.DineOut) {
                            totalPrice += it.chargesPrice
                        }
                    }
                }
            }.await()

            async(ioDispatcher) {
                val data = cartOrderDao.getCartProductsByOrderId(orderId)

                data.cartItems.forEach {
                    val result = cartOrderDao.getProductPriceAndQuantity(data.cartOrder.orderId, it.productId)

                    totalPrice += result.productPrice.times(it.quantity)
                }


            }.await()
        }

        return OrderPrice(totalPrice, discountPrice)
    }

}