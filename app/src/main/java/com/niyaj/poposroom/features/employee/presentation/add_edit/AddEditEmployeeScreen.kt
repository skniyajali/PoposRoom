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
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.poposroom.features.common.components.StandardButton
import com.niyaj.poposroom.features.common.components.StandardOutlinedTextField
import com.niyaj.poposroom.features.common.components.StandardScaffoldWithOutDrawer
import com.niyaj.poposroom.features.common.components.TextWithIcon
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmallMax
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.toJoinedDate
import com.niyaj.poposroom.features.common.utils.toMilliSecond
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
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

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

    val dialogState = rememberMaterialDialogState()

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

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
                    .testTag(ADD_EDIT_EMPLOYEE_BUTTON)
                    .padding(horizontal = SpaceSmallMax),
                text = if (employeeId == 0) CREATE_NEW_EMPLOYEE else EDIT_EMPLOYEE,
                icon = if (employeeId == 0) Icons.Default.Add else Icons.Default.Edit,
                enabled = enableBtn,
                onClick = {
                    viewModel.onEvent(AddEditEmployeeEvent.CreateOrUpdateEmployee(employeeId))
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.Center,
        ) {
            item(EMPLOYEE_NAME_FIELD) {
                StandardOutlinedTextField(
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
                StandardOutlinedTextField(
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
                StandardOutlinedTextField(
                    value = viewModel.state.employeeSalary,
                    label = EMPLOYEE_SALARY_FIELD,
                    leadingIcon = Icons.Default.Money,
                    keyboardType = KeyboardType.Number,
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
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            }
                            .menuAnchor(),
                        value = viewModel.state.employeePosition,
                        label = EMPLOYEE_POSITION_FIELD,
                        leadingIcon = Icons.Default.Radar,
                        isError = positionError != null,
                        errorText = positionError,
                        errorTextTag = EMPLOYEE_POSITION_ERROR,
                        readOnly = true,
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                    )

                    if(positions.isNotEmpty()) {
                        DropdownMenu(
                            modifier = Modifier
                                .width(with(LocalDensity.current){textFieldSize.width.toDp()}),
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            positions.forEachIndexed { index, position ->
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .testTag(position)
                                        .fillMaxWidth(),
                                    text = { Text(position) },
                                    onClick = {
                                        expanded = false
                                        viewModel.onEvent(
                                            AddEditEmployeeEvent.EmployeePositionChanged(position)
                                        )
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )

                                if(index != positions.size - 1) {
                                    Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray, thickness = 0.8.dp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(EMPLOYEE_EMAIL_FIELD) {
                StandardOutlinedTextField(
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
                StandardOutlinedTextField(
                    value = viewModel.state.employeeJoinedDate.toJoinedDate,
                    label = EMPLOYEE_JOINED_DATE_FIELD,
                    leadingIcon = Icons.Default.CalendarToday,
                    trailingIcon = {
                        IconButton(
                            onClick = { dialogState.show() }
                        ) {
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Choose a date")
                        }
                    },
                    readOnly = true,
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
                            ElevatedFilterChip(
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
                            ElevatedFilterChip(
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
                                },
                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
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
                date <= LocalDate.now()
            }
        ) {date ->
            viewModel.onEvent(AddEditEmployeeEvent.EmployeeJoinedDateChanged(date.toMilliSecond))
        }
    }
}