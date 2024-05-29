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
import com.niyaj.common.utils.calculateEndOfDayTime
import com.niyaj.common.utils.getStartTime
import com.niyaj.data.repository.ReportsRepository
import com.niyaj.database.dao.ReportsDao
import com.niyaj.database.model.ReportsEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.CategoryWiseReport
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.ExpensesReport
import com.niyaj.model.OrderType
import com.niyaj.model.ProductWiseReport
import com.niyaj.model.Reports
import com.niyaj.model.TotalSales
import kotlinx.collections.immutable.toImmutableList
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

    override suspend fun generateReport(reportDate: String): Resource<Boolean> {
        return try {
            val endDate: String = calculateEndOfDayTime(date = reportDate)

            val itemReports = getItemsReport(reportDate.toLong(), endDate.toLong())

            val findReport = withContext(ioDispatcher) {
                reportsDao.findReportExists(reportDate)
            }

            val newReport = ReportsEntity(
                reportId = findReport ?: 0,
                expensesQty = itemReports.expenses.totalQuantity,
                expensesAmount = itemReports.expenses.totalExpenses,
                dineInSalesQty = itemReports.dineInOrders.totalOrders,
                dineInSalesAmount = itemReports.dineInOrders.totalAmount,
                dineOutSalesQty = itemReports.dineOutOrders.totalOrders,
                dineOutSalesAmount = itemReports.dineOutOrders.totalAmount,
                reportDate = reportDate,
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

    override suspend fun getReportByReportDate(reportDate: String): Flow<Reports> {
        return withContext(ioDispatcher) {
            reportsDao.getReportByReportDate(reportDate)
                .mapLatest { it?.toExternalModel() ?: Reports() }
        }
    }

    override suspend fun getReports(): Flow<List<Reports>> {
        return withContext(ioDispatcher) {
            reportsDao.getReports().mapLatest { list ->
                list.map {
                    it.toExternalModel()
                }
            }
        }
    }

    override suspend fun getProductWiseReport(
        startDate: String,
        orderType: String,
    ): Flow<List<ProductWiseReport>> {
        return withContext(ioDispatcher) {
            val endDate: String = calculateEndOfDayTime(date = startDate.ifEmpty { getStartTime })
            reportsDao.getProductWiseOrders(
                startDate.toLong(),
                endDate.toLong(),
                orderType.ifEmpty { null },
            )
        }
    }

    override suspend fun getCategoryWiseReport(
        startDate: String,
        orderType: String,
    ): Flow<List<CategoryWiseReport>> {
        return withContext(ioDispatcher) {
            val endDate: String = calculateEndOfDayTime(date = startDate.ifEmpty { getStartTime })

            reportsDao.getCategoryWiseOrders(
                startDate.toLong(),
                endDate.toLong(),
                orderType.ifEmpty { null },
            )
                .mapLatest { list ->
                    list.groupBy { it.categoryName }.map { (categoryName, products) ->
                        CategoryWiseReport(
                            categoryName = categoryName,
                            productWithQuantity = products.map {
                                ProductWiseReport(
                                    productId = it.productId,
                                    productName = it.productName,
                                    quantity = it.quantity,
                                )
                            }.toImmutableList(),
                        )
                    }
                        .sortedByDescending { report -> report.productWithQuantity.sumOf { it.quantity } }
                }
        }
    }

    override suspend fun deleteLastSevenDaysBeforeData(): Resource<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getAddressWiseReport(startDate: String): Flow<List<AddressWiseReport>> {
        return withContext(ioDispatcher) {
            val endDate: String = calculateEndOfDayTime(date = startDate.ifEmpty { getStartTime })

            reportsDao.getAddressWiseOrders(startDate.toLong(), endDate.toLong())
        }
    }

    override suspend fun getCustomerWiseReport(startDate: String): Flow<List<CustomerWiseReport>> {
        return withContext(ioDispatcher) {
            val endDate: String = calculateEndOfDayTime(date = startDate.ifEmpty { getStartTime })

            reportsDao.getCustomerWiseOrder(startDate.toLong(), endDate.toLong())
        }
    }

    private suspend fun getItemsReport(startDate: Long, endDate: Long): TotalSales {
        return withContext(ioDispatcher) {
            val expenses = async(ioDispatcher) {
                reportsDao.getTotalExpenses(startDate, endDate)
            }

            val dineInOrder = async(ioDispatcher) {
                reportsDao.getTotalOrders(startDate, endDate, OrderType.DineIn)
            }

            val dineOutOrder = async(ioDispatcher) {
                reportsDao.getTotalOrders(startDate, endDate, OrderType.DineOut)
            }

            TotalSales(
                expenses = expenses.await(),
                dineInOrders = dineInOrder.await(),
                dineOutOrders = dineOutOrder.await(),
            )
        }
    }

    override suspend fun getExpensesReports(startDate: String): Flow<List<ExpensesReport>> {
        return withContext(ioDispatcher) {
            val endDate: String = calculateEndOfDayTime(date = startDate.ifEmpty { getStartTime })

            reportsDao.getExpensesReport(startDate.toLong(), endDate.toLong())
        }
    }
}
