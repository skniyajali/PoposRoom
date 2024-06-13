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

package com.niyaj.employeePayment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.ui.parameterProvider.PaymentPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

enum class ViewType {
    LIST,
    CARD,
}

@Composable
internal fun EmployeePaymentList(
    modifier: Modifier = Modifier,
    viewType: ViewType,
    items: ImmutableList<EmployeeWithPayments>,
    doesSelected: (Int) -> Boolean,
    onSelectItem: (Int) -> Unit,
    isInSelectionMode: Boolean = true,
    showTrailingIcon: Boolean = false,
    showEmployeeDetails: Boolean = false,
    onClickAddPayment: (employeeId: Int) -> Unit = {},
    onClickEmployee: (employeeId: Int) -> Unit = {},
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScrollJank(scrollableState = lazyListState, stateName = "Payment::List")

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        state = lazyListState,
    ) {
        items(
            items = items,
            key = { item ->
                item.employee.employeeId
            },
        ) { empWithPayments ->
            if (empWithPayments.payments.isNotEmpty()) {
                PaymentsList(
                    viewType = viewType,
                    employee = empWithPayments.employee,
                    payments = empWithPayments.payments.toImmutableList(),
                    showTrailingIcon = showTrailingIcon,
                    showEmployeeDetails = showEmployeeDetails,
                    doesSelected = doesSelected,
                    onClick = {
                        if (isInSelectionMode) {
                            onSelectItem(it)
                        }
                    },
                    onLongClick = onSelectItem,
                    onClickAddPayment = onClickAddPayment,
                    onClickEmployee = onClickEmployee,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun EmployeePaymentListViewPreview(
    modifier: Modifier = Modifier,
    items: List<EmployeeWithPayments> = PaymentPreviewData.employeesWithPayments,
) {
    PoposRoomTheme {
        EmployeePaymentList(
            modifier = modifier,
            viewType = ViewType.LIST,
            items = items.toImmutableList(),
            doesSelected = { false },
            onSelectItem = {},
            isInSelectionMode = false,
            showTrailingIcon = false,
            showEmployeeDetails = false,
            onClickAddPayment = {},
            onClickEmployee = {},
        )
    }
}

@DevicePreviews
@Composable
private fun EmployeePaymentListCardViewPreview(
    modifier: Modifier = Modifier,
    items: List<EmployeeWithPayments> = PaymentPreviewData.employeesWithPayments,
) {
    PoposRoomTheme {
        EmployeePaymentList(
            modifier = modifier,
            viewType = ViewType.CARD,
            items = items.toImmutableList(),
            doesSelected = { false },
            onSelectItem = {},
            isInSelectionMode = false,
            showTrailingIcon = false,
            showEmployeeDetails = false,
            onClickAddPayment = {},
            onClickEmployee = {},
        )
    }
}
