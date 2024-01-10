package com.niyaj.employee.details

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventBusy
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.niyaj.common.utils.toYearAndMonth
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.employee.components.AbsentDetails
import com.niyaj.employee.components.EmployeeDetails
import com.niyaj.employee.components.PaymentDetails
import com.niyaj.employee.components.SalaryEstimationCard
import com.niyaj.employee.destinations.AddEditEmployeeScreenDestination
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

/**
 * Employee Details Screen
 * @author Sk Niyaj Ali
 * @param employeeId
 * @param navController
 * @param viewModel
 * @see EmployeeDetailsViewModel
 */
@Destination
@Composable
fun EmployeeDetailsScreen(
    employeeId: Int = 0,
    navController: NavController,
    onClickAddPayment: (Int) -> Unit,
    onClickAddAbsent: (Int) -> Unit,
    viewModel: EmployeeDetailsViewModel = hiltViewModel(),
) {
    val empId = navController.currentBackStackEntryAsState().value?.arguments?.getInt("employeeId")
        ?: employeeId

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
    
    TrackScreenViewEvent(screenName = "Employee Details Screen")

    StandardScaffold(
        navController = navController,
        snackbarHostState = snackbarHostState,
        title = "Employee Details",
        selectionCount = 0,
        showBackButton = true,
        floatingActionButton = {},
        navActions = {
            IconButton(
                onClick = {
                    onClickAddPayment(empId)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Payment Entry")
            }

            IconButton(
                onClick = {
                    onClickAddAbsent(empId)
                }
            ) {
                Icon(imageVector = Icons.Default.EventBusy, contentDescription = "Add Absent Entry")
            }
        },
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Employee Details::List")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
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
                            EmployeeDetailsEvent.OnChooseSalaryDate(it)
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
                        onClickAddAbsent(empId)
                    },
                    onClickSalaryEntry = {
                        onClickAddPayment(empId)
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            item(key = "EmployeeDetails") {
                EmployeeDetails(
                    employeeState = employeeState,
                    employeeDetailsExpanded = employeeDetailsExpanded,
                    onClickEdit = {
                        navController.navigate(AddEditEmployeeScreenDestination(empId))
                    },
                    onExpanded = {
                        employeeDetailsExpanded = !employeeDetailsExpanded
                    }
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