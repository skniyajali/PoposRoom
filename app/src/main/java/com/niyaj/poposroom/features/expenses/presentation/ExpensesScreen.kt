package com.niyaj.poposroom.features.expenses.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
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
import com.niyaj.poposroom.features.common.components.CircularBox
import com.niyaj.poposroom.features.common.components.ItemNotAvailable
import com.niyaj.poposroom.features.common.components.LoadingIndicator
import com.niyaj.poposroom.features.common.components.NoteText
import com.niyaj.poposroom.features.common.components.StandardFAB
import com.niyaj.poposroom.features.common.components.StandardOutlinedAssistChip
import com.niyaj.poposroom.features.common.components.StandardScaffold
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.ui.theme.SpaceLarge
import com.niyaj.poposroom.features.common.ui.theme.SpaceMedium
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.isScrolled
import com.niyaj.poposroom.features.common.utils.toMilliSecond
import com.niyaj.poposroom.features.common.utils.toPrettyDate
import com.niyaj.poposroom.features.common.utils.toRupee
import com.niyaj.poposroom.features.destinations.AddEditExpenseScreenDestination
import com.niyaj.poposroom.features.expenses.domain.model.Expense
import com.niyaj.poposroom.features.expenses.domain.utils.ExpenseTestTags.CREATE_NEW_EXPENSE
import com.niyaj.poposroom.features.expenses.domain.utils.ExpenseTestTags.DELETE_EXPENSE_MESSAGE
import com.niyaj.poposroom.features.expenses.domain.utils.ExpenseTestTags.DELETE_EXPENSE_TITLE
import com.niyaj.poposroom.features.expenses.domain.utils.ExpenseTestTags.EXPENSE_NOT_AVAIlABLE
import com.niyaj.poposroom.features.expenses.domain.utils.ExpenseTestTags.EXPENSE_SCREEN_TITLE
import com.niyaj.poposroom.features.expenses.domain.utils.ExpenseTestTags.EXPENSE_SEARCH_PLACEHOLDER
import com.niyaj.poposroom.features.expenses.domain.utils.ExpenseTestTags.EXPENSE_TAG
import com.niyaj.poposroom.features.expenses.domain.utils.ExpenseTestTags.NO_ITEMS_IN_EXPENSE
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.LocalDate

@Destination
@Composable
fun ExpensesScreen(
    navController: NavController,
    viewModel: ExpensesViewModel = hiltViewModel(),
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
                is UiEvent.IsLoading -> {}
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

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        }
    }

    StandardScaffold(
        navController = navController,
        snackbarHostState = snackbarState,
        title = if (selectedItems.isEmpty()) EXPENSE_SCREEN_TITLE else "${selectedItems.size} Selected",
        placeholderText = EXPENSE_SEARCH_PLACEHOLDER,
        showSearchBar = showSearchBar,
        showSettings = false,
        selectionCount = selectedItems.size,
        searchText = searchText,
        showBackButton = showSearchBar,
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
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        onEditClick = {
            navController.navigate(AddEditExpenseScreenDestination(selectedItems.first()))
        },
        onDeleteClick = {
            openDialog.value = true
        },
        onDeselect = viewModel::deselectItems,
        onSelectAllClick = viewModel::selectAllItems,
        onSearchTextChanged = viewModel::searchTextChanged,
        onSearchClick = viewModel::openSearchBar,
        onBackClick = viewModel::closeSearchBar,
        onClearClick = viewModel::clearSearchText
    ) { _ ->
        Column(
            modifier = Modifier
                .padding(SpaceSmall),
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
                            onClick = {
                                dialogState.show()
                            }
                        )

                    }
                    Spacer(modifier = Modifier.height(SpaceMini))

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceMedium)
                    )
                    Spacer(modifier = Modifier.height(SpaceMini))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                        itemsIndexed(
                            items = state.data,
                            key = { index, item ->
                                item.expenseName.plus(index + item.expenseId)
                            }
                        ) { index,  expense ->
                            ExpensesData(
                                item = expense,
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

                            if (index == state.data.size - 1) {
                                Spacer(modifier = Modifier.height(SpaceLarge))
                                Spacer(modifier = Modifier.height(SpaceLarge))
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

    ListItem(
        modifier = modifier
            .testTag(EXPENSE_TAG.plus(item.expenseId))
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
            StandardOutlinedAssistChip(
                text = item.expenseDate.toPrettyDate(),
                icon = Icons.Default.CalendarMonth
            )
        }
    )
}