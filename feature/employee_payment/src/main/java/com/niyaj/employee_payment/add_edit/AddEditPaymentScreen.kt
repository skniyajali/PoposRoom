package com.niyaj.employee_payment.add_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
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
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Destination(
    route = Screens.ADD_EDIT_PAYMENT_SCREEN
)
@Composable
fun AddEditPaymentScreen(
    paymentId: Int = 0,
    employeeId: Int = 0,
    navController: NavController,
    viewModel: AddEditPaymentViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val lazyListState = rememberLazyListState()

    val employees = viewModel.employees.collectAsStateWithLifecycle().value

    val employeeError = viewModel.employeeError.collectAsStateWithLifecycle().value
    val amountError = viewModel.amountError.collectAsStateWithLifecycle().value
    val dateError = viewModel.dateError.collectAsStateWithLifecycle().value
    val typeError = viewModel.paymentTypeError.collectAsStateWithLifecycle().value
    val modeError = viewModel.paymentModeError.collectAsStateWithLifecycle().value
    val noteError = viewModel.paymentNoteError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(
        employeeError,
        amountError,
        dateError,
        modeError,
        typeError,
        noteError,
    ).all { it == null }

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when(data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    var employeeToggled by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val selectedEmployee = viewModel.selectedEmployee.collectAsStateWithLifecycle().value

    val dialogState = rememberMaterialDialogState()

    val title = if (paymentId == 0) CREATE_NEW_PAYMENT else EDIT_PAYMENT_ITEM

    StandardScaffoldNew(
        navController = navController,
        title = title,
        onBackClick = {
            navController.navigateUp()
        },
        showBackButton = true,
        showBottomBar = enableBtn,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_PAYMENT_ENTRY_BUTTON)
                    .padding(horizontal = SpaceSmallMax),
                enabled = enableBtn,
                text = title,
                icon = if (paymentId == 0) Icons.Default.Add else Icons.Default.Edit,
                onClick = {
                    viewModel.onEvent(AddEditPaymentEvent.CreateOrUpdatePayment(paymentId))
                }
            )
        }
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(ADD_EDIT_PAYMENT_SCREEN)
                .fillMaxWidth()
                .padding(SpaceSmall),
        ) {
            item(PAYMENT_EMPLOYEE_NAME_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = employees.isNotEmpty() && employeeToggled,
                    onExpandedChange = {
                        employeeToggled = !employeeToggled
                    },
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            }
                            .menuAnchor(),
                        value = selectedEmployee.employeeName,
                        label = PAYMENT_EMPLOYEE_NAME_FIELD,
                        leadingIcon = Icons.Default.Person4,
                        isError = employeeError != null,
                        errorText = employeeError,
                        readOnly = true,
                        errorTextTag = PAYMENT_EMPLOYEE_NAME_ERROR,
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = employeeToggled
                            )
                        },
                    )

                    DropdownMenu(
                        expanded = employeeToggled,
                        onDismissRequest = {
                            employeeToggled = false
                        },
                        modifier = Modifier
                            .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                    ) {
                        employees.forEachIndexed{ index, employee ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(employee.employeeName)
                                    .fillMaxWidth(),
                                onClick = {
                                    viewModel.onEvent(AddEditPaymentEvent.OnSelectEmployee(employee))

                                    employeeToggled = false
                                },
                                text = {
                                    Text(text = employee.employeeName)
                                },
                                leadingIcon = {
                                    CircularBox(
                                        icon = Icons.Default.Person,
                                        doesSelected = false,
                                        size = 30.dp,
                                        showBorder = false,
                                        text = employee.employeeName
                                    )
                                }
                            )

                            if(index != employees.size - 1) {
                                HorizontalDivider(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 44.dp))
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
                                            .align(Alignment.CenterHorizontally)
                                    )
                                },
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                navController.navigate(Screens.ADD_EDIT_EMPLOYEE_SCREEN)
                            },
                            text = {
                                Text(
                                    text = "Create a new employee",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Create",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                    contentDescription = "trailing"
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(GIVEN_DATE_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.paymentDate.toPrettyDate(),
                    label = GIVEN_DATE_FIELD,
                    leadingIcon = Icons.Default.CalendarToday,
                    trailingIcon = {
                        FilledTonalIconButton(
                            onClick = { dialogState.show() }
                        ) {
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Choose a date")
                        }
                    },
                    isError = dateError != null,
                    errorText = dateError,
                    readOnly = true,
                    errorTextTag = GIVEN_DATE_ERROR,
                    onValueChange = {},
                    suffix = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "Click Here")
                            Spacer(modifier = Modifier.width(SpaceMini))
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, "Click Here")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(GIVEN_AMOUNT_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.paymentAmount,
                    label = GIVEN_AMOUNT_FIELD,
                    leadingIcon = Icons.Default.Money,
                    keyboardType = KeyboardType.Number,
                    isError = amountError != null,
                    errorText = amountError,
                    errorTextTag = GIVEN_AMOUNT_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditPaymentEvent.PaymentAmountChanged(it))
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(PAYMENT_NOTE_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.paymentNote,
                    label = PAYMENT_NOTE_FIELD,
                    leadingIcon = Icons.Default.Description,
                    isError = noteError != null,
                    errorText = noteError,
                    errorTextTag = PAYMENT_NOTE_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditPaymentEvent.PaymentNoteChanged(it))
                    }
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
                        icon = Icons.AutoMirrored.Filled.MergeType
                    )

                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row {
                        PaymentType.entries.forEach { type ->
                            ElevatedFilterChip(
                                modifier = Modifier.testTag(PAYMENT_TYPE_FIELD.plus(type.name)),
                                selected = viewModel.state.paymentType == type,
                                onClick = {
                                    viewModel.onEvent(AddEditPaymentEvent.PaymentTypeChanged(type))
                                },
                                label = {
                                    Text(text = type.name)
                                }
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
                        icon = Icons.AutoMirrored.Filled.MergeType
                    )

                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row {
                        PaymentMode.entries.forEach { type ->
                            ElevatedFilterChip(
                                modifier = Modifier.testTag(PAYMENT_MODE_FIELD.plus(type.name)),
                                selected = viewModel.state.paymentMode == type,
                                onClick = {
                                    viewModel.onEvent(AddEditPaymentEvent.PaymentModeChanged(type))
                                },
                                label = {
                                    Text(text = type.name)
                                },
                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
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
        }
    ) {
        datepicker(
            allowedDateValidator = { date ->
                if (selectedEmployee.employeeId != 0) {
                    (date.toMilliSecond >= selectedEmployee.employeeJoinedDate) && (date <= LocalDate.now())
                } else date == LocalDate.now()
            }
        ) {date ->
            viewModel.onEvent(AddEditPaymentEvent.PaymentDateChanged(date.toMilliSecond))
        }
    }
}