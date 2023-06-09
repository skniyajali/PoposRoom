package com.niyaj.poposroom.features.employee_absent.presentation.add_edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Person4
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.poposroom.features.common.components.StandardButton
import com.niyaj.poposroom.features.common.components.StandardOutlinedTextField
import com.niyaj.poposroom.features.common.components.StandardScaffoldWithOutDrawer
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmallMax
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.toMilliSecond
import com.niyaj.poposroom.features.common.utils.toPrettyDate
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_DATE_ERROR
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_DATE_FIELD
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_EMPLOYEE_NAME_ERROR
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_EMPLOYEE_NAME_FIELD
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_REASON_FIELD
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ADD_EDIT_ABSENT_ENTRY_BUTTON
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ADD_EDIT_ABSENT_SCREEN
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.CREATE_NEW_ABSENT
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.EDIT_ABSENT_ITEM
import com.niyaj.poposroom.features.employee_payment.domain.utils.PaymentScreenTags.CREATE_NEW_PAYMENT
import com.niyaj.poposroom.features.employee_payment.domain.utils.PaymentScreenTags.EDIT_PAYMENT_ITEM
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AddEditAbsentScreen(
    absentId: Int = 0,
    employeeId: Int = 0,
    navController: NavController,
    viewModel: AddEditAbsentViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val lazyListState = rememberLazyListState()

    val employees = viewModel.employees.collectAsStateWithLifecycle().value

    val employeeError = viewModel.employeeError.collectAsStateWithLifecycle().value
    val dateError = viewModel.dateError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(employeeError, dateError).all { it == null }

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when(data) {
                is UiEvent.IsLoading -> {}
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

    val title = if (absentId == 0) CREATE_NEW_PAYMENT else EDIT_PAYMENT_ITEM

    StandardScaffoldWithOutDrawer(
        title = title,
        onBackClick = {
            navController.navigateUp()
        },
        showBottomBar = enableBtn,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_ABSENT_ENTRY_BUTTON)
                    .padding(horizontal = SpaceSmallMax),
                enabled = enableBtn,
                text = if (absentId == 0) CREATE_NEW_ABSENT else EDIT_ABSENT_ITEM,
                icon = if (absentId == 0) Icons.Default.Add else Icons.Default.EditCalendar,
                onClick = {
                    viewModel.onEvent(AddEditAbsentEvent.CreateOrUpdateAbsent(absentId))
                }
            )
        }
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(ADD_EDIT_ABSENT_SCREEN)
                .fillMaxWidth()
                .padding(SpaceSmall),
        ) {
            item(ABSENT_EMPLOYEE_NAME_FIELD) {
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
                        label = ABSENT_EMPLOYEE_NAME_FIELD,
                        leadingIcon = Icons.Default.Person4,
                        isError = employeeError != null,
                        errorText = employeeError,
                        readOnly = true,
                        errorTextTag = ABSENT_EMPLOYEE_NAME_ERROR,
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = employeeToggled
                            )
                        },
                    )

                    DropdownMenu(
                        expanded = employees.isNotEmpty() && employeeToggled,
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
                                    viewModel.onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))
                                    viewModel.onEvent(
                                        AddEditAbsentEvent.EmployeeChanged(employee.employeeId)
                                    )
                                    employeeToggled = false
                                },
                                text = {
                                    Text(text = employee.employeeName)
                                }
                            )

                            if(index != employees.size - 1) {
                                Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(ABSENT_DATE_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.absentDate.toPrettyDate(),
                    label = ABSENT_DATE_FIELD,
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
                    errorTextTag = ABSENT_DATE_ERROR,
                    onValueChange = {},
                    suffix = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "Click Here")
                            Spacer(modifier = Modifier.width(SpaceMini))
                            Icon(imageVector = Icons.Default.ArrowForward, null)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }


            item(ABSENT_REASON_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.absentReason,
                    label = ABSENT_REASON_FIELD,
                    leadingIcon = Icons.Default.Description,
                    onValueChange = {
                        viewModel.onEvent(AddEditAbsentEvent.AbsentReasonChanged(it))
                    }
                )

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
            viewModel.onEvent(AddEditAbsentEvent.AbsentDateChanged(date.toMilliSecond))
        }
    }
}