package com.niyaj.employee.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.EmployeeTestTags.ADD_EDIT_EMPLOYEE_BUTTON
import com.niyaj.common.tags.EmployeeTestTags.CREATE_NEW_EMPLOYEE
import com.niyaj.common.tags.EmployeeTestTags.EDIT_EMPLOYEE
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_EMAIL_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_JOINED_DATE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_POSITION_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_POSITION_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_TYPE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_TYPE_FIELD
import com.niyaj.common.utils.toJoinedDate
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.PhoneNoCountBox
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.StandardRoundedFilterChip
import com.niyaj.ui.components.StandardScaffoldRouteNew
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
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(route = Screens.ADD_EDIT_EMPLOYEE_SCREEN)
fun AddEditEmployeeScreen(
    employeeId: Int = 0,
    navigator: DestinationsNavigator,
    viewModel: AddEditEmployeeViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val lazyListState = rememberLazyListState()
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

    val title = if (employeeId == 0) CREATE_NEW_EMPLOYEE else EDIT_EMPLOYEE

    var expanded by remember { mutableStateOf(false) }

    val dialogState = rememberMaterialDialogState()

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    TrackScreenViewEvent(screenName = Screens.ADD_EDIT_EMPLOYEE_SCREEN)

    StandardScaffoldRouteNew(
        title = title,
        showBackButton = true,
        onBackClick = navigator::navigateUp,
        showBottomBar = lazyListState.isScrollingUp(),
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
            ) {
                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ADD_EDIT_EMPLOYEE_BUTTON)
                        .padding(SpaceSmall),
                    text = if (employeeId == 0) CREATE_NEW_EMPLOYEE else EDIT_EMPLOYEE,
                    icon = if (employeeId == 0) PoposIcons.Add else PoposIcons.Edit,
                    enabled = enableBtn,
                    onClick = {
                        viewModel.onEvent(AddEditEmployeeEvent.CreateOrUpdateEmployee(employeeId))
                    },
                )
            }
        },
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Add/Edit Employee::Fields")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item(EMPLOYEE_NAME_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.employeeName,
                    label = EMPLOYEE_NAME_FIELD,
                    leadingIcon = PoposIcons.Person,
                    isError = nameError != null,
                    errorText = nameError,
                    errorTextTag = EMPLOYEE_NAME_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged(it))
                    },
                )
            }

            item(EMPLOYEE_PHONE_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.employeePhone,
                    label = EMPLOYEE_PHONE_FIELD,
                    leadingIcon = PoposIcons.PhoneAndroid,
                    isError = phoneError != null,
                    errorText = phoneError,
                    errorTextTag = EMPLOYEE_PHONE_ERROR,
                    keyboardType = KeyboardType.Number,
                    trailingIcon = {
                        PhoneNoCountBox(
                            count = viewModel.state.employeePhone.length,
                        )
                    },
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged(it))
                    },
                )
            }

            item(EMPLOYEE_SALARY_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.employeeSalary,
                    label = EMPLOYEE_SALARY_FIELD,
                    leadingIcon = PoposIcons.Money,
                    keyboardType = KeyboardType.Number,
                    isError = salaryError != null,
                    errorText = salaryError,
                    errorTextTag = EMPLOYEE_SALARY_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged(it))
                    },
                )
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
                        leadingIcon = PoposIcons.Radar,
                        isError = positionError != null,
                        errorText = positionError,
                        errorTextTag = EMPLOYEE_POSITION_ERROR,
                        readOnly = true,
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded,
                            )
                        },
                    )

                    if (positions.isNotEmpty()) {
                        DropdownMenu(
                            modifier = Modifier
                                .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
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
                                            AddEditEmployeeEvent.EmployeePositionChanged(position),
                                        )
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )

                                if (index != positions.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color.Gray,
                                        thickness = 0.8.dp,
                                    )
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
                    leadingIcon = PoposIcons.Email,
                    onValueChange = {
                        viewModel.onEvent(AddEditEmployeeEvent.EmployeeEmailChanged(it))
                    },
                )
            }

            item(EMPLOYEE_JOINED_DATE_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.employeeJoinedDate.toJoinedDate,
                    label = EMPLOYEE_JOINED_DATE_FIELD,
                    leadingIcon = PoposIcons.CalenderToday,
                    trailingIcon = {
                        IconButton(
                            onClick = { dialogState.show() },
                        ) {
                            Icon(
                                imageVector = PoposIcons.CalenderMonth,
                                contentDescription = "Choose a date",
                            )
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
                            Icon(
                                imageVector = PoposIcons.ArrowForward,
                                "Click Here",
                            )
                        }
                    },
                )
            }

            item(EMPLOYEE_TYPE_FIELD) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpaceSmall),
                ) {
                    IconWithText(
                        text = "Employee Type",
                        icon = PoposIcons.PersonPin,
                    )

                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row {
                        EmployeeType.entries.forEach { type ->
                            StandardRoundedFilterChip(
                                modifier = Modifier.testTag(EMPLOYEE_TYPE_FIELD.plus(type.name)),
                                text = type.name,
                                selected = viewModel.state.employeeType == type,
                                onClick = {
                                    viewModel.onEvent(
                                        AddEditEmployeeEvent.EmployeeTypeChanged(type),
                                    )
                                },
                                selectedColor = MaterialTheme.colorScheme.primaryContainer,
                            )

                            Spacer(modifier = Modifier.width(SpaceMini))
                        }
                    }
                }
            }

            item(EMPLOYEE_SALARY_TYPE_FIELD) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = SpaceSmall),
                ) {
                    IconWithText(
                        text = "Employee Salary Type",
                        icon = PoposIcons.CalenderMonth,
                    )

                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row {
                        EmployeeSalaryType.entries.forEach { type ->
                            StandardRoundedFilterChip(
                                modifier = Modifier.testTag(EMPLOYEE_SALARY_TYPE_FIELD.plus(type.name)),
                                text = type.name,
                                selected = viewModel.state.employeeSalaryType == type,
                                onClick = {
                                    viewModel.onEvent(
                                        AddEditEmployeeEvent.EmployeeSalaryTypeChanged(type),
                                    )
                                },
                            )

                            Spacer(modifier = Modifier.width(SpaceMini))
                        }
                    }
                }
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
                date <= LocalDate.now()
            },
        ) { date ->
            viewModel.onEvent(AddEditEmployeeEvent.EmployeeJoinedDateChanged(date.toMilliSecond))
        }
    }
}