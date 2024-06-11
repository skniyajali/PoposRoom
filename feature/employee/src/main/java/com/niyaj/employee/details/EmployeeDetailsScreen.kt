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

package com.niyaj.employee.details

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_DETAILS
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.employee.components.AbsentDetails
import com.niyaj.employee.components.EmployeeDetails
import com.niyaj.employee.components.PaymentDetails
import com.niyaj.employee.components.SalaryEstimationCard
import com.niyaj.employee.destinations.AddEditEmployeeScreenDestination
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.ui.components.PoposScaffold
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.EmployeePreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.OpenResultRecipient
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Employee Details Screen
 * @author Sk Niyaj Ali
 * @param employeeId
 * @param navigator
 * @param viewModel
 * @see EmployeeDetailsViewModel
 */
@Suppress("DEPRECATION")
@Destination(route = Screens.EMPLOYEE_DETAILS_SCREEN)
@Composable
fun EmployeeDetailsScreen(
    employeeId: Int = 0,
    navigator: DestinationsNavigator,
    onClickAddPayment: (Int) -> Unit,
    onClickAddAbsent: (Int) -> Unit,
    viewModel: EmployeeDetailsViewModel = hiltViewModel(),
    paymentRecipient: OpenResultRecipient<String>,
    absentRecipient: OpenResultRecipient<String>,
) {
    val salaryEstimationState by viewModel.salaryEstimation.collectAsStateWithLifecycle()
    val salaryDates by viewModel.salaryDates.collectAsStateWithLifecycle()
    val employeeState by viewModel.employeeDetails.collectAsStateWithLifecycle()
    val paymentsState by viewModel.payments.collectAsStateWithLifecycle()
    val absentState by viewModel.employeeAbsentDates.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val refreshState = rememberSwipeRefreshState(isLoading)

    // TODO:: Workaround to update data atomically
    paymentRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}

            is NavResult.Value -> {
                viewModel.updateLoading()
            }
        }
    }

    // TODO:: Workaround to update data atomically
    absentRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}

            is NavResult.Value -> {
                viewModel.updateLoading()
            }
        }
    }

    val selectedSalaryDate = viewModel.selectedSalaryDate.value

    TrackScreenViewEvent(screenName = Screens.EMPLOYEE_DETAILS_SCREEN + "/$employeeId")

    // TODO:: Workaround to update data atomically
    SwipeRefresh(
        state = refreshState,
        onRefresh = viewModel::updateLoading,
    ) {
        EmployeeDetailsScreenContent(
            modifier = Modifier,
            employeeState = employeeState,
            salaryEstimationState = salaryEstimationState,
            paymentsState = paymentsState,
            absentState = absentState,
            salaryDates = salaryDates.toImmutableList(),
            selectedSalaryDate = selectedSalaryDate,
            onEvent = viewModel::onEvent,
            onBackClick = navigator::navigateUp,
            onClickAddPayment = {
                navigator.clearBackStack(Screens.EMPLOYEE_DETAILS_SCREEN)
                onClickAddPayment(employeeId)
            },
            onClickAddAbsent = {
                onClickAddAbsent(employeeId)
            },
            onClickEdit = {
                navigator.navigate(AddEditEmployeeScreenDestination(employeeId))
            },
        )
    }
}

