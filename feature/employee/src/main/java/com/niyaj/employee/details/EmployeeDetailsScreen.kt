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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.utils.toYearAndMonth
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.employee.components.AbsentDetails
import com.niyaj.employee.components.EmployeeDetails
import com.niyaj.employee.components.PaymentDetails
import com.niyaj.employee.components.SalaryEstimationCard
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

/**
 * Employee Details Screen
 * @author Sk Niyaj Ali
 * @param employeeId
 * @param navigator
 * @param viewModel
 * @see EmployeeDetailsViewModel
 */
@Destination(route = Screens.EMPLOYEE_DETAILS_SCREEN)
@Composable
fun EmployeeDetailsScreen(
    employeeId: Int = 0,
    navigator: DestinationsNavigator,
    onClickAddPayment: (Int) -> Unit,
    onClickAddAbsent: (Int) -> Unit,
    viewModel: EmployeeDetailsViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val salaryEstimationState = viewModel.salaryEstimation.collectAsStateWithLifecycle().value

    val salaryDates = viewModel.salaryDates.collectAsStateWithLifecycle().value

    val selectedSalaryDate = viewModel.selectedSalaryDate.value

    val employeeState = viewModel.employeeDetails.collectAsStateWithLifecycle().value

    val paymentsState = viewModel.payments.collectAsStateWithLifecycle().value

    val absentState = viewModel.employeeAbsentDates.collectAsStateWithLifecycle().value

    var employeeDetailsExpanded by remember {
        mutableStateOf(false)
    }

    var paymentDetailsExpanded by remember {
        mutableStateOf(false)
    }

    var absentReportsExpanded by remember {
        mutableStateOf(false)
    }

    TrackScreenViewEvent(screenName = Screens.EMPLOYEE_DETAILS_SCREEN + "/$employeeId")

    PoposPrimaryScaffold(
        currentRoute = Screens.EMPLOYEE_DETAILS_SCREEN,
        selectionCount = 0,
        snackbarHostState = snackbarHostState,
        title = "Employee Details",
        showBackButton = true,
        gesturesEnabled = false,
        floatingActionButton = {},
        navActions = {
            IconButton(
                onClick = {
                    onClickAddPayment(employeeId)
                },
            ) {
                Icon(imageVector = PoposIcons.Add, contentDescription = "Add Payment Entry")
            }

            IconButton(
                onClick = {
                    onClickAddAbsent(employeeId)
                },
            ) {
                Icon(imageVector = PoposIcons.EventBusy, contentDescription = "Add Absent Entry")
            }
        },
        onBackClick = navigator::navigateUp,
        onNavigateToScreen = navigator::navigate,
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Employee Details::List")

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            state = lazyListState,
        ) {
            item(key = "CalculateSalary") {
                SalaryEstimationCard(
                    uiState = salaryEstimationState,
                    dropdownText = selectedSalaryDate?.first?.toYearAndMonth
                        ?: if (salaryDates.isNotEmpty()) salaryDates.first().startDate.toYearAndMonth else "",
                    salaryDates = salaryDates,
                    onDateClick = {
                        viewModel.onEvent(
                            EmployeeDetailsEvent.OnChooseSalaryDate(it),
                        )
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
                    onClickAbsentEntry = {
                        onClickAddAbsent(employeeId)
                    },
                    onClickSalaryEntry = {
                        onClickAddPayment(employeeId)
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item(key = "EmployeeDetails") {
                EmployeeDetails(
                    employeeState = employeeState,
                    employeeDetailsExpanded = employeeDetailsExpanded,
                    onClickEdit = {
                        navigator.navigate(
                            com.niyaj.employee.destinations.AddEditEmployeeScreenDestination(
                                employeeId,
                            ),
                        )
                    },
                    onExpanded = {
                        employeeDetailsExpanded = !employeeDetailsExpanded
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item(key = "PaymentDetails") {
                PaymentDetails(
                    employeePaymentsState = paymentsState,
                    paymentDetailsExpanded = paymentDetailsExpanded,
                    onExpanded = {
                        paymentDetailsExpanded = !paymentDetailsExpanded
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
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
