/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.CategoryWiseReport
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.ExpensesReport
import com.niyaj.model.ProductWiseReport
import com.niyaj.model.Reports
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {

    suspend fun generateReport(reportDate: String): Resource<Boolean>

    suspend fun getReportByReportDate(reportDate: String): Flow<Reports>

    suspend fun getReports(): Flow<List<Reports>>

    suspend fun getProductWiseReport(startDate: String, orderType: String): Flow<List<ProductWiseReport>>

    suspend fun getCategoryWiseReport(startDate: String, orderType: String): Flow<List<CategoryWiseReport>>

    suspend fun deleteLastSevenDaysBeforeData(): Resource<Boolean>

    suspend fun getAddressWiseReport(startDate: String): Flow<List<AddressWiseReport>>

    suspend fun getCustomerWiseReport(startDate: String): Flow<List<CustomerWiseReport>>

    suspend fun getExpensesReports(startDate: String): Flow<List<ExpensesReport>>
}