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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.niyaj.common.tags.AbsentScreenTags.ADD_EDIT_ABSENT_ENTRY_BUTTON
import com.niyaj.common.tags.AbsentScreenTags.ADD_EDIT_ABSENT_SCREEN
import com.niyaj.common.tags.AbsentScreenTags.CREATE_NEW_ABSENT
import com.niyaj.common.tags.AbsentScreenTags.EDIT_ABSENT_ITEM
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
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

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AddEditAbsentScreen(
    absentId: Int = 0,
    employeeId: Int = 0,
    navigator: DestinationsNavigator,
    viewModel: AddEditAbsentViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val lazyListState = rememberLazyListState()

    val employees = viewModel.employees.collectAsStateWithLifecycle().value

    val employeeError = viewModel.employeeError.collectAsStateWithLifecycle().value
    val dateError = viewModel.dateError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(employeeError, dateError).all { it == null }

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

    var employeeToggled by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val selectedEmployee = viewModel.selectedEmployee.collectAsStateWithLifecycle().value

    val dialogState = rememberMaterialDialogState()

    val title = if (absentId == 0) CREATE_NEW_ABSENT else EDIT_ABSENT_ITEM

    TrackScreenViewEvent(screenName = "Add/Edit Absent Screen/employeeId: $employeeId/absentId: $absentId")

    PoposSecondaryScaffold(
        title = title,
        onBackClick = navigator::navigateUp,
        showBottomBar = true,
        showBackButton = true,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_ABSENT_ENTRY_BUTTON)
                    .padding(SpaceMedium),
                enabled = enableBtn,
                text = title,
                icon = if (absentId == 0) PoposIcons.Add else PoposIcons.EditCalender,
                onClick = {
                    viewModel.onEvent(AddEditAbsentEvent.CreateOrUpdateAbsent(absentId))
                },
            )
        },
    ) { paddingValues ->
        TrackScrollJank(scrollableState = lazyListState, stateName = "Add/Edit Absent Screen Field")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(ADD_EDIT_ABSENT_SCREEN)
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
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
                                // This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            }
                            .menuAnchor(),
                        value = selectedEmployee.employeeName,
                        label = ABSENT_EMPLOYEE_NAME_FIELD,
                        leadingIcon = PoposIcons.Person4,
                        isError = employeeError != null,
                        errorText = employeeError,
                        readOnly = true,
                        errorTextTag = ABSENT_EMPLOYEE_NAME_ERROR,
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = employeeToggled,
                            )
                        },
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
                                    viewModel.onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))
                                    employeeToggled = false
                                },
                                text = {
                                    Text(text = employee.employeeName)
                                },
                                leadingIcon = {
                                    CircularBox(
                                        icon = PoposIcons.Person4,
                                        doesSelected = false,
                                        size = 30.dp,
                                        showBorder = false,
                                        text = employee.employeeName,
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
                            onClick = {
                                navigator.navigate(Screens.ADD_EDIT_EMPLOYEE_SCREEN)
                            },
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

            item(ABSENT_DATE_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.absentDate.toPrettyDate(),
                    label = ABSENT_DATE_FIELD,
                    leadingIcon = PoposIcons.CalenderToday,
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
                            Icon(imageVector = PoposIcons.ArrowRightAlt, "Click Here")
                        }
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(ABSENT_REASON_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.absentReason,
                    label = ABSENT_REASON_FIELD,
                    leadingIcon = PoposIcons.Description,
                    onValueChange = {
                        viewModel.onEvent(AddEditAbsentEvent.AbsentReasonChanged(it))
                    },
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
            viewModel.onEvent(AddEditAbsentEvent.AbsentDateChanged(date.toMilliSecond))
        }
    }
}
