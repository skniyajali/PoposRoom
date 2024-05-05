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