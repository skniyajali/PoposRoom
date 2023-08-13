package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.ReportsRepository
import com.niyaj.database.dao.ReportsDao
import com.niyaj.database.model.ReportsEntity
import com.niyaj.database.model.asExternalModel
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.CartOrder
import com.niyaj.model.CategoryWiseReport
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.ProductAndQuantity
import com.niyaj.model.ProductWiseReport
import com.niyaj.model.Reports
import com.niyaj.model.TotalOrders
import com.niyaj.model.TotalSales
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class ReportsRepositoryImpl(
    private val reportsDao: ReportsDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ReportsRepository {

    override suspend fun generateReport(startDate: String, endDate: String): Resource<Boolean> {
        return try {
            val itemReports = getItemsReport(startDate.toLong(), endDate.toLong())

            val findReport = withContext(ioDispatcher) {
                reportsDao.findReportExists(startDate)
            }

            val newReport = ReportsEntity(
                reportId = findReport ?: 0,
                expensesQty = itemReports.expenses.totalExpenses,
                expensesAmount = itemReports.expenses.totalExpenses,
                dineInSalesQty = itemReports.dineInOrders.totalOrders,
                dineInSalesAmount = itemReports.dineInOrders.totalAmount,
                dineOutSalesQty = itemReports.dineOutOrders.totalOrders,
                dineOutSalesAmount = itemReports.dineOutOrders.totalAmount,
                reportDate = startDate,
                updatedAt = if (findReport != null) System.currentTimeMillis().toString() else null,
            )

            val result = withContext(ioDispatcher) {
                reportsDao.updateOrInsertReport(newReport)
            }

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun getReportByReportDate(reportDate: String): Flow<Reports?> {
        return withContext(ioDispatcher) {
            reportsDao.getReportByReportDate(reportDate).mapLatest { it?.toExternalModel() }
        }
    }

    override suspend fun getReports(startDate: String): Flow<List<Reports>> {
        return withContext(ioDispatcher) {
            reportsDao.getReports(startDate).mapLatest { list ->
                list.map {
                    it.toExternalModel()
                }
            }
        }
    }

    override suspend fun getProductWiseReport(
        startDate: String,
        endDate: String,
        orderType: String,
    ): Flow<List<ProductWiseReport>> {
        return withContext(ioDispatcher) {
            reportsDao.getProductWiseOrder(startDate.toLong(), endDate.toLong())
                .mapLatest { it ->
                    it.filter {
                        if (orderType.isNotEmpty()) {
                            it.cartOrderEntity.orderType.name == orderType
                        } else true
                    }.flatMap { orders ->
                        orders.cartItems.map { product ->
                            ProductAndQuantity(
                                productId = product.productId,
                                quantity = product.quantity,
                            )
                        }
                    }.groupBy { it.productId }.map { groupedProducts ->
                        ProductWiseReport(
                            productId = groupedProducts.key,
                            productName = withContext(ioDispatcher) {
                                reportsDao.getProductNameById(groupedProducts.key)
                            },
                            quantity = groupedProducts.value.sumOf { it.quantity }
                        )
                    }.sortedByDescending { it.quantity }
                }
        }
    }

    override suspend fun getCategoryWiseReport(
        startDate: String,
        endDate: String,
        orderType: String,
    ): Flow<List<CategoryWiseReport>> {
        return withContext(ioDispatcher) {
            reportsDao.getProductWiseOrder(startDate.toLong(), endDate.toLong())
                .mapLatest { orders ->
                    orders.filter {
                        orderType.isEmpty() || it.cartOrderEntity.orderType.name == orderType
                    }.flatMap { order ->
                        order.cartItems.map { cartItem -> cartItem.productId to cartItem.quantity }
                    }
                        .groupBy { (productId, _) ->
                            reportsDao.getProductCategoryById(productId)
                        }
                        .map { (category, productAndQuantityList) ->
                            CategoryWiseReport(
                                category = reportsDao.getCategoryById(category).asExternalModel(),
                                productWithQuantity = productAndQuantityList
                                    .groupBy { (productId, _) -> productId }
                                    .map { (productId, productAndQuantityList) ->
                                        ProductWiseReport(
                                            productId = productId,
                                            productName = withContext(ioDispatcher) {
                                                reportsDao.getProductNameById(productId)
                                            },
                                            quantity = productAndQuantityList.sumOf { (_, quantity) -> quantity }
                                        )
                                    }.sortedByDescending { it.quantity }
                            )
                        }.sortedByDescending { it -> it.productWithQuantity.sumOf { it.quantity } }
                }
        }
    }

    override suspend fun getDineOutOrders(
        startDate: String,
        endDate: String,
    ): Flow<List<CartOrder>> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLastSevenDaysBeforeData(): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getAddressWiseReport(
        startDate: String,
        endDate: String,
    ): Flow<List<AddressWiseReport>> {
        return withContext(ioDispatcher) {
            reportsDao.getAddressWiseOrder(startDate.toLong(), endDate.toLong()).mapLatest { list ->
                list.groupBy {
                    it
                }.map {
                    AddressWiseReport(
                        address = withContext(ioDispatcher) {
                            reportsDao.getAddressById(it.key).asExternalModel()
                        },
                        orderQty = it.value.size
                    )
                }
            }
        }
    }

    override suspend fun getCustomerWiseReport(
        startDate: String,
        endDate: String,
    ): Flow<List<CustomerWiseReport>> {
        return withContext(ioDispatcher) {
            reportsDao.getCustomerWiseOrder(startDate.toLong(), endDate.toLong())
                .mapLatest { list ->
                    list.groupBy { it }.map {
                        CustomerWiseReport(
                            customer = withContext(ioDispatcher) {
                                reportsDao.getCustomerById(it.key).asExternalModel()
                            },
                            orderQty = it.value.size
                        )
                    }
                }
        }
    }

    private suspend fun getItemsReport(startDate: Long, endDate: Long): TotalSales {
        return withContext(ioDispatcher) {
            val expenses = async(ioDispatcher) {
                reportsDao.getTotalExpenses(startDate, endDate)
            }

            val dineInOrder = async(ioDispatcher) {
                val result = reportsDao.getTotalDineInOrders(startDate, endDate)

                TotalOrders(
                    totalOrders = result.size.toLong(),
                    totalAmount = result.sumOf { it.orderPrice.totalPrice }
                )
            }

            val dineOutOrder = async(ioDispatcher) {
                val result = reportsDao.getTotalDineOutOrders(startDate, endDate)

                TotalOrders(
                    totalOrders = result.size.toLong(),
                    totalAmount = result.sumOf { it.orderPrice.totalPrice }
                )
            }

            TotalSales(
                expenses = expenses.await(),
                dineInOrders = dineInOrder.await(),
                dineOutOrders = dineOutOrder.await()
            )
        }
    }
}