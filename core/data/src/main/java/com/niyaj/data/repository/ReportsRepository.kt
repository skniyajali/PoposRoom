package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.CartOrder
import com.niyaj.model.CategoryWiseReport
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.ProductWiseReport
import com.niyaj.model.Reports
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {

    suspend fun generateReport(startDate: String, endDate: String): Resource<Boolean>

    suspend fun getReportByReportDate(reportDate: String): Flow<Reports?>

    suspend fun getReports(startDate: String): Flow<List<Reports>>

    suspend fun getProductWiseReport(
        startDate: String,
        endDate: String,
        orderType: String,
    ): Flow<List<ProductWiseReport>>

    suspend fun getCategoryWiseReport(
        startDate: String,
        endDate: String,
        orderType: String,
    ): Flow<List<CategoryWiseReport>>

    suspend fun getDineOutOrders(startDate: String, endDate: String): Flow<List<CartOrder>>

    suspend fun deleteLastSevenDaysBeforeData(): Resource<Boolean>

    suspend fun getAddressWiseReport(startDate: String, endDate: String): Flow<List<AddressWiseReport>>

    suspend fun getCustomerWiseReport(startDate: String, endDate: String): Flow<List<CustomerWiseReport>>
}