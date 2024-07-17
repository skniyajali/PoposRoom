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

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.common.utils.getStartTime
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.CategoryWiseReport
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.ExpensesReport
import com.niyaj.model.ProductWiseReport
import com.niyaj.model.Reports
import com.niyaj.ui.event.UiState
import kotlinx.collections.immutable.toImmutableList

class ReportsPreviewParameter : PreviewParameterProvider<UiState<Reports>> {
    override val values: Sequence<UiState<Reports>> = sequenceOf(
        UiState.Loading,
        UiState.Empty,
        UiState.Success(ReportsPreviewData.reports),
    )
}

class CategoryWiseReportPreviewProvider :
    PreviewParameterProvider<UiState<List<CategoryWiseReport>>> {
    override val values: Sequence<UiState<List<CategoryWiseReport>>> = sequenceOf(
        UiState.Loading,
        UiState.Empty,
        UiState.Success(ReportsPreviewData.categoryWiseReport),
    )
}

class CustomerWiseReportPreviewParameter :
    PreviewParameterProvider<UiState<List<CustomerWiseReport>>> {
    override val values: Sequence<UiState<List<CustomerWiseReport>>> = sequenceOf(
        UiState.Loading,
        UiState.Empty,
        UiState.Success(ReportsPreviewData.customerWiseReport),
    )
}

class ExpensesReportPreviewParameter : PreviewParameterProvider<UiState<List<ExpensesReport>>> {
    override val values: Sequence<UiState<List<ExpensesReport>>> = sequenceOf(
        UiState.Loading,
        UiState.Empty,
        UiState.Success(ReportsPreviewData.expensesReport),
    )
}

class AddressWiseReportPreviewParameter :
    PreviewParameterProvider<UiState<List<AddressWiseReport>>> {
    override val values: Sequence<UiState<List<AddressWiseReport>>> = sequenceOf(
        UiState.Loading,
        UiState.Empty,
        UiState.Success(ReportsPreviewData.addressWiseReport),
    )
}

object ReportsPreviewData {

    val reports = Reports(
        reportId = 2,
        dineInSalesAmount = 1200,
        dineOutSalesAmount = 2400,
        expensesAmount = 8000,
        expensesQty = 2,
        dineInSalesQty = 8,
        dineOutSalesQty = 10,
        reportDate = getStartTime,
        createdAt = System.currentTimeMillis().toString(),
        updatedAt = System.currentTimeMillis().toString(),
    )

    val addressWiseReport: List<AddressWiseReport> = List(5) {
        AddressWiseReport(
            addressId = it,
            addressName = "Address $it",
            shortName = "A$it",
            totalOrders = (1..5).random(),
            totalSales = it.times((400..500).random()),
        )
    }

    val categoryWiseReport: List<CategoryWiseReport> = List(5) {
        CategoryWiseReport(
            categoryName = "Category $it",
            productWithQuantity = List(5) { index ->
                ProductWiseReport(
                    productId = index,
                    productName = "Product $index",
                    quantity = index.times((1..5).random()),
                )
            }.toImmutableList(),
        )
    }

    val customerWiseReport: List<CustomerWiseReport> = List(5) {
        CustomerWiseReport(
            customerId = it,
            customerName = "Customer $it",
            customerPhone = "9078452121".plus(it),
            totalOrders = (1..5).random(),
            totalSales = it.times((400..500).random()),
        )
    }

    val expensesReport: List<ExpensesReport> = List(5) {
        ExpensesReport(
            expenseId = it,
            expenseName = "Expense $it",
            expenseAmount = it.times((400..500).random()).toString(),
        )
    }
}