@VisibleForTesting
@Composable
internal fun EmployeeDetailsScreenContent(
    modifier: Modifier = Modifier,
    employeeState: UiState<Employee>,
    salaryEstimationState: UiState<EmployeeSalaryEstimation>,
    paymentsState: UiState<List<EmployeePayments>>,
    absentState: UiState<List<EmployeeAbsentDates>>,
    salaryDates: ImmutableList<EmployeeMonthlyDate>,
    selectedSalaryDate: Pair<String, String>? = null,
    onEvent: (EmployeeDetailsEvent) -> Unit,
    onBackClick: () -> Unit,
    onClickAddPayment: () -> Unit,
    onClickAddAbsent: () -> Unit,
    onClickEdit: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    var employeeDetailsExpanded by rememberSaveable { mutableStateOf(true) }
    var paymentDetailsExpanded by rememberSaveable { mutableStateOf(false) }
    var absentReportsExpanded by rememberSaveable { mutableStateOf(false) }

    TrackScrollJank(scrollableState = lazyListState, stateName = "Employee Details::List")

    PoposScaffold(
        modifier = modifier,
        title = EMPLOYEE_DETAILS,
        showBackButton = true,
        onBackClick = onBackClick,
        navActions = {
            IconButton(
                onClick = onClickAddPayment,
            ) {
                Icon(
                    imageVector = PoposIcons.Add,
                    contentDescription = "Add Payment Entry",
                )
            }

            IconButton(
                onClick = onClickAddAbsent,
            ) {
                Icon(
                    imageVector = PoposIcons.EventBusy,
                    contentDescription = "Add Absent Entry",
                )
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            state = lazyListState,
        ) {
            item(key = "CalculateSalary") {
                SalaryEstimationCard(
                    uiState = salaryEstimationState,
                    selectedSalaryDate = selectedSalaryDate,
                    salaryDates = salaryDates,
                    onDateClick = {
                        onEvent(EmployeeDetailsEvent.OnChooseSalaryDate(it))
                    },
                    onClickPaymentCount = {
                        scope.launch {
                            lazyListState.animateScrollToItem(2)
                        }
                    },
                    onClickAbsentCount = {
                        scope.launch {
                            lazyListState.animateScrollToItem(3)
                        }
                    },
                    onClickAbsentEntry = onClickAddAbsent,
                    onClickSalaryEntry = onClickAddPayment,
                )
            }

            item(key = "EmployeeDetails") {
                EmployeeDetails(
                    employeeState = employeeState,
                    employeeDetailsExpanded = employeeDetailsExpanded,
                    onClickEdit = onClickEdit,
                    onExpanded = {
                        employeeDetailsExpanded = !employeeDetailsExpanded
                    },
                )
            }

            item(key = "PaymentDetails") {
                PaymentDetails(
                    employeePaymentsState = paymentsState,
                    paymentDetailsExpanded = paymentDetailsExpanded,
                    onExpanded = {
                        paymentDetailsExpanded = !paymentDetailsExpanded
                    },
                )
            }

            item(key = "AbsentDetails") {
                AbsentDetails(
                    absentState = absentState,
                    absentReportsExpanded = absentReportsExpanded,
                    onExpanded = {
                        absentReportsExpanded = !absentReportsExpanded
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }
        }
    }
}

@DevicePreviews
@Composable
private fun EmployeeDetailsScreenContentLoadingPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        EmployeeDetailsScreenContent(
            modifier = modifier,
            employeeState = UiState.Loading,
            salaryEstimationState = UiState.Loading,
            paymentsState = UiState.Loading,
            absentState = UiState.Loading,
            salaryDates = persistentListOf(),
            selectedSalaryDate = null,
            onEvent = {},
            onBackClick = {},
            onClickAddPayment = {},
            onClickAddAbsent = {},
            onClickEdit = {},
        )
    }
}

@DevicePreviews
@Composable
private fun EmployeeDetailsScreenContentEmptyPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        EmployeeDetailsScreenContent(
            modifier = modifier,
            employeeState = UiState.Empty,
            salaryEstimationState = UiState.Empty,
            paymentsState = UiState.Empty,
            absentState = UiState.Empty,
            salaryDates = persistentListOf(),
            selectedSalaryDate = null,
            onEvent = {},
            onBackClick = {},
            onClickAddPayment = {},
            onClickAddAbsent = {},
            onClickEdit = {},
        )
    }
}

@DevicePreviews
@Composable
private fun EmployeeDetailsScreenContentPreview(
    modifier: Modifier = Modifier,
    employee: Employee = EmployeePreviewData.employeeList.first(),
    salaryEstimation: EmployeeSalaryEstimation = EmployeePreviewData.employeeSalaryEstimations.last(),
    payments: List<EmployeePayments> = EmployeePreviewData.employeePayments,
    absent: List<EmployeeAbsentDates> = EmployeePreviewData.employeeAbsentDates,
    salaryDates: List<EmployeeMonthlyDate> = EmployeePreviewData.employeeMonthlyDates,
) {
    PoposRoomTheme {
        EmployeeDetailsScreenContent(
            modifier = modifier,
            employeeState = UiState.Success(employee),
            salaryEstimationState = UiState.Success(salaryEstimation),
            paymentsState = UiState.Success(payments),
            absentState = UiState.Success(absent),
            salaryDates = salaryDates.toImmutableList(),
            selectedSalaryDate = null,
            onEvent = {},
            onBackClick = {},
            onClickAddPayment = {},
            onClickAddAbsent = {},
            onClickEdit = {},
        )
    }
}