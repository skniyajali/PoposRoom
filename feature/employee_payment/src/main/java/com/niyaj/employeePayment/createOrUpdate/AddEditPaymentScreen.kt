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

package com.niyaj.employeePayment.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.PaymentScreenTags.ADD_EDIT_PAYMENT_ENTRY_BUTTON
import com.niyaj.common.tags.PaymentScreenTags.ADD_EDIT_PAYMENT_SCREEN
import com.niyaj.common.tags.PaymentScreenTags.CREATE_NEW_PAYMENT
import com.niyaj.common.tags.PaymentScreenTags.EDIT_PAYMENT_ITEM
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_AMOUNT_ERROR
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_AMOUNT_FIELD
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_DATE_ERROR
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_DATE_FIELD
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_EMPLOYEE_NAME_ERROR
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_EMPLOYEE_NAME_FIELD
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_MODE_FIELD
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_NOTE_ERROR
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_NOTE_FIELD
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_TYPE_FIELD
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.StandardRoundedFilterChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Employee
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.parameterProvider.EmployeePreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDate

@Destination(route = Screens.ADD_EDIT_PAYMENT_SCREEN)
@Composable
fun AddEditPaymentScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    paymentId: Int = 0,
    employeeId: Int = 0,
    viewModel: AddEditPaymentViewModel = hiltViewModel(),
) {
    TrackScreenViewEvent(screenName = "Add/Edit Payment Screen-$employeeId/$paymentId")

    val employees by viewModel.employees.collectAsStateWithLifecycle()
    val employeeError by viewModel.employeeError.collectAsStateWithLifecycle()
    val amountError by viewModel.amountError.collectAsStateWithLifecycle()
    val dateError by viewModel.dateError.collectAsStateWithLifecycle()
    val typeError by viewModel.paymentTypeError.collectAsStateWithLifecycle()
    val modeError by viewModel.paymentModeError.collectAsStateWithLifecycle()
    val noteError by viewModel.paymentNoteError.collectAsStateWithLifecycle()
    val selectedEmployee by viewModel.selectedEmployee.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    val title = if (paymentId == 0) CREATE_NEW_PAYMENT else EDIT_PAYMENT_ITEM
    val icon = if (paymentId == 0) PoposIcons.Add else PoposIcons.Edit

    AddEditPaymentScreenContent(
        state = viewModel.state,
        employees = employees.toImmutableList(),
        selectedEmployee = selectedEmployee,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
        onClickNewEmployee = {
            navigator.navigate(Screens.ADD_EDIT_EMPLOYEE_SCREEN)
        },
        employeeError = employeeError,
        amountError = amountError,
        dateError = dateError,
        typeError = typeError,
        modeError = modeError,
        noteError = noteError,
        modifier = modifier,
        title = title,
        icon = icon,
    )
}

@VisibleForTesting
@Composable
@Suppress("LongMethod")
internal fun AddEditPaymentScreenContent(
    state: AddEditPaymentState,
    employees: ImmutableList<Employee>,
    selectedEmployee: Employee,
    onEvent: (AddEditPaymentEvent) -> Unit,
    onBackClick: () -> Unit,
    onClickNewEmployee: () -> Unit,
    employeeError: String?,
    amountError: String?,
    dateError: String?,
    typeError: String?,
    modeError: String?,
    noteError: String?,
    modifier: Modifier = Modifier,
    title: String = CREATE_NEW_PAYMENT,
    icon: ImageVector = PoposIcons.Add,
) {
    val dialogState = rememberMaterialDialogState()
    val lazyListState = rememberLazyListState()

    var employeeToggled by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val enableBtn = listOf(
        employeeError,
        amountError,
        dateError,
        modeError,
        typeError,
        noteError,
    ).all { it == null }

    PoposSecondaryScaffold(
        title = title,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = lazyListState.isScrollingUp(),
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON),
                enabled = enableBtn,
                text = title,
                icon = icon,
                onClick = {
                    onEvent(AddEditPaymentEvent.CreateOrUpdatePayment)
                },
            )
        },
    ) { paddingValues ->
        TrackScrollJank(scrollableState = lazyListState, stateName = "Add/Edit Payment::Fields")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(ADD_EDIT_PAYMENT_SCREEN)
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
        ) {
            item(PAYMENT_EMPLOYEE_NAME_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = employees.isNotEmpty() && employeeToggled,
                    onExpandedChange = {
                        employeeToggled = !employeeToggled
                    },
                ) {
                    StandardOutlinedTextField(
                        label = PAYMENT_EMPLOYEE_NAME_FIELD,
                        leadingIcon = PoposIcons.Person4,
                        value = selectedEmployee.employeeName,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                // This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            }
                            .menuAnchor(),
                        isError = employeeError != null,
                        errorText = employeeError,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = employeeToggled,
                            )
                        },
                        readOnly = true,
                        errorTextTag = PAYMENT_EMPLOYEE_NAME_ERROR,
                    )

                    DropdownMenu(
                        expanded = employeeToggled,
                        onDismissRequest = {
                            employeeToggled = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                    ) {
                        employees.forEachIndexed { index, employee ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(employee.employeeName)
                                    .fillMaxWidth(),
                                onClick = {
                                    onEvent(AddEditPaymentEvent.OnSelectEmployee(employee))

                                    employeeToggled = false
                                },
                                text = {
                                    Text(text = employee.employeeName)
                                },
                                leadingIcon = {
                                    CircularBox(
                                        icon = PoposIcons.Person,
                                        selected = false,
                                        text = employee.employeeName,
                                        showBorder = false,
                                        size = 30.dp,
                                    )
                                },
                            )

                            if (index != employees.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 44.dp),
                                )
                            }
                        }

                        if (employees.isEmpty()) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                enabled = false,
                                onClick = {},
                                text = {
                                    Text(
                                        text = "Employees not available",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.CenterHorizontally),
                                    )
                                },
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = onClickNewEmployee,
                            text = {
                                Text(
                                    text = "Create a new employee",
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = PoposIcons.Add,
                                    contentDescription = "Create",
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = PoposIcons.ArrowRightAlt,
                                    contentDescription = "trailing",
                                )
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(GIVEN_DATE_FIELD) {
                StandardOutlinedTextField(
                    label = GIVEN_DATE_FIELD,
                    leadingIcon = PoposIcons.CalenderToday,
                    value = state.paymentDate.toPrettyDate(),
                    onValueChange = {},
                    isError = dateError != null,
                    errorText = dateError,
                    trailingIcon = {
                        FilledTonalIconButton(
                            onClick = { dialogState.show() },
                        ) {
                            Icon(
                                imageVector = PoposIcons.CalenderMonth,
                                contentDescription = "Choose a date",
                            )
                        }
                    },
                    readOnly = true,
                    errorTextTag = GIVEN_DATE_ERROR,
                    suffix = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "Click Here")
                            Spacer(modifier = Modifier.width(SpaceMini))
                            Icon(imageVector = PoposIcons.ArrowForward, "Click Here")
                        }
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(GIVEN_AMOUNT_FIELD) {
                StandardOutlinedTextField(
                    label = GIVEN_AMOUNT_FIELD,
                    leadingIcon = PoposIcons.Money,
                    value = state.paymentAmount,
                    onValueChange = {
                        onEvent(AddEditPaymentEvent.PaymentAmountChanged(it))
                    },
                    isError = amountError != null,
                    errorText = amountError,
                    keyboardType = KeyboardType.Number,
                    errorTextTag = GIVEN_AMOUNT_ERROR,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(PAYMENT_NOTE_FIELD) {
                StandardOutlinedTextField(
                    label = PAYMENT_NOTE_FIELD,
                    leadingIcon = PoposIcons.Description,
                    value = state.paymentNote,
                    onValueChange = {
                        onEvent(AddEditPaymentEvent.PaymentNoteChanged(it))
                    },
                    isError = noteError != null,
                    errorText = noteError,
                    errorTextTag = PAYMENT_NOTE_ERROR,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(PAYMENT_TYPE_FIELD) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpaceSmall),
                ) {
                    IconWithText(
                        text = PAYMENT_TYPE_FIELD,
                        icon = PoposIcons.MergeType,
                    )

                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row {
                        PaymentType.entries.forEach { type ->
                            StandardRoundedFilterChip(
                                text = type.name,
                                modifier = Modifier.testTag(PAYMENT_TYPE_FIELD.plus(type.name)),
                                selected = state.paymentType == type,
                                selectedColor = MaterialTheme.colorScheme.tertiary,
                                onClick = {
                                    onEvent(AddEditPaymentEvent.PaymentTypeChanged(type))
                                },
                            )

                            Spacer(modifier = Modifier.width(SpaceMini))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(PAYMENT_MODE_FIELD) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpaceSmall),
                ) {
                    IconWithText(
                        text = PAYMENT_MODE_FIELD,
                        icon = PoposIcons.MergeType,
                    )

                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row {
                        PaymentMode.entries.forEach { type ->
                            StandardRoundedFilterChip(
                                text = type.name,
                                modifier = Modifier.testTag(PAYMENT_MODE_FIELD.plus(type.name)),
                                selected = state.paymentMode == type,
                                selectedColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    onEvent(AddEditPaymentEvent.PaymentModeChanged(type))
                                },
                            )

                            Spacer(modifier = Modifier.width(SpaceMini))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        },
    ) {
        datepicker(
            allowedDateValidator = { date ->
                if (selectedEmployee.employeeId != 0) {
                    (date.toMilliSecond >= selectedEmployee.employeeJoinedDate) && (date <= LocalDate.now())
                } else {
                    date == LocalDate.now()
                }
            },
        ) { date ->
            onEvent(AddEditPaymentEvent.PaymentDateChanged(date.toMilliSecond))
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditPaymentScreenContentPreview(
    modifier: Modifier = Modifier,
    employees: ImmutableList<Employee> = EmployeePreviewData.employeeList.toImmutableList(),
) {
    PoposRoomTheme {
        AddEditPaymentScreenContent(
            state = AddEditPaymentState(
                paymentAmount = "200",
                paymentNote = "Advance Payment",
                paymentType = PaymentType.Advanced,
                paymentMode = PaymentMode.Cash,
            ),
            employees = employees,
            selectedEmployee = employees.first(),
            onEvent = {},
            onBackClick = {},
            onClickNewEmployee = {},
            employeeError = null,
            amountError = null,
            dateError = null,
            typeError = null,
            modeError = null,
            noteError = null,
            modifier = modifier,
        )
    }
}
