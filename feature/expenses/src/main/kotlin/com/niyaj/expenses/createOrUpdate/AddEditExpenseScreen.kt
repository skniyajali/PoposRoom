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

package com.niyaj.expenses.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.ExpenseTestTags.ADD_EDIT_EXPENSE_BUTTON
import com.niyaj.common.tags.ExpenseTestTags.CREATE_NEW_EXPENSE
import com.niyaj.common.tags.ExpenseTestTags.EDIT_EXPENSE_ITEM
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_AMOUNT_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_AMOUNT_FIELD
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_DATE_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_DATE_FIELD
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_FIELD
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NOTE_FIELD
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.parameterProvider.ExpensePreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.datetime.Clock
import java.time.LocalDate

@Destination
@Composable
fun AddEditExpenseScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    expenseId: Int = 0,
    viewModel: AddEditExpenseViewModel = hiltViewModel(),
) {
    TrackScreenViewEvent(screenName = "Add/Edit Expenses Screen/$expenseId")

    val dateError by viewModel.dateError.collectAsStateWithLifecycle()
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val amountError by viewModel.priceError.collectAsStateWithLifecycle()
    val existError by viewModel.existingData.collectAsStateWithLifecycle()
    val expensesNames by viewModel.expensesName.collectAsStateWithLifecycle()
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

    val title = if (expenseId == 0) CREATE_NEW_EXPENSE else EDIT_EXPENSE_ITEM
    val icon = if (expenseId == 0) PoposIcons.Add else PoposIcons.Edit

    AddEditExpenseScreenContent(
        modifier = modifier,
        title = title,
        icon = icon,
        state = viewModel.state,
        expensesNames = expensesNames,
        dateError = dateError,
        nameError = nameError,
        amountError = amountError,
        existError = existError,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
    )
}

@VisibleForTesting
@Composable
@Suppress("LongMethod")
internal fun AddEditExpenseScreenContent(
    state: AddEditExpenseState,
    expensesNames: List<String>,
    onEvent: (AddEditExpenseEvent) -> Unit,
    onBackClick: () -> Unit,
    dateError: String?,
    nameError: String?,
    amountError: String?,
    existError: String?,
    modifier: Modifier = Modifier,
    title: String = CREATE_NEW_EXPENSE,
    icon: ImageVector = PoposIcons.Add,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val dialogState = rememberMaterialDialogState()

    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val enableBtn = listOf(dateError, nameError, amountError).all { it == null }

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
                    .testTag(ADD_EDIT_EXPENSE_BUTTON),
                text = title,
                icon = icon,
                enabled = enableBtn,
                onClick = {
                    onEvent(AddEditExpenseEvent.AddOrUpdateExpense)
                },
            )
        },
    ) { paddingValues ->
        TrackScrollJank(
            scrollableState = lazyListState,
            stateName = "Add/Edit Expenses Screen::Field",
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(EXPENSE_NAME_FIELD) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StandardOutlinedTextField(
                        label = EXPENSE_NAME_FIELD,
                        leadingIcon = PoposIcons.Radar,
                        value = state.expenseName,
                        onValueChange = {
                            expanded = true
                            onEvent(AddEditExpenseEvent.ExpensesNameChanged(it))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                // This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        isError = nameError != null,
                        errorText = nameError,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded,
                            )
                        },
                        showClearIcon = state.expenseName.isNotEmpty(),
                        readOnly = false,
                        errorTextTag = EXPENSE_NAME_ERROR,
                        onClickClearIcon = {
                            expanded = true
                            onEvent(AddEditExpenseEvent.ExpensesNameChanged(""))
                        },
                    )

                    if (expensesNames.isNotEmpty()) {
                        DropdownMenu(
                            modifier = Modifier
                                .heightIn(max = 200.dp)
                                .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            properties = PopupProperties(
                                focusable = false,
                                dismissOnBackPress = true,
                                dismissOnClickOutside = true,
                                excludeFromSystemGesture = true,
                                clippingEnabled = true,
                            ),
                        ) {
                            expensesNames.forEachIndexed { index, name ->
                                DropdownMenuItem(
                                    modifier = Modifier
                                        .testTag(name)
                                        .fillMaxWidth(),
                                    text = { Text(name) },
                                    onClick = {
                                        expanded = false
                                        onEvent(
                                            AddEditExpenseEvent.ExpensesNameChanged(name),
                                        )
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )

                                if (index != expensesNames.size - 1) {
                                    HorizontalDivider(
                                        modifier = Modifier.fillMaxWidth(),
                                        thickness = 0.8.dp,
                                        color = Color.Gray,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(EXPENSE_AMOUNT_FIELD) {
                StandardOutlinedTextField(
                    label = EXPENSE_AMOUNT_FIELD,
                    leadingIcon = PoposIcons.PhoneAndroid,
                    value = state.expenseAmount,
                    onValueChange = {
                        onEvent(AddEditExpenseEvent.ExpensesAmountChanged(it))
                    },
                    isError = amountError != null,
                    errorText = amountError,
                    keyboardType = KeyboardType.Number,
                    showClearIcon = state.expenseAmount.isNotEmpty(),
                    errorTextTag = EXPENSE_AMOUNT_ERROR,
                    onClickClearIcon = {
                        onEvent(AddEditExpenseEvent.ExpensesAmountChanged(""))
                    },
                )
            }

            item(EXPENSE_DATE_FIELD) {
                StandardOutlinedTextField(
                    label = EXPENSE_DATE_FIELD,
                    leadingIcon = PoposIcons.CalenderToday,
                    value = state.expenseDate.toPrettyDate(),
                    onValueChange = {},
                    isError = dateError != null,
                    errorText = dateError,
                    trailingIcon = {
                        IconButton(
                            onClick = dialogState::show,
                            modifier = Modifier.testTag("changeDate"),
                        ) {
                            Icon(
                                imageVector = PoposIcons.CalenderMonth,
                                contentDescription = "Choose a date",
                            )
                        }
                    },
                    readOnly = true,
                    errorTextTag = EXPENSE_DATE_ERROR,
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
            }

            item(EXPENSE_NOTE_FIELD) {
                StandardOutlinedTextField(
                    label = EXPENSE_NOTE_FIELD,
                    leadingIcon = PoposIcons.NoteAdd,
                    value = state.expenseNote,
                    onValueChange = {
                        onEvent(AddEditExpenseEvent.ExpensesNoteChanged(it))
                    },
                    showClearIcon = state.expenseNote.isNotEmpty(),
                    onClickClearIcon = {
                        onEvent(AddEditExpenseEvent.ExpensesNoteChanged(""))
                    },
                )
            }

            item("ExistError") {
                existError?.let {
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    ListItem(
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(SpaceMini)),
                        headlineContent = {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        leadingContent = {
                            Icon(imageVector = PoposIcons.Info, contentDescription = "info")
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        ),
                    )
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
            onEvent(AddEditExpenseEvent.ExpensesDateChanged(date.toMilliSecond))
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditExpenseScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AddEditExpenseScreenContent(
            modifier = modifier,
            state = AddEditExpenseState(
                expenseName = "Gas",
                expenseDate = Clock.System.now().toEpochMilliseconds().toString(),
                expenseAmount = "500",
                expenseNote = "This is test note",
            ),
            expensesNames = ExpensePreviewData.expenseNames,
            dateError = null,
            nameError = null,
            amountError = null,
            existError = null,
            onEvent = {},
            onBackClick = {},
        )
    }
}
