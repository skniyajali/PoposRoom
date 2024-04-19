package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.utils.calculateEndDate
import com.niyaj.common.utils.calculateStartDate
import com.niyaj.common.utils.getEndDateLong
import com.niyaj.common.utils.getStartDateLong
import com.niyaj.data.repository.PrintRepository
import com.niyaj.database.dao.OrderDao
import com.niyaj.database.dao.PrintDao
import com.niyaj.database.model.CartItemDto
import com.niyaj.database.model.DeliveryReportDto
import com.niyaj.database.model.asExternalModel
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import com.niyaj.model.ChargesNameAndPrice
import com.niyaj.model.Customer
import com.niyaj.model.DeliveryReport
import com.niyaj.model.OrderDetails
import com.niyaj.model.OrderType
import com.niyaj.model.Profile
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class PrintRepositoryImpl(
    private val printDao: PrintDao,
    private val orderDao: OrderDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : PrintRepository {

    override suspend fun getOrderDetails(orderId: Int): OrderDetails {
        return withContext(ioDispatcher) {
            mapCartItemToOrderDetails(printDao.getOrderDetails(orderId))
        }
    }

    override suspend fun getDeliveryReports(date: String): List<DeliveryReport> {
        return withContext(ioDispatcher) {
            val startDate = if (date.isNotEmpty()) {
                calculateStartDate(date)
            } else getStartDateLong

            val endDate = if (date.isNotEmpty()) {
                calculateEndDate(date)
            } else getEndDateLong

            printDao.getDeliveryReports(startDate, endDate).map(DeliveryReportDto::toExternalModel)
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
                orderPrice = order.orderPrice.toExternalModel()
            )
        }
    }

}