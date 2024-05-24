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

package com.niyaj.employee_absent

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_NOT_AVAILABLE
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_SCREEN_TITLE
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_TAG
import com.niyaj.common.tags.AbsentScreenTags.CREATE_NEW_ABSENT
import com.niyaj.common.tags.AbsentScreenTags.DELETE_ABSENT_MESSAGE
import com.niyaj.common.tags.AbsentScreenTags.DELETE_ABSENT_TITLE
import com.niyaj.common.tags.AbsentScreenTags.NO_ITEMS_IN_ABSENT
import com.niyaj.common.utils.toDate
import com.niyaj.common.utils.toMonthAndYear
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.employee_absent.destinations.AbsentExportScreenDestination
import com.niyaj.employee_absent.destinations.AbsentImportScreenDestination
import com.niyaj.employee_absent.destinations.AbsentSettingsScreenDestination
import com.niyaj.employee_absent.destinations.AddEditAbsentScreenDestination
import com.niyaj.model.Absent
import com.niyaj.model.EmployeeWithAbsents
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardAssistChip
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardElevatedCard
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldRoute
import com.niyaj.ui.components.TextWithBorderCount
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
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.ABSENT_SCREEN)
@Composable
fun AbsentScreen(
    navigator: DestinationsNavigator,
    viewModel: AbsentViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditAbsentScreenDestination, String>,
    exportRecipient: ResultRecipient<AbsentExportScreenDestination, String>,
    importRecipient: ResultRecipient<AbsentImportScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val uiState = viewModel.absents.collectAsStateWithLifecycle().value

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

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navigator.popBackStack()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
            }

            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }

                viewModel.deselectItems()
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

    TrackScreenViewEvent(screenName = Screens.ABSENT_SCREEN)

    StandardScaffoldRoute(
        currentRoute = Screens.ABSENT_SCREEN,
        title = if (selectedItems.isEmpty()) ABSENT_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditAbsentScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_ABSENT,
                containerColor = MaterialTheme.colorScheme.surface,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = ABSENT_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navigator.navigate(AddEditAbsentScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navigator.navigate(AbsentSettingsScreenDestination)
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
        Crossfade(
            modifier = Modifier.fillMaxSize(),
            targetState = uiState,
            label = "Absent State",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) ABSENT_NOT_AVAILABLE else NO_ITEMS_IN_ABSENT,
                        buttonText = CREATE_NEW_ABSENT,
                        onClick = {
                            navigator.navigate(AddEditAbsentScreenDestination())
                        },
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(scrollableState = lazyListState, stateName = "Absent::List")

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall),
                        state = lazyListState,
                    ) {
                        items(
                            items = state.data,
                            key = { it.employee.employeeId },
                            contentType = { it },
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
                                        navigator.navigate(AddEditAbsentScreenDestination(employeeId = it))
                                    },
                                )

                                Spacer(modifier = Modifier.height(SpaceSmall))
                            }
                        }
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        StandardDialog(
            title = DELETE_ABSENT_TITLE,
            message = DELETE_ABSENT_MESSAGE,
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

@Composable
fun AbsentData(
    modifier: Modifier = Modifier,
    item: EmployeeWithAbsents,
    expanded: (Int) -> Boolean,
    onExpandChanged: (Int) -> Unit,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onChipClick: (Int) -> Unit = {},
    showTrailingIcon: Boolean = true,
) = trace("AbsentData") {
    val groupByMonth = remember(item.absents) {
        item.absents.groupBy { toMonthAndYear(it.absentDate) }
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceMini),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        StandardExpandable(
            modifier = Modifier.padding(vertical = SpaceSmall),
            expanded = expanded(item.employee.employeeId),
            onExpandChanged = {
                onExpandChanged(item.employee.employeeId)
            },
            title = {
                IconWithText(
                    text = item.employee.employeeName,
                    icon = PoposIcons.Person,
                    isTitle = true,
                )
            },
            trailing = {
                if (showTrailingIcon) {
                    StandardAssistChip(
                        modifier = Modifier.wrapContentSize(),
                        text = "Add Entry",
                        icon = PoposIcons.Add,
                        onClick = { onChipClick(item.employee.employeeId) },
                    )
                }
            },
            content = {
                EmployeeAbsentData(
                    groupedAbsents = groupByMonth,
                    doesSelected = doesSelected,
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
            },
        )
    }
}

/**
 * Employee Absent Dates
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmployeeAbsentData(
    modifier: Modifier = Modifier,
    groupedAbsents: Map<String, List<Absent>>,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
) = trace("EmployeeAbsentData") {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        groupedAbsents.forEach { grouped ->
            TextWithBorderCount(
                modifier = Modifier,
                text = grouped.key,
                leadingIcon = PoposIcons.CalenderMonth,
                count = grouped.value.size,
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMini),
                horizontalArrangement = Arrangement.Start,
            ) {
                grouped.value.forEach { item ->
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
                        },
                    ) {
                        Text(
                            text = item.absentDate.toDate,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(SpaceSmall),
                        )
                    }
                }
            }
        }
    }
}