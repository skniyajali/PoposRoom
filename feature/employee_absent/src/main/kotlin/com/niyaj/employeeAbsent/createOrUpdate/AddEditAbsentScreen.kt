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

package com.niyaj.employeeAbsent.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_ERROR
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_FIELD
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_EMPLOYEE_NAME_ERROR
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_EMPLOYEE_NAME_FIELD
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_REASON_FIELD
import com.niyaj.common.tags.AbsentScreenTags.ADD_EDIT_ABSENT_BTN
import com.niyaj.common.tags.AbsentScreenTags.ADD_EDIT_ABSENT_SCREEN
import com.niyaj.common.tags.AbsentScreenTags.CREATE_NEW_ABSENT
import com.niyaj.common.tags.AbsentScreenTags.EDIT_ABSENT_ITEM
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposTonalIconButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Employee
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.parameterProvider.EmployeePreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Destination(route = Screens.ADD_EDIT_ABSENT_SCREEN)
@Composable
fun AddEditAbsentScreen(
    navigator: DestinationsNavigator,
    onClickAddEmployee: () -> Unit,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    absentId: Int = 0,
    employeeId: Int = 0,
    viewModel: AddEditAbsentViewModel = hiltViewModel(),
) {
    val employees by viewModel.employees.collectAsStateWithLifecycle()
    val employeeError by viewModel.employeeError.collectAsStateWithLifecycle()
    val dateError by viewModel.dateError.collectAsStateWithLifecycle()
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

    val title = if (absentId == 0) CREATE_NEW_ABSENT else EDIT_ABSENT_ITEM
    val icon = if (absentId == 0) PoposIcons.Add else PoposIcons.EditCalender

    TrackScreenViewEvent(screenName = "${ADD_EDIT_ABSENT_SCREEN}/employeeId: $employeeId/absentId: $absentId")

    AddEditAbsentScreenContent(
        employees = employees,
        state = viewModel.state,
        selectedEmployee = selectedEmployee,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
        onClickAddEmployee = onClickAddEmployee,
        modifier = modifier,
        employeeError = employeeError,
        dateError = dateError,
        title = title,
        icon = icon,
    )
}

@VisibleForTesting
@Composable
@Suppress("LongMethod")
internal fun AddEditAbsentScreenContent(
    employees: List<Employee>,
    state: AddEditAbsentState,
    selectedEmployee: Employee,
    onEvent: (AddEditAbsentEvent) -> Unit,
    onBackClick: () -> Unit,
    onClickAddEmployee: () -> Unit,
    modifier: Modifier = Modifier,
    employeeError: String? = null,
    dateError: String? = null,
    title: String = CREATE_NEW_ABSENT,
    icon: ImageVector = PoposIcons.Add,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val dialogState = rememberMaterialDialogState()

    var employeeToggled by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val enableBtn = listOf(employeeError, dateError).all { it == null }

    PoposSecondaryScaffold(
        title = title,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = true,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_ABSENT_BTN),
                enabled = enableBtn,
                text = title,
                icon = icon,
                onClick = {
                    onEvent(AddEditAbsentEvent.CreateOrUpdateAbsent)
                },
            )
        },
    ) { paddingValues ->
        TrackScrollJank(scrollableState = lazyListState, stateName = "Add/Edit Absent Screen Field")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(ABSENT_EMPLOYEE_NAME_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = employees.isNotEmpty() && employeeToggled,
                    onExpandedChange = {
                        employeeToggled = !employeeToggled
                    },
                ) {
                    StandardOutlinedTextField(
                        label = ABSENT_EMPLOYEE_NAME_FIELD,
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
                        errorTextTag = ABSENT_EMPLOYEE_NAME_ERROR,
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
                                    onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))
                                    employeeToggled = false
                                },
                                text = {
                                    Text(text = employee.employeeName)
                                },
                                leadingIcon = {
                                    CircularBox(
                                        icon = PoposIcons.Person4,
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
                            onClick = onClickAddEmployee,
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
            }

            item(ABSENT_DATE_FIELD) {
                StandardOutlinedTextField(
                    label = ABSENT_DATE_FIELD,
                    leadingIcon = PoposIcons.CalenderToday,
                    value = state.absentDate.toPrettyDate(),
                    onValueChange = {},
                    isError = dateError != null,
                    errorText = dateError,
                    trailingIcon = {
                        PoposTonalIconButton(
                            icon = PoposIcons.CalenderMonth,
                            onClick = dialogState::show,
                            modifier = Modifier.testTag("ChooseDate"),
                        )
                    },
                    readOnly = true,
                    errorTextTag = ABSENT_DATE_ERROR,
                    suffix = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "Click Here")
                            Spacer(modifier = Modifier.width(SpaceMini))
                            Icon(imageVector = PoposIcons.ArrowRightAlt, "Click Here")
                        }
                    },
                )
            }

            item(ABSENT_REASON_FIELD) {
                StandardOutlinedTextField(
                    label = ABSENT_REASON_FIELD,
                    leadingIcon = PoposIcons.Description,
                    value = state.absentReason,
                    onValueChange = {
                        onEvent(AddEditAbsentEvent.AbsentReasonChanged(it))
                    },
                    showClearIcon = state.absentReason.isNotEmpty(),
                    onClickClearIcon = {
                        onEvent(AddEditAbsentEvent.AbsentReasonChanged(""))
                    },
                )
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
            onEvent(AddEditAbsentEvent.AbsentDateChanged(date.toMilliSecond))
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditAbsentScreenContentPreview(
    modifier: Modifier = Modifier,
    employees: List<Employee> = EmployeePreviewData.employeeList,
    selectedEmployee: Employee = employees.first(),
) {
    PoposRoomTheme {
        AddEditAbsentScreenContent(
            employees = employees,
            state = AddEditAbsentState(
                absentReason = "Seek Leave",
            ),
            selectedEmployee = selectedEmployee,
            onEvent = {},
            onBackClick = {},
            onClickAddEmployee = {},
            modifier = modifier,
            employeeError = null,
            dateError = null,
        )
    }
}
