package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.calculateEndDate
import com.niyaj.common.utils.calculateStartDate
import com.niyaj.common.utils.getEndDateLong
import com.niyaj.common.utils.getStartDateLong
import com.niyaj.data.repository.OrderRepository
import com.niyaj.database.dao.CartOrderDao
import com.niyaj.database.dao.OrderDao
import com.niyaj.database.dao.SelectedDao
import com.niyaj.database.model.CartItemDto
import com.niyaj.database.model.OrderDto
import com.niyaj.database.model.SelectedEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.Customer
import com.niyaj.model.Order
import com.niyaj.model.OrderDetails
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType
import com.niyaj.model.SELECTED_ID
import com.niyaj.model.searchOrder
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext


class OrderRepositoryImpl(
    private val orderDao: OrderDao,
    private val cartOrderDao: CartOrderDao,
    private val selectedDao: SelectedDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : OrderRepository {

    override suspend fun getAllOrders(date: String, searchText: String): Flow<List<Order>> {
        return withContext(ioDispatcher) {
            val startDate = if (date.isNotEmpty()) {
                calculateStartDate(date)
            } else getStartDateLong

            val endDate = if (date.isNotEmpty()) {
                calculateEndDate(date)
            } else getEndDateLong

            orderDao.getAllOrders(startDate, endDate).mapLatest { list ->
                mapCartItemToOrder(list)
            }.mapLatest {
                it.searchOrder(searchText)
            }
        }
    }

    override suspend fun getAllOrders(
        date: String,
        orderType: OrderType,
        searchText: String
    ): Flow<List<Order>> {
        return withContext(ioDispatcher) {
            val startDate = if (date.isNotEmpty()) {
                calculateStartDate(date)
            } else getStartDateLong

            val endDate = if (date.isNotEmpty()) {
                calculateEndDate(date)
            } else getEndDateLong

            orderDao.getAllOrders(startDate, endDate, orderType).mapLatest { list ->
                mapCartItemToOrder(list)
            }.mapLatest {
                it.searchOrder(searchText)
            }
        }
    }

    override suspend fun getAllCharges(): Flow<List<Charges>> {
        return withContext(ioDispatcher) {
            orderDao.getAllCharges().mapLatest { it ->
                it.map { it.asExternalModel() }
            }
        }
    }

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

                async {
                    updateOrDeleteSelectedOrder()
                }.await()

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun markOrderAsProcessing(orderId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = cartOrderDao.markAsProcessing(orderId)

                async {
                    updateOrDeleteSelectedOrder()
                }.await()

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    private suspend fun mapCartItemToOrder(cartOrders: List<OrderDto>): List<Order> {
        return withContext(ioDispatcher) {
            cartOrders.map { order ->
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
                    orderPrice = order.orderPrice.toExternalModel()
                )
            }
        }
    }

    private suspend fun mapCartItemToOrderDetails(order: CartItemDto): OrderDetails {
        return withContext(ioDispatcher) {
            val addOnItems = async(ioDispatcher) {
                order.addOnItems.map {
                    orderDao.getAddOnItemById(it).asExternalModel()
                }
            }

            val charges = async(ioDispatcher) {
                order.charges.map {
                    orderDao.getChargesById(it).asExternalModel()
                }
            }

            val cartProducts = async(ioDispatcher) {
                order.cartItems.map { cartItem ->
                    val product = orderDao.getProductById(cartItem.productId)
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
                    orderDao.getAddressById(order.cartOrder.addressId).asExternalModel()
                } else Address()
            }

            val customer = async(ioDispatcher) {
                if (order.cartOrder.orderType != OrderType.DineIn) {
                    orderDao.getCustomerById(order.cartOrder.customerId).asExternalModel()
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
                cartProducts = cartProducts.await().toImmutableList(),
                addOnItems = addOnItems.await().toImmutableList(),
                charges = charges.await().toImmutableList(),
                orderPrice = OrderPrice(totalPrice = order.orderPrice.totalPrice)
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
                        orderId = it
                    )
                )
            } ?: selectedDao.deleteSelectedOrder(SELECTED_ID)
        }
    }

}