package com.niyaj.poposroom.features.expenses.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.poposroom.features.common.components.CircularBox
import com.niyaj.poposroom.features.common.components.StandardFAB
import com.niyaj.poposroom.features.common.components.StandardOutlinedAssistChip
import com.niyaj.poposroom.features.common.components.StandardScaffold
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.ui.theme.SpaceLarge
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.isScrolled
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

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

//    val data = viewModel.data.collectAsLazyPagingItems()

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

        LazyColumn(
            modifier = Modifier
                .padding(SpaceSmall),
            state = lazyListState
        ) {
//            item {
//                ElevatedCard(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(250.dp)
//                        .padding(SpaceSmall)
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(SpaceSmall),
//                        horizontalAlignment = Alignment.Start
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            CircularBox(
//                                icon = Icons.Default.TrendingUp,
//                                doesSelected = false,
//                            )
//
//                            Text(
//                                text = "Total Expenses",
//                                style = MaterialTheme.typography.titleMedium
//                            )
//                        }
//                    }
//                }
//            }

            when(state) {
                is UiState.Empty -> Error(EXPENSE_NOT_AVAIlABLE)
                is UiState.Loading -> Loading()
                is UiState.Success -> {
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

//            when (val prepend = data.loadState.prepend) {
//                is LoadState.Loading -> Loading()
//                is LoadState.Error -> Error(prepend.error.message)
//                else -> {}
//            }
//
//            items(
//                count = data.itemCount,
//                key = data.itemKey(),
//                contentType = data.itemContentType()
//            ) { index ->
//                val item = data[index]
//                item?.let { expense ->
//                    ExpensesData(
//                        item = expense,
//                        doesSelected = {
//                            selectedItems.contains(it)
//                        },
//                        onClick = {
//                            if (selectedItems.isNotEmpty()) {
//                                viewModel.selectItem(it)
//                            }
//                        },
//                        onLongClick = viewModel::selectItem
//                    )
//                }
//            }
//
//            when (val refresh = data.loadState.refresh) {
//                is LoadState.Loading -> Loading()
//                is LoadState.Error -> Error(refresh.error.message)
//                else -> {}
//            }
//
//            when (val append = data.loadState.append) {
//                is LoadState.Loading -> Loading()
//                is LoadState.Error -> Error(append.error.message)
//                else -> {}
//            }
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

private fun LazyListScope.Loading() {
    item {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    }
}


private fun LazyListScope.Error(
    message: String? = null,
) {
    message?.let {
        item {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall)
            )
        }
    }
}