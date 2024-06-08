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

package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.common.utils.Constants.PRINTER_ID
import com.niyaj.model.Printer
import com.niyaj.model.Profile
import kotlinx.coroutines.flow.Flow

interface PrinterRepository {

    fun getPrinter(printerId: String = PRINTER_ID): Flow<Printer>

    suspend fun getPrinterInfo(printerId: String = PRINTER_ID): Flow<Printer>

    suspend fun addOrUpdatePrinterInfo(printer: Printer): Resource<Boolean>

    fun getProfileInfo(restaurantId: Int): Flow<Profile>
}
