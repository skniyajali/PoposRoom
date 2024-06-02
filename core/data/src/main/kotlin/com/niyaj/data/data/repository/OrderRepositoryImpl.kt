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
import com.niyaj.common.utils.calculateEndDate
import com.niyaj.common.utils.calculateStartDate
import com.niyaj.common.utils.getEndDateLong
import com.niyaj.common.utils.getStartDateLong
import com.niyaj.data.repository.OrderRepository
import com.niyaj.database.dao.CartOrderDao
import com.niyaj.database.dao.OrderDao
import com.niyaj.database.dao.SelectedDao
import com.niyaj.database.model.OrderDetailsDto
import com.niyaj.database.model.SelectedEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.Order
import com.niyaj.model.OrderDetails
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

    override suspend fun getDineInOrders(date: String, searchText: String): Flow<List<Order>> {
        return withContext(ioDispatcher) {
            val startDate = if (date.isNotEmpty()) {
                calculateStartDate(date)
            } else {
                getStartDateLong
            }

            val endDate = if (date.isNotEmpty()) {
                calculateEndDate(date)
            } else {
                getEndDateLong
            }

            orderDao.getAllOrder(startDate, endDate, OrderType.DineIn).mapLatest {
                it.searchOrder(searchText)
            }
        }
    }

    override suspend fun getDineOutOrders(date: String, searchText: String): Flow<List<Order>> {
        return withContext(ioDispatcher) {
            val startDate = if (date.isNotEmpty()) {
                calculateStartDate(date)
            } else {
                getStartDateLong
            }

            val endDate = if (date.isNotEmpty()) {
                calculateEndDate(date)
            } else {
                getEndDateLong
            }

            orderDao.getAllOrder(startDate, endDate, OrderType.DineOut).mapLatest {
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
            orderDao.getFullOrderDetails(orderId).mapLatest {
                mapOrderDetailsDtoToOrderDetails(it)
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

    private suspend fun mapOrderDetailsDtoToOrderDetails(order: OrderDetailsDto): OrderDetails {
        return withContext(ioDispatcher) {
            val cartProducts = async(ioDispatcher) {
                order.cartItems.map { cartItem ->
                    val product = orderDao.getProductById(cartItem.productId)
                    CartProductItem(
                        productId = product.productId,
                        productName = product.productName,
                        productPrice = product.productPrice,
                        productQuantity = cartItem.quantity,
                    )
                }
            }

            OrderDetails(
                cartOrder = order.cartOrder.toExternalModel(),
                cartProducts = cartProducts.await().toImmutableList(),
                addOnItems = order.addOnItems.map { it.asExternalModel() }.toImmutableList(),
                charges = order.charges.map { it.asExternalModel() }.toImmutableList(),
                orderPrice = order.orderPrice,
                deliveryPartner = order.deliveryPartner,
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
}
