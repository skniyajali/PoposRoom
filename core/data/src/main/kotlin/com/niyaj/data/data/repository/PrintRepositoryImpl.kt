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
import com.niyaj.common.utils.calculateEndDate
import com.niyaj.common.utils.calculateStartDate
import com.niyaj.common.utils.getEndDateLong
import com.niyaj.common.utils.getStartDateLong
import com.niyaj.data.repository.PrintRepository
import com.niyaj.database.dao.PrintDao
import com.niyaj.database.model.OrderDetailsDto
import com.niyaj.database.model.asExternalModel
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.CartProductItem
import com.niyaj.model.ChargesNameAndPrice
import com.niyaj.model.DeliveryReport
import com.niyaj.model.OrderDetails
import com.niyaj.model.Profile
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PrintRepositoryImpl @Inject constructor(
    private val printDao: PrintDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : PrintRepository {

    override suspend fun getOrderDetails(orderId: Int): OrderDetails {
        return withContext(ioDispatcher) {
            mapOrderDetailsDtoToOrderDetails(printDao.getFullOrderDetails(orderId))
        }
    }

    override suspend fun getDeliveryReports(date: String, partnerId: Int?): List<DeliveryReport> {
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

            printDao.getDeliveryReport(startDate, endDate, partnerId)
        }
    }

    override suspend fun getCharges(): List<ChargesNameAndPrice> {
        return withContext(ioDispatcher) {
            printDao.getAllCharges()
        }
    }

    override fun getProfileInfo(restaurantId: Int): Flow<Profile> {
        return printDao.getProfileInfo(restaurantId).mapLatest {
            it?.asExternalModel() ?: Profile.defaultProfileInfo
        }
    }

    private suspend fun mapOrderDetailsDtoToOrderDetails(order: OrderDetailsDto): OrderDetails {
        return withContext(ioDispatcher) {
            val cartProducts = async(ioDispatcher) {
                order.cartItems.map { cartItem ->
                    val product = printDao.getProductById(cartItem.productId)
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
}
