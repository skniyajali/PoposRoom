package com.niyaj.poposroom.features.employee.presentation.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.poposroom.features.common.components.StandardButton
import com.niyaj.poposroom.features.common.components.StandardTextField
import com.niyaj.poposroom.features.common.components.TextWithIcon
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.Constants
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.toJoinedDate
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeSalaryType
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.ADD_EDIT_EMPLOYEE_BUTTON
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.CREATE_NEW_EMPLOYEE
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EDIT_EMPLOYEE
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_EMAIL_FIELD
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_JOINED_DATE_FIELD
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_NAME_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_NAME_FIELD
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_PHONE_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_PHONE_FIELD
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_POSITION_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_POSITION_FIELD
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_SALARY_ERROR
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_SALARY_FIELD
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_SALARY_TYPE_FIELD
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeTestTags.EMPLOYEE_TYPE_FIELD
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun AddEditEmployeeScreen(
    employeeId: Int = 0,
    navController: NavController,
    viewModel: AddEditEmployeeViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val phoneError = viewModel.phoneError.collectAsStateWithLifecycle().value
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val salaryError = viewModel.salaryError.collectAsStateWithLifecycle().value
    val positionError = viewModel.positionError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(phoneError, nameError, salaryError, positionError).all {
        it == null
    }

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

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

    val title = if (employeeId == 0) CREATE_NEW_EMPLOYEE else EDIT_EMPLOYEE

    var expanded by remember { mutableStateOf(false) }

    val positions by remember(viewModel.state.employeePosition) {
        derivedStateOf {
            positions.filter { it.contains(viewModel.state.employeePosition) }
        }
    }

    val openDialog = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = viewModel.state.employeeJoinedDate.toLong())
    val confirmEnabled = remember(datePickerState) {
        derivedStateOf { datePickerState.selectedDateMillis != null }
    }

    val focusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = Modifier
            .testTag(title)
            .fillMaxWidth(),
        topBar = {
            LargeTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        },
                        modifier = Modifier.testTag(Constants.STANDARD_BACK_BUTTON)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.scrim
                        )
                    }
                },
                title = {
                    Text(text = title)
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(SpaceSmall)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            verticalArrangement = Arrangement.Center,
        ) {
            item(EMPLOYEE_NAME_FIELD) {
                StandardTextField(
                    value = viewModel.state.employeeName,
                    label = EMPLOYEE_NAME_FIELD,
                    leadingIcon = Icons.Default.Person,
                    isError = nameError != null,
                    errorText = nameError,
                    errorTextTag = EMPLOYEE_NAME_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged(it))
                    }
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(EMPLOYEE_PHONE_FIELD) {
                StandardTextField(
                    value = viewModel.state.employeePhone,
                    label = EMPLOYEE_PHONE_FIELD,
                    leadingIcon = Icons.Default.PhoneAndroid,
                    isError = phoneError != null,
                    errorText = phoneError,
                    errorTextTag = EMPLOYEE_PHONE_ERROR,
                    keyboardType = KeyboardType.Number,
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged(it))
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(EMPLOYEE_SALARY_FIELD) {
                StandardTextField(
                    value = viewModel.state.employeeSalary,
                    label = EMPLOYEE_SALARY_FIELD,
                    leadingIcon = Icons.Default.Money,
                    isError = salaryError != null,
                    errorText = salaryError,
                    errorTextTag = EMPLOYEE_SALARY_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged(it))
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(EMPLOYEE_POSITION_FIELD) {
                ExposedDropdownMenuBox(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    StandardTextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .menuAnchor(),
                        value = viewModel.state.employeePosition,
                        label = EMPLOYEE_POSITION_FIELD,
                        leadingIcon = Icons.Default.Radar,
                        isError = positionError != null,
                        errorText = positionError,
                        errorTextTag = EMPLOYEE_POSITION_ERROR,
                        onValueChange = {
                            viewModel.onEvent(AddEditEmployeeEvent.EmployeePositionChanged(it))
                            expanded = true
                        }
                    )

                    if(positions.isNotEmpty()) {
                        ExposedDropdownMenu(
                            modifier = Modifier
                                .fillMaxWidth()
                                .exposedDropdownSize(true),
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            positions.forEach { position ->
                                DropdownMenuItem(
                                    text = { Text(position) },
                                    onClick = {
                                        expanded = false
                                        viewModel.onEvent(
                                            AddEditEmployeeEvent.EmployeePositionChanged(position)
                                        )
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(EMPLOYEE_EMAIL_FIELD) {
                StandardTextField(
                    value = viewModel.state.employeeEmail ?: "",
                    label = EMPLOYEE_EMAIL_FIELD,
                    leadingIcon = Icons.Default.Email,
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeeEmailChanged(it))
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(EMPLOYEE_JOINED_DATE_FIELD) {
                StandardTextField(
                    value = viewModel.state.employeeJoinedDate.toJoinedDate,
                    label = EMPLOYEE_JOINED_DATE_FIELD,
                    leadingIcon = Icons.Default.CalendarToday,
                    trailingIcon = Icons.Default.CalendarMonth,
                    readOnly = true,
                    onValueChange = {},
                    onTrailingIconClick = {
                        openDialog.value = true
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(EMPLOYEE_TYPE_FIELD) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpaceSmall),
                ) {
                    TextWithIcon(
                        text = "Employee Type",
                        icon = Icons.Default.PersonPin
                    )

                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row {
                        EmployeeType.values().forEach { type ->
                            FilterChip(
                                modifier = Modifier.testTag(EMPLOYEE_TYPE_FIELD.plus(type.name)),
                                selected = viewModel.state.employeeType == type,
                                onClick = {
                                    viewModel.onEvent(AddEditEmployeeEvent.EmployeeTypeChanged(type))
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

            item(EMPLOYEE_SALARY_TYPE_FIELD){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpaceSmall),
                ) {
                    TextWithIcon(
                        text = "Employee Salary Type",
                        icon = Icons.Default.CalendarMonth
                    )

                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row {
                        EmployeeSalaryType.values().forEach { type ->
                            FilterChip(
                                modifier = Modifier.testTag(EMPLOYEE_SALARY_TYPE_FIELD.plus(type.name)),
                                selected = viewModel.state.employeeSalaryType == type,
                                onClick = {
                                    viewModel.onEvent(
                                        AddEditEmployeeEvent.EmployeeSalaryTypeChanged(
                                            type
                                        )
                                    )
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

            item(ADD_EDIT_EMPLOYEE_BUTTON) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardButton(
                    modifier = Modifier.testTag(ADD_EDIT_EMPLOYEE_BUTTON),
                    text = if (employeeId == 0) CREATE_NEW_EMPLOYEE else EDIT_EMPLOYEE,
                    enabled = enableBtn,
                    onClick = {
                        viewModel.onEvent(AddEditEmployeeEvent.CreateOrUpdateEmployee(employeeId))
                    }
                )
            }
        }
    }


    if (openDialog.value) {
        DatePickerDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        viewModel.onEvent(
                            AddEditEmployeeEvent.EmployeeJoinedDateChanged(
                                datePickerState.selectedDateMillis.toString()
                            )
                        )
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}