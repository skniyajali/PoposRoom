package com.niyaj.expenses.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.data.utils.ExpenseTestTags.ADD_EDIT_EXPENSE_BUTTON
import com.niyaj.data.utils.ExpenseTestTags.CREATE_NEW_EXPENSE
import com.niyaj.data.utils.ExpenseTestTags.EDIT_EXPENSE_ITEM
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_AMOUNT_ERROR
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_AMOUNT_FIELD
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_DATE_ERROR
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_DATE_FIELD
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_NAME_ERROR
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_NAME_FIELD
import com.niyaj.data.utils.ExpenseTestTags.EXPENSE_NOTE_FIELD
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.StandardScaffoldWithOutDrawer
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AddEditExpenseScreen(
    expenseId: Int = 0,
    navController: NavController,
    viewModel: AddEditExpenseViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {

    val dateError = viewModel.dateError.collectAsStateWithLifecycle().value
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val amountError = viewModel.priceError.collectAsStateWithLifecycle().value
    val existError = viewModel.existingData.collectAsStateWithLifecycle().value

    val enableBtn = listOf(dateError, nameError, amountError).all {
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

    val title = if (expenseId == 0) CREATE_NEW_EXPENSE else EDIT_EXPENSE_ITEM

    var expanded by remember { mutableStateOf(false) }

    val expensesNames = viewModel.expensesName.collectAsStateWithLifecycle().value

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
                    .testTag(ADD_EDIT_EXPENSE_BUTTON)
                    .padding(horizontal = SpaceSmallMax),
                text = if (expenseId == 0) CREATE_NEW_EXPENSE else EDIT_EXPENSE_ITEM,
                icon = if (expenseId == 0) Icons.Default.Add else Icons.Default.Edit,
                enabled = enableBtn,
                onClick = {
                    viewModel.onEvent(AddEditExpenseEvent.AddOrUpdateExpense(expenseId))
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(EXPENSE_NAME_FIELD) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        value = viewModel.state.expenseName,
                        label = EXPENSE_NAME_FIELD,
                        leadingIcon = Icons.Default.Radar,
                        isError = nameError != null,
                        errorText = nameError,
                        errorTextTag = EXPENSE_NAME_ERROR,
                        readOnly = false,
                        onValueChange = {
                            expanded = true
                            viewModel.onEvent(AddEditExpenseEvent.ExpensesNameChanged(it))
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
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
                                        viewModel.onEvent(
                                            AddEditExpenseEvent.ExpensesNameChanged(name)
                                        )
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )

                                if (index != expensesNames.size - 1) {
                                    Divider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = Color.Gray,
                                        thickness = 0.8.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(EXPENSE_AMOUNT_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.expenseAmount,
                    label = EXPENSE_AMOUNT_FIELD,
                    leadingIcon = Icons.Default.PhoneAndroid,
                    isError = amountError != null,
                    errorText = amountError,
                    errorTextTag = EXPENSE_AMOUNT_ERROR,
                    keyboardType = KeyboardType.Number,
                    onValueChange = {
                        viewModel.onEvent(AddEditExpenseEvent.ExpensesAmountChanged(it))
                    }
                )
            }

            item(EXPENSE_DATE_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.expenseDate.toPrettyDate(),
                    label = EXPENSE_DATE_FIELD,
                    leadingIcon = Icons.Default.CalendarToday,
                    trailingIcon = {
                        IconButton(
                            onClick = { dialogState.show() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Choose a date"
                            )
                        }
                    },
                    readOnly = true,
                    isError = dateError != null,
                    errorText = dateError,
                    errorTextTag = EXPENSE_DATE_ERROR,
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
            }

            item(EXPENSE_NOTE_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.expenseNote,
                    label = EXPENSE_NOTE_FIELD,
                    leadingIcon = Icons.Default.NoteAdd,
                    onValueChange = {
                        viewModel.onEvent(AddEditExpenseEvent.ExpensesNoteChanged(it))
                    }
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
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        leadingContent = {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "info")
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        )
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
        }
    ) {
        datepicker(
            allowedDateValidator = { date ->
                date <= LocalDate.now()
            }
        ) { date ->
            viewModel.onEvent(AddEditExpenseEvent.ExpensesDateChanged(date.toMilliSecond))
        }
    }
}