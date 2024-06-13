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
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.database.dao.PrinterDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Printer
import com.niyaj.model.Profile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class PrinterRepositoryImpl(
    private val printerDao: PrinterDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : PrinterRepository {

    override fun getPrinter(printerId: String): Flow<Printer> {
        return printerDao.printerInfo(printerId).mapLatest {
            it?.toExternalModel() ?: Printer.defaultPrinterInfo
        }
    }

    override suspend fun getPrinterInfo(printerId: String): Flow<Printer> {
        return flow { }
    }

    override suspend fun addOrUpdatePrinterInfo(printer: Printer): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val result = printerDao.insertOrUpdatePrinterInfo(printer.toEntity())

                Resource.Success(result > 0)
            } catch (e: Exception) {
                Resource.Error(e.message)
            }
        }
    }

    override fun getProfileInfo(restaurantId: Int): Flow<Profile> {
        return printerDao.getProfileInfo(restaurantId).mapLatest {
            it?.asExternalModel() ?: Profile.defaultProfileInfo
        }
    }
}
