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
import com.niyaj.common.utils.calculateStartOfDayTime
import com.niyaj.core.datastore.KeepDataConfigDataSource
import com.niyaj.data.repository.DataDeletionRepository
import com.niyaj.database.dao.DataDeletionDao
import com.niyaj.database.util.DatabaseHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class DataDeletionRepositoryImpl @Inject constructor(
    private val keepDataConfig: KeepDataConfigDataSource,
    private val deletionDao: DataDeletionDao,
    private val databaseHelper: DatabaseHelper,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : DataDeletionRepository {

    override suspend fun deleteData(): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val deleteData = keepDataConfig.deleteDataBeforeInterval.stateIn(this).value

                if (deleteData) {
                    val config = keepDataConfig.keepDataConfig.stateIn(this).value

                    val reportDate = calculateStartOfDayTime(days = "-${config.reportInterval}")
                    val orderDate = calculateStartOfDayTime(days = "-${config.orderInterval}")
                    val expenseDate = calculateStartOfDayTime(days = "-${config.expenseInterval}")
                    val marketListDate =
                        calculateStartOfDayTime(days = "-${config.marketListInterval}")

                    async { deletionDao.deleteReportData(reportDate) }.await()
                    async { deletionDao.deleteOrdersData(Date(orderDate.toLong())) }.await()
                    async { deletionDao.deleteExpenses(expenseDate) }.await()
                    async { deletionDao.deleteMarketList(marketListDate.toLong()) }.await()
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteAllRecords(): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                databaseHelper.deleteAllTables()
            }

            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}
