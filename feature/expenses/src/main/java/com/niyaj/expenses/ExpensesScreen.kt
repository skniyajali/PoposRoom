package com.niyaj.expenses

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TurnedInNot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.tags.ExpenseTestTags.CREATE_NEW_EXPENSE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_EXPENSE_MESSAGE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_EXPENSE_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NOT_AVAIlABLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SCREEN_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_TAG
import com.niyaj.common.tags.ExpenseTestTags.NO_ITEMS_IN_EXPENSE
import com.niyaj.designsystem.theme.IconSizeSmall
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.expenses.destinations.AddEditExpenseScreenDestination
import com.niyaj.model.Expense
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardOutlinedAssistChip
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.LocalDate

@RootNavGraph(start = true)
@Destination(
    route = Screens.ExpensesScreen
)
@Composable
fun ExpensesScreen(
    navController: NavController,
    viewModel: ExpensesViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditExpenseScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.expenses.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyListState = rememberLazyListState()

    val showFab = viewModel.totalItems.isNotEmpty()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val openDialog = remember { mutableStateOf(false) }
    val dialogState = rememberMaterialDialogState()

    val selectedDate = viewModel.selectedDate.collectAsStateWithLifecycle().value.toPrettyDate()
    val totalAmount = viewModel.totalAmount.collectAsStateWithLifecycle().value.toRupee
    val totalItem = viewModel.totalItems.size.toString()

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.successMessage)
                    }
                }
            }
        }
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
            }
            is NavResult.Value -> {
                scope.launch {
                    viewModel.deselectItems()
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        }
    }

    StandardScaffold(
        navController = navController,
        title = if (selectedItems.isEmpty()) EXPENSE_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_EXPENSE,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditExpenseScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = EXPENSE_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showSearchBar,
                searchText = searchText,
                onEditClick = {
                  navController.navigate(AddEditExpenseScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {},
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = viewModel::closeSearchBar,
        snackbarHostState = snackbarState,
    ) { _ ->
        Column(
            modifier = Modifier
                .padding(SpaceSmall),
        ) {
            TotalExpenses(
                totalAmount = totalAmount,
                totalItem = totalItem,
                selectedDate = selectedDate,
                onDateClick = {
                    dialogState.show()
                }
            )

            when(state) {
                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) EXPENSE_NOT_AVAIlABLE else NO_ITEMS_IN_EXPENSE,
                        buttonText = CREATE_NEW_EXPENSE,
                        onClick = {
                            navController.navigate(AddEditExpenseScreenDestination())
                        }
                    )
                }
                is UiState.Loading -> LoadingIndicator()
                is UiState.Success -> {
                    LazyColumn(
                        state = lazyListState
                    ) {
                        val grouped = state.data.groupBy { it.expenseName }

                        grouped.forEach { (_, expenses) ->
                            if (expenses.size > 1) {
                                item {
                                    GroupedExpensesData(
                                        items = expenses,
                                        doesSelected = {
                                            selectedItems.contains(it)
                                        },
                                        onClick = {
                                            if (selectedItems.isNotEmpty()) {
                                                viewModel.selectItem(it)
                                            }
                                        },
                                        onLongClick = viewModel::selectItem
                                    )
                                }
                            }else {
                                item {
                                    ExpensesData(
                                        item = expenses.first(),
                                        doesSelected = {
                                            selectedItems.contains(it)
                                        },
                                        onClick = {
                                            if (selectedItems.isNotEmpty()) {
                                                viewModel.selectItem(it)
                                            }
                                        },
                                        onLongClick = viewModel::selectItem
                                    )
                                }
                            }
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
        }
    ) {
        datepicker(
            allowedDateValidator = { date ->
                date <= LocalDate.now()
            }
        ) {date ->
            viewModel.selectDate(date.toMilliSecond)
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                viewModel.deselectItems()
            },
            title = {
                Text(text = DELETE_EXPENSE_TITLE)
            },
            text = {
                Text(
                    text = DELETE_EXPENSE_MESSAGE
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        viewModel.deleteItems()
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        viewModel.deselectItems()
                    },
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
fun TotalExpenses(
    totalAmount: String,
    totalItem: String,
    selectedDate: String,
    onDateClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularBox(
                        icon = Icons.Default.TrendingUp,
                        doesSelected = false,
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = "Total Expenses",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                StandardOutlinedAssistChip(
                    text = selectedDate,
                    icon = Icons.Default.CalendarMonth,
                    onClick = onDateClick,
                    trailingIcon = Icons.Default.ArrowDropDown
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmallMax))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmallMax))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = totalAmount,
                    style = MaterialTheme.typography.titleLarge
                )
                NoteText(
                    text = "Total $totalItem Expenses",
                    icon = Icons.Default.TrendingUp
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesData(
    modifier: Modifier = Modifier,
    item: Expense,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
) {
    val borderStroke = if (doesSelected(item.expenseId)) border else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, RoundedCornerShape(SpaceMini))
            } ?: Modifier)
            .clip(RoundedCornerShape(SpaceMini))
            .combinedClickable(
                onClick = {
                    onClick(item.expenseId)
                },
                onLongClick = {
                    onLongClick(item.expenseId)
                },
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ListItem(
                modifier = modifier
                    .testTag(EXPENSE_TAG.plus(item.expenseId))
                    .fillMaxWidth(),
                headlineContent = {
                    Text(
                        text = item.expenseName,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                supportingContent = {
                    Text(text = item.expenseAmount.toRupee)
                },
                leadingContent = {
                    CircularBox(
                        icon = Icons.Default.Person,
                        doesSelected = doesSelected(item.expenseId),
                        text = item.expenseName
                    )
                },
                trailingContent = {
                    NoteText(
                        text = item.expenseDate.toPrettyDate(),
                        icon = Icons.Default.CalendarMonth
                    )
                }
            )

            if (item.expenseNote.isNotEmpty()) {
                NoteText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    text = item.expenseNote,
                    icon = Icons.Default.TurnedInNot,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun GroupedExpensesData(
    modifier: Modifier = Modifier,
    items: List<Expense>,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
) {
    val item = items.first()
    val totalAmount = items.sumOf { it.expenseAmount.toInt() }.toString()
    val notes = items.map { it.expenseNote }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ListItem(
                modifier = modifier
                    .fillMaxWidth(),
                headlineContent = {
                    Text(
                        text = item.expenseName,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                supportingContent = {
                    Text(text = totalAmount.toRupee)
                },
                leadingContent = {
                    CircularBox(
                        icon = Icons.Default.Person,
                        doesSelected = false,
                        text = item.expenseName
                    )
                },
                trailingContent = {
                    NoteText(
                        text = item.expenseDate.toPrettyDate(),
                        icon = Icons.Default.CalendarMonth
                    )
                }
            )

            Spacer(modifier = Modifier.height(SpaceMini))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
            ) {
                items.forEach { expense ->
                    val borderStroke = if (doesSelected(expense.expenseId)) border else null

                    ElevatedCard(
                        modifier = modifier
                            .testTag(EXPENSE_TAG.plus(expense.expenseId))
                            .then(borderStroke?.let {
                                Modifier.border(it, RoundedCornerShape(SpaceMini))
                            } ?: Modifier)
                            .clip(RoundedCornerShape(SpaceMini))
                            .combinedClickable(
                                onClick = {
                                    onClick(expense.expenseId)
                                },
                                onLongClick = {
                                    onLongClick(expense.expenseId)
                                },
                            ),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(
                                imageVector = if (doesSelected(expense.expenseId))
                                    Icons.Default.Check else Icons.Default.CurrencyRupee,
                                contentDescription = null,
                                modifier = Modifier.size(IconSizeSmall)
                            )

                            Spacer(modifier = Modifier.width(SpaceMini))

                            Text(
                                text = expense.expenseAmount,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(SpaceSmall))
                }
            }

            Spacer(modifier = Modifier.height(SpaceMini))

            if (notes.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceMini))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    notes.forEach { note ->
                        NoteText(
                            text = note,
                            icon = Icons.Default.StickyNote2,
                            color = MaterialTheme.colorScheme.error
                        ) 
                        Spacer(modifier = Modifier.height(SpaceMini))
                    }
                }
            }
        }
    }
}