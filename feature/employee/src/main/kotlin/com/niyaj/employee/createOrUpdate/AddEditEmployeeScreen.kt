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

package com.niyaj.employee.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.EmployeeTestTags.ADD_EDIT_EMPLOYEE_BTN
import com.niyaj.common.tags.EmployeeTestTags.CREATE_NEW_EMPLOYEE
import com.niyaj.common.tags.EmployeeTestTags.EDIT_EMPLOYEE
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_EMAIL_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_JOINED_DATE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PARTNER_CHECKED_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PARTNER_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PARTNER_UNCHECKED_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_POSITION_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_POSITION_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_TYPE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_TYPE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.QR_CODE_NOTE
import com.niyaj.common.utils.toJoinedDate
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.StandardRoundedFilterChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.PhoneNoCountBox
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardCheckboxWithText
import com.niyaj.ui.components.StandardOutlinedTextField
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

@Composable
@Destination(route = Screens.ADD_EDIT_EMPLOYEE_SCREEN)
fun AddEditEmployeeScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    employeeId: Int = 0,
    viewModel: AddEditEmployeeViewModel = hiltViewModel(),
) {
    val phoneError by viewModel.phoneError.collectAsStateWithLifecycle()
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val salaryError by viewModel.salaryError.collectAsStateWithLifecycle()
    val positionError by viewModel.positionError.collectAsStateWithLifecycle()
    val scannedBitmap by viewModel.scannedBitmap.collectAsStateWithLifecycle()

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
    val icon = if (employeeId == 0) PoposIcons.Add else PoposIcons.Edit

    TrackScreenViewEvent(screenName = Screens.ADD_EDIT_EMPLOYEE_SCREEN)

    AddEditEmployeeScreenContent(
        title = title,
        icon = icon,
        modifier = modifier,
        state = viewModel.state,
        phoneError = phoneError,
        nameError = nameError,
        salaryError = salaryError,
        positionError = positionError,
        scannedBitmap = scannedBitmap?.asImageBitmap(),
        onBackClick = navigator::navigateUp,
        onEvent = viewModel::onEvent,
    )
}

@VisibleForTesting
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("LongMethod")
internal fun AddEditEmployeeScreenContent(
    state: AddEditEmployeeState,
    onEvent: (AddEditEmployeeEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    phoneError: String? = null,
    nameError: String? = null,
    salaryError: String? = null,
    positionError: String? = null,
    scannedBitmap: ImageBitmap? = null,
    title: String = CREATE_NEW_EMPLOYEE,
    icon: ImageVector = PoposIcons.Add,
) {
    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()

    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val enableBtn = listOf(phoneError, nameError, salaryError, positionError).all {
        it == null
    }

    PoposSecondaryScaffold(
        title = title,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = true,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_EMPLOYEE_BTN),
                text = title,
                icon = icon,
                enabled = enableBtn,
                onClick = {
                    onEvent(AddEditEmployeeEvent.CreateOrUpdateEmployee)
                },
            )
        },
    ) { paddingValues ->
        TrackScrollJank(scrollableState = lazyListState, stateName = "Add/Edit Employee::Fields")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag("addEditEmployeeFields")
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(EMPLOYEE_NAME_FIELD) {
                StandardOutlinedTextField(
                    label = EMPLOYEE_NAME_FIELD,
                    leadingIcon = PoposIcons.Person,
                    value = state.employeeName,
                    onValueChange = {
                        onEvent(AddEditEmployeeEvent.EmployeeNameChanged(it))
                    },
                    isError = nameError != null,
                    errorText = nameError,
                    showClearIcon = state.employeeName.isNotEmpty(),
                    errorTextTag = EMPLOYEE_NAME_ERROR,
                    onClickClearIcon = {
                        onEvent(AddEditEmployeeEvent.EmployeeNameChanged(""))
                    },
                )
            }

            item(EMPLOYEE_PHONE_FIELD) {
                StandardOutlinedTextField(
                    label = EMPLOYEE_PHONE_FIELD,
                    leadingIcon = PoposIcons.PhoneAndroid,
                    value = state.employeePhone,
                    onValueChange = {
                        onEvent(AddEditEmployeeEvent.EmployeePhoneChanged(it))
                    },
                    isError = phoneError != null,
                    errorText = phoneError,
                    keyboardType = KeyboardType.Number,
                    showClearIcon = state.employeePhone.isNotEmpty(),
                    errorTextTag = EMPLOYEE_PHONE_ERROR,
                    suffix = {
                        AnimatedVisibility(
                            visible = state.employeePhone.length != 10,
                        ) {
                            PhoneNoCountBox(
                                count = state.employeePhone.length,
                            )
                        }
                    },
                    onClickClearIcon = {
                        onEvent(AddEditEmployeeEvent.EmployeePhoneChanged(""))
                    },
                )
            }

            item(EMPLOYEE_SALARY_FIELD) {
                StandardOutlinedTextField(
                    label = EMPLOYEE_SALARY_FIELD,
                    leadingIcon = PoposIcons.Money,
                    value = state.employeeSalary,
                    onValueChange = {
                        onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged(it))
                    },
                    isError = salaryError != null,
                    errorText = salaryError,
                    keyboardType = KeyboardType.Number,
                    showClearIcon = state.employeeSalary.isNotEmpty(),
                    errorTextTag = EMPLOYEE_SALARY_ERROR,
                    onClickClearIcon = {
                        onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged(""))
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
                        label = EMPLOYEE_POSITION_FIELD,
                        leadingIcon = PoposIcons.Radar,
                        value = state.employeePosition,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                // This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            }
                            .menuAnchor(),
                        isError = positionError != null,
                        errorText = positionError,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded,
                            )
                        },
                        readOnly = true,
                        errorTextTag = EMPLOYEE_POSITION_ERROR,
                    )

                    if (positions.isNotEmpty()) {
                        DropdownMenu(
                            modifier = Modifier
                                .testTag("positionList")
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
                                        onEvent(
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
                    label = EMPLOYEE_EMAIL_FIELD,
                    leadingIcon = PoposIcons.Email,
                    value = state.employeeEmail ?: "",
                    onValueChange = {
                        onEvent(AddEditEmployeeEvent.EmployeeEmailChanged(it))
                    },
                    keyboardType = KeyboardType.Email,
                    showClearIcon = !state.employeeEmail.isNullOrEmpty(),
                    onClickClearIcon = {
                        onEvent(AddEditEmployeeEvent.EmployeeEmailChanged(""))
                    },
                )
            }

            item(EMPLOYEE_JOINED_DATE_FIELD) {
                StandardOutlinedTextField(
                    label = EMPLOYEE_JOINED_DATE_FIELD,
                    leadingIcon = PoposIcons.CalenderToday,
                    value = state.employeeJoinedDate.toJoinedDate,
                    onValueChange = {},
                    trailingIcon = {
                        IconButton(
                            onClick = { dialogState.show() },
                            modifier = Modifier.testTag("datePicker"),
                        ) {
                            Icon(
                                imageVector = PoposIcons.CalenderMonth,
                                contentDescription = "Choose a date",
                            )
                        }
                    },
                    readOnly = true,
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
                        text = EMPLOYEE_TYPE_FIELD,
                        icon = PoposIcons.PersonPin,
                    )

                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row {
                        EmployeeType.entries.forEach { type ->
                            StandardRoundedFilterChip(
                                text = type.name,
                                modifier = Modifier.testTag(EMPLOYEE_TYPE_FIELD.plus(type.name)),
                                selected = state.employeeType == type,
                                selectedColor = MaterialTheme.colorScheme.primaryContainer,
                                onClick = {
                                    onEvent(
                                        AddEditEmployeeEvent.EmployeeTypeChanged(type),
                                    )
                                },
                            )

                            Spacer(modifier = Modifier.width(SpaceMini))
                        }
                    }
                }
            }

            item(EMPLOYEE_SALARY_TYPE_FIELD) {
                Column(
                    modifier = Modifier
                        .testTag(EMPLOYEE_SALARY_TYPE_FIELD)
                        .fillMaxWidth()
                        .padding(horizontal = SpaceSmall),
                ) {
                    IconWithText(
                        text = EMPLOYEE_SALARY_TYPE_FIELD,
                        icon = PoposIcons.CalenderMonth,
                    )

                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row {
                        EmployeeSalaryType.entries.forEach { type ->
                            StandardRoundedFilterChip(
                                text = type.name,
                                modifier = Modifier
                                    .testTag(EMPLOYEE_SALARY_TYPE_FIELD.plus(type.name)),
                                selected = state.employeeSalaryType == type,
                                onClick = {
                                    onEvent(AddEditEmployeeEvent.EmployeeSalaryTypeChanged(type))
                                },
                            )

                            Spacer(modifier = Modifier.width(SpaceMini))
                        }
                    }
                }
            }

            item(EMPLOYEE_PARTNER_FIELD) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        StandardCheckboxWithText(
                            text = if (state.isDeliveryPartner) {
                                EMPLOYEE_PARTNER_CHECKED_FIELD
                            } else {
                                EMPLOYEE_PARTNER_UNCHECKED_FIELD
                            },
                            checked = state.isDeliveryPartner,
                            onCheckedChange = {
                                onEvent(AddEditEmployeeEvent.UpdateDeliveryPartner)
                            },
                            modifier = Modifier
                                .testTag(EMPLOYEE_PARTNER_FIELD)
                                .weight(2f, false),
                        )

                        Spacer(Modifier.width(SpaceSmall))

                        AnimatedVisibility(
                            visible = state.isDeliveryPartner,
                        ) {
                            StandardRoundedFilterChip(
                                text = if (scannedBitmap != null) "Scanned" else "Scan QR",
                                icon = PoposIcons.QrCodeScanner,
                                selected = scannedBitmap != null,
                                onClick = {
                                    onEvent(AddEditEmployeeEvent.ScanQRCode)
                                },
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = state.isDeliveryPartner && scannedBitmap == null,
                    ) {
                        NoteCard(
                            text = QR_CODE_NOTE,
                            onClick = { onEvent(AddEditEmployeeEvent.ScanQRCode) },
                        )
                    }
                }
            }

            item("scannedBitmap") {
                AnimatedVisibility(
                    visible = scannedBitmap != null,
                ) {
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (scannedBitmap != null) {
                            Image(
                                bitmap = scannedBitmap,
                                contentDescription = "QR Code",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
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
            onEvent(AddEditEmployeeEvent.EmployeeJoinedDateChanged(date.toMilliSecond))
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditEmployeeScreenContentPreview() {
    PoposRoomTheme {
        AddEditEmployeeScreenContent(
            title = CREATE_NEW_EMPLOYEE,
            icon = PoposIcons.Add,
            state = AddEditEmployeeState(
                employeePhone = "9078563421",
                employeeName = "Leopoldo Rodriguez",
                employeeSalary = "12000",
                employeePosition = "Chef",
                employeeEmail = null,
                employeeSalaryType = EmployeeSalaryType.Monthly,
                employeeType = EmployeeType.FullTime,
                isDeliveryPartner = true,
                partnerQRCode = "No QR",
            ),
            phoneError = null,
            nameError = null,
            salaryError = null,
            positionError = null,
            onBackClick = {},
            onEvent = {},
        )
    }
}
