package com.niyaj.employee.details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.flowlayout.FlowRow
import com.niyaj.common.utils.Constants.PAID
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toDate
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toJoinedDate
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toYearAndMonth
import com.niyaj.data.utils.EmployeeTestTags.REMAINING_AMOUNT_TEXT
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.employee.components.SalaryDateDropdown
import com.niyaj.employee.destinations.AddEditEmployeeScreenDestination
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.ui.components.IconBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PaymentStatusChip
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.StandardOutlinedChip
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.event.UiState
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
                            lazyListState.animateScrollToItem(3)
                        }
                    },
                    onClickAbsentCount = {
                        scope.launch {
                            lazyListState.animateScrollToItem(4)
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


/**
 *
 */
@Composable
fun SalaryEstimationCard(
    uiState: UiState<EmployeeSalaryEstimation>,
    dropdownText: String = "",
    salaryDates: List<EmployeeMonthlyDate> = emptyList(),
    onDateClick: (Pair<String, String>) -> Unit = {},
    onClickPaymentCount: () -> Unit = {},
    onClickAbsentCount: () -> Unit = {},
    onClickAbsentEntry: () -> Unit = {},
    onClickSalaryEntry: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Salary Estimation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            SalaryDateDropdown(
                text = dropdownText,
                salaryDates = salaryDates,
                onDateClick = {
                    onDateClick(it)
                }
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))
        Spacer(modifier = Modifier.height(SpaceSmall))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Crossfade(
            targetState = uiState,
            label = "SalaryEstimationState"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    Text(
                        text = "Something went wrong!",
                        textAlign = TextAlign.Center,
                    )
                }

                is UiState.Success -> {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = state.data.remainingAmount.toRupee,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag(REMAINING_AMOUNT_TEXT)
                            )

                            Column(
                                horizontalAlignment = Alignment.End,
                            ) {
                                PaymentStatusChip(isPaid = state.data.status == PAID)

                                state.data.message?.let {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    Text(
                                        text = it,
                                        color = if (state.data.status == PAID)
                                            MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = TextAlign.End
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Divider(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SuggestionChip(
                                modifier = Modifier.testTag("AdvancePayment"),
                                onClick = onClickPaymentCount,
                                label = {
                                    Text(text = "${state.data.paymentCount} Advance Payment")
                                },
                            )

                            Spacer(modifier = Modifier.width(SpaceSmall))

                            SuggestionChip(
                                modifier = Modifier.testTag("DaysAbsent"),
                                onClick = onClickAbsentCount,
                                label = {
                                    Text(text = "${state.data.absentCount} Days Absent")
                                },
                            )
                        }

                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Divider(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        StandardButton(
                            text = "Add Absent Entry",
                            icon = Icons.Default.EventBusy,
                            onClick = onClickAbsentEntry,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        )

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        StandardButton(
                            text = "Add Payment Entry",
                            icon = Icons.Default.Money,
                            onClick = onClickSalaryEntry,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(SpaceSmall))
    }
}


/**
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetails(
    employeeState: UiState<Employee>,
    employeeDetailsExpanded: Boolean = false,
    onClickEdit: () -> Unit = {},
    onExpanded: () -> Unit = {},
) {
    Card(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
        )
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = employeeDetailsExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                IconWithText(
                    text = "Employee Details",
                    icon = Icons.Default.Person
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Employee",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = employeeState,
                    label = "EmployeeDetailsState"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Employee details not found",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall)
                            ) {
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeeName),
                                    text = "Name - ${state.data.employeeName}",
                                    icon = Icons.Default.Person
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeePhone),
                                    text = "Phone - ${state.data.employeePhone}",
                                    icon = Icons.Default.PhoneAndroid
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeeSalary.toRupee),
                                    text = "Salary - ${state.data.employeeSalary.toRupee}",
                                    icon = Icons.Default.CurrencyRupee
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeeSalaryType.name),
                                    text = "Salary Type - ${state.data.employeeSalaryType}",
                                    icon = Icons.Default.Merge
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeePosition),
                                    text = "Position - ${state.data.employeePosition}",
                                    icon = Icons.Default.Approval
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeeType.name),
                                    text = "Type - ${state.data.employeeType}",
                                    icon = Icons.Default.MergeType
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    modifier = Modifier.testTag(state.data.employeeJoinedDate.toDate),
                                    text = "Joined Date : ${state.data.employeeJoinedDate.toJoinedDate}",
                                    icon = Icons.Default.CalendarToday
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                TextWithIcon(
                                    text = "Created At : ${state.data.createdAt.toPrettyDate()}",
                                    icon = Icons.Default.AccessTime
                                )
                                state.data.updatedAt?.let {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    TextWithIcon(
                                        text = "Updated At : ${it.toPrettyDate()}",
                                        icon = Icons.Default.Login
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}

/**
 *
 */
@Composable
fun PaymentDetails(
    employeePaymentsState: UiState<List<EmployeePayments>>,
    paymentDetailsExpanded: Boolean = false,
    onExpanded: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onExpanded()
            }
            .testTag("PaymentDetails"),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
        )
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = paymentDetailsExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                IconWithText(
                    text = "Payment Details",
                    icon = Icons.Default.Money
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpanded()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = employeePaymentsState,
                    label = "PaymentDetails"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        UiState.Empty -> {
                            ItemNotAvailable(
                                text = "You have not paid any amount to this employee.",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                state.data.forEachIndexed { index, salaries ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(SpaceSmall),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Column {
                                            Text(
                                                text = "${salaries.startDate.toFormattedDate} - ${salaries.endDate.toFormattedDate}",
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            Text(
                                                text = salaries.payments.sumOf { it.paymentAmount.toLong() }
                                                    .toString().toRupee,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        Divider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        salaries.payments.forEachIndexed { index, salary ->
                                            EmployeePayment(payment = salary)

                                            if (index != salaries.payments.size - 1) {
                                                Spacer(modifier = Modifier.height(SpaceSmall))
                                                Divider(modifier = Modifier.fillMaxWidth())
                                                Spacer(modifier = Modifier.height(SpaceSmall))
                                            }
                                        }
                                    }

                                    if (index != state.data.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        Divider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}


@Composable
fun EmployeePayment(
    payment: Payment,
) {
    Row(
        modifier = Modifier
            .testTag("Payment Tag")
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = payment.paymentAmount.toRupee,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.8F)
        )

        Text(
            text = payment.paymentDate.toBarDate,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(0.8F),
        )

        Row(
            modifier = Modifier.weight(1.4F),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            IconBox(
                text = payment.paymentMode.name,
                icon = when (payment.paymentMode) {
                    PaymentMode.Cash -> Icons.Default.Money
                    PaymentMode.Online -> Icons.Default.AccountBalance
                    else -> Icons.Default.Payments
                },
                selected = false,
            )

            Spacer(modifier = Modifier.width(SpaceSmall))

            StandardOutlinedChip(text = payment.paymentType.name)
        }
    }
}

/**
 *
 */
@Composable
fun AbsentDetails(
    absentState: UiState<List<EmployeeAbsentDates>>,
    absentReportsExpanded: Boolean = false,
    onExpanded: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onExpanded()
            }
            .testTag("AbsentDetails"),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
        )
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = absentReportsExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                IconWithText(
                    text = "Absent Details",
                    icon = Icons.Default.EventBusy
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand Absent Details",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = absentState,
                    label = "AbsentDetails"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Employee absent reports not available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                state.data.forEachIndexed { index, absentReport ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(SpaceSmall),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Column {
                                            Text(
                                                text = "${absentReport.startDate.toFormattedDate} - ${absentReport.endDate.toFormattedDate}",
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            Text(
                                                text = "${absentReport.absentDates.size} Days Absent",
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        Divider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(SpaceSmall),
                                            crossAxisSpacing = SpaceMini,
                                        ) {
                                            absentReport.absentDates.forEach { date ->
                                                Card(
                                                    modifier = Modifier
                                                        .testTag(date.plus(absentReport.startDate)),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = LightColor6
                                                    )
                                                ) {
                                                    Text(
                                                        text = date.toFormattedDate,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        textAlign = TextAlign.Start,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier
                                                            .padding(SpaceSmall)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(SpaceSmall))
                                            }
                                        }
                                    }

                                    if (index != state.data.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        Divider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}