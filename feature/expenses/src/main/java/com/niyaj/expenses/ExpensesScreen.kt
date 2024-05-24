/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.expenses

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.ExpenseTestTags.CREATE_NEW_EXPENSE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_EXPENSE_MESSAGE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_EXPENSE_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NOT_AVAIlABLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SCREEN_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.ExpenseTestTags.NO_ITEMS_IN_EXPENSE
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.expenses.components.ExpensesData
import com.niyaj.expenses.components.GroupedExpensesData
import com.niyaj.expenses.components.TotalExpenses
import com.niyaj.expenses.destinations.AddEditExpenseScreenDestination
import com.niyaj.expenses.destinations.ExpensesExportScreenDestination
import com.niyaj.expenses.destinations.ExpensesImportScreenDestination
import com.niyaj.expenses.destinations.ExpensesSettingsScreenDestination
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldRoute
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.LocalDate

@RootNavGraph(start = true)
@Destination(route = Screens.EXPENSES_SCREEN)
@Composable
fun ExpensesScreen(
    navigator: DestinationsNavigator,
    viewModel: ExpensesViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditExpenseScreenDestination, String>,
    exportRecipient: ResultRecipient<ExpensesExportScreenDestination, String>,
    importRecipient: ResultRecipient<ExpensesImportScreenDestination, String>,
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
        when (result) {
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

    exportRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
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
        } else {
            navigator.popBackStack()
        }
    }

    TrackScreenViewEvent(screenName = Screens.EXPENSES_SCREEN)

    StandardScaffoldRoute(
        currentRoute = Screens.EXPENSES_SCREEN,
        title = if (selectedItems.isEmpty()) EXPENSE_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditExpenseScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_EXPENSE,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = EXPENSE_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navigator.navigate(AddEditExpenseScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navigator.navigate(ExpensesSettingsScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = viewModel::closeSearchBar,
        snackbarHostState = snackbarState,
        onNavigateToScreen = navigator::navigate,
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            TotalExpenses(
                totalAmount = totalAmount,
                totalItem = totalItem,
                selectedDate = selectedDate,
                onDateClick = {
                    dialogState.show()
                },
            )

            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) EXPENSE_NOT_AVAIlABLE else NO_ITEMS_IN_EXPENSE,
                        buttonText = CREATE_NEW_EXPENSE,
                        onClick = {
                            navigator.navigate(AddEditExpenseScreenDestination())
                        },
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(scrollableState = lazyListState, stateName = "Expenses::List")
                    val grouped = remember(state.data) {
                        state.data.groupBy { it.expenseName }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = lazyListState,
                    ) {
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
                                        onLongClick = viewModel::selectItem,
                                    )
                                }
                            } else {
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
                                        onLongClick = viewModel::selectItem,
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
        },
    ) {
        datepicker(
            allowedDateValidator = { date ->
                date <= LocalDate.now()
            },
        ) { date ->
            viewModel.selectDate(date.toMilliSecond)
        }
    }

    if (openDialog.value) {
        StandardDialog(
            title = DELETE_EXPENSE_TITLE,
            message = DELETE_EXPENSE_MESSAGE,
            onConfirm = {
                openDialog.value = false
                viewModel.deleteItems()
            },
            onDismiss = {
                openDialog.value = false
                viewModel.deselectItems()
            },
        )
    }
}