package com.niyaj.poposroom.features.employee_absent.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.niyaj.poposroom.features.common.components.ItemNotAvailable
import com.niyaj.poposroom.features.common.components.LoadingIndicator
import com.niyaj.poposroom.features.common.components.StandardAssistChip
import com.niyaj.poposroom.features.common.components.StandardElevatedCard
import com.niyaj.poposroom.features.common.components.StandardExpandable
import com.niyaj.poposroom.features.common.components.StandardFAB
import com.niyaj.poposroom.features.common.components.StandardScaffold
import com.niyaj.poposroom.features.common.components.TextWithBorderCount
import com.niyaj.poposroom.features.common.components.TextWithIcon
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.isScrolled
import com.niyaj.poposroom.features.common.utils.toDate
import com.niyaj.poposroom.features.common.utils.toMonthAndYear
import com.niyaj.poposroom.features.destinations.AddEditAbsentScreenDestination
import com.niyaj.poposroom.features.employee_absent.domain.model.Absent
import com.niyaj.poposroom.features.employee_absent.domain.model.EmployeeWithAbsent
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_NOT_AVAIlABLE
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_SCREEN_TITLE
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_SEARCH_PLACEHOLDER
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_TAG
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.CREATE_NEW_ABSENT
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.DELETE_ABSENT_MESSAGE
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.DELETE_ABSENT_TITLE
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.NO_ITEMS_IN_ABSENT
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination
@Composable
fun AbsentScreen(
    navController: NavController,
    viewModel: AbsentViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditAbsentScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.absents.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyListState = rememberLazyListState()

    val showFab = viewModel.totalItems.isNotEmpty()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val selectedEmployee = viewModel.selectedEmployee.collectAsStateWithLifecycle().value

    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when(data) {
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

    resultRecipient.onNavResult {result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }

                viewModel.deselectItems()
            }
        }
    }

    StandardScaffold(
        navController = navController,
        snackbarHostState = snackbarState,
        title = if (selectedItems.isEmpty()) ABSENT_SCREEN_TITLE else "${selectedItems.size} Selected",
        placeholderText = ABSENT_SEARCH_PLACEHOLDER,
        showSearchBar = showSearchBar,
        selectionCount = selectedItems.size,
        searchText = searchText,
        showBackButton = showSearchBar,
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_ABSENT,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                containerColor = MaterialTheme.colorScheme.surface,
                onFabClick = {
                    navController.navigate(AddEditAbsentScreenDestination())
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
            navController.navigate(AddEditAbsentScreenDestination(selectedItems.first()))
        },
        onDeleteClick = {
            openDialog.value = true
        },
        onDeselect = viewModel::deselectItems,
        onSelectAllClick = viewModel::selectAllItems,
        onSearchTextChanged = viewModel::searchTextChanged,
        onSearchClick = viewModel::openSearchBar,
        onBackClick = viewModel::closeSearchBar,
        onClearClick = viewModel::clearSearchText,
    ) { _ ->
        when (state) {
            is UiState.Loading -> LoadingIndicator()
            is UiState.Empty -> {
                ItemNotAvailable(
                    text = if (searchText.isEmpty()) ABSENT_NOT_AVAIlABLE else NO_ITEMS_IN_ABSENT,
                    buttonText = CREATE_NEW_ABSENT,
                    onClick = {
                        navController.navigate(AddEditAbsentScreenDestination())
                    }
                )
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(SpaceSmall),
                    state = lazyListState
                ) {
                    items(
                        items = state.data,
                        key = { it.employee.employeeId }
                    ) { item ->
                        if (item.absents.isNotEmpty()) {
                            AbsentData(
                                item = item,
                                expanded = {
                                    selectedEmployee == it
                                },
                                doesSelected = {
                                    selectedItems.contains(it)
                                },
                                onClick = {
                                    if (selectedItems.isNotEmpty()) {
                                        viewModel.selectItem(it)
                                    }
                                },
                                onExpandChanged = viewModel::selectEmployee,
                                onLongClick = viewModel::selectItem,
                                onChipClick = {
                                    navController.navigate(AddEditAbsentScreenDestination(employeeId = it))
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                viewModel.deselectItems()
            },
            title = {
                Text(text = DELETE_ABSENT_TITLE)
            },
            text = {
                Text(
                    text = DELETE_ABSENT_MESSAGE
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
fun AbsentData(
    modifier: Modifier = Modifier,
    item: EmployeeWithAbsent,
    expanded: (Int) ->  Boolean,
    onExpandChanged: (Int) -> Unit,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onChipClick: (Int) -> Unit = {},
) {
    val groupByMonth = item.absents.groupBy { toMonthAndYear(it.absentDate) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceMini),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        StandardExpandable(
            modifier = Modifier.padding(vertical = SpaceSmall),
            expanded = expanded(item.employee.employeeId),
            onExpandChanged = {
                onExpandChanged(item.employee.employeeId)
            },
            title = {
                TextWithIcon(
                    text = item.employee.employeeName,
                    icon = Icons.Default.Person,
                    isTitle = true
                )
            },
            trailing = {
                StandardAssistChip(
                    modifier = Modifier.wrapContentSize(),
                    text = "Add Entry",
                    icon = Icons.Default.Add,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = { onChipClick(item.employee.employeeId) },
                )
            },
            content = {
                EmployeeAbsentData(
                    groupedAbsents = groupByMonth,
                    doesSelected = doesSelected,
                    onClick = onClick,
                    onLongClick = onLongClick
                )
            }
        )
    }
}

/**
 * Employee Absent Dates
 */
@Composable
fun EmployeeAbsentData(
    modifier: Modifier = Modifier,
    groupedAbsents:  Map<String, List<Absent>>,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        groupedAbsents.forEach { grouped ->
            TextWithBorderCount(
                modifier = Modifier,
                text = grouped.key,
                leadingIcon = Icons.Default.CalendarMonth,
                count = grouped.value.size,
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMini),
                crossAxisSpacing = SpaceMini,
            ) {
                grouped.value.forEach{  item ->
                    StandardElevatedCard(
                        modifier = modifier,
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        testTag = ABSENT_TAG.plus(item.absentId),
                        doesSelected = doesSelected(item.absentId),
                        onClick = {
                            onClick(item.absentId)
                        },
                        onLongClick = {
                            onLongClick(item.absentId)
                        }
                    ) {
                        Text(
                            text = item.absentDate.toDate,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(SpaceSmall)
                        )
                    }
                }
            }
        }
    }
}