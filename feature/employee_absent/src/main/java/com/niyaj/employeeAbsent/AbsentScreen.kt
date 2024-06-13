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

package com.niyaj.employeeAbsent

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_NOT_AVAILABLE
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_SCREEN_TITLE
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AbsentScreenTags.CREATE_NEW_ABSENT
import com.niyaj.common.tags.AbsentScreenTags.DELETE_ABSENT_MESSAGE
import com.niyaj.common.tags.AbsentScreenTags.DELETE_ABSENT_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.employeeAbsent.components.AbsentEmployeeList
import com.niyaj.employeeAbsent.destinations.AbsentExportScreenDestination
import com.niyaj.employeeAbsent.destinations.AbsentImportScreenDestination
import com.niyaj.employeeAbsent.destinations.AbsentSettingsScreenDestination
import com.niyaj.employeeAbsent.destinations.AddEditAbsentScreenDestination
import com.niyaj.model.EmployeeWithAbsents
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AbsentPreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.CoroutineScope
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

    val uiState by viewModel.absents.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    AbsentScreenContent(
        modifier = Modifier,
        uiState = uiState,
        selectedItems = viewModel.selectedItems.toList(),
        selectedEmployees = viewModel.selectedEmployee.toList(),
        showSearchBar = showSearchBar,
        searchText = viewModel.searchText.value,
        onClickSearchIcon = viewModel::openSearchBar,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClickClear = viewModel::clearSearchText,
        onCloseSearchBar = viewModel::closeSearchBar,
        onClickSelectItem = viewModel::selectItem,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onClickDelete = viewModel::deleteItems,
        onSelectEmployee = viewModel::selectEmployee,
        onClickBack = navigator::popBackStack,
        onNavigateToScreen = navigator::navigate,
        onClickCreateNew = {
            navigator.navigate(AddEditAbsentScreenDestination())
        },
        onClickEdit = {
            navigator.navigate(AddEditAbsentScreenDestination(it))
        },
        onClickSettings = {
            navigator.navigate(AbsentSettingsScreenDestination())
        },
        onAbsentAddClick = {
            navigator.navigate(AddEditAbsentScreenDestination(employeeId = it))
        },
        snackbarState = snackbarState,
    )

    HandleResultRecipients(
        resultRecipient = resultRecipient,
        exportRecipient = exportRecipient,
        importRecipient = importRecipient,
        event = event,
        onDeselectItems = viewModel::deselectItems,
        coroutineScope = scope,
        snackbarHostState = snackbarState,
    )
}

@androidx.annotation.VisibleForTesting
@Composable
internal fun AbsentScreenContent(
    modifier: Modifier = Modifier,
    uiState: UiState<List<EmployeeWithAbsents>>,
    selectedItems: List<Int>,
    selectedEmployees: List<Int>,
    showSearchBar: Boolean,
    searchText: String,
    onClickSearchIcon: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClickClear: () -> Unit,
    onCloseSearchBar: () -> Unit,
    onClickSelectItem: (Int) -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onClickDelete: () -> Unit,
    onClickBack: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    onClickCreateNew: () -> Unit,
    onClickEdit: (Int) -> Unit,
    onClickSettings: () -> Unit,
    onSelectEmployee: (Int) -> Unit,
    onAbsentAddClick: (Int) -> Unit,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = Screens.ABSENT_SCREEN)

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else if (showSearchBar) {
            onCloseSearchBar()
        } else {
            onClickBack()
        }
    }

    val showFab = uiState is UiState.Success
    val openDialog = remember { mutableStateOf(false) }

    PoposPrimaryScaffold(
        modifier = modifier,
        currentRoute = Screens.ABSENT_SCREEN,
        title = if (selectedItems.isEmpty()) ABSENT_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = onClickCreateNew,
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_ABSENT,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = ABSENT_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchBar = showSearchBar,
                showSearchIcon = showFab,
                searchText = searchText,
                onEditClick = {
                    onClickEdit(selectedItems.first())
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = onClickSettings,
                onSelectAllClick = onClickSelectAll,
                onClearClick = onClickClear,
                onSearchIconClick = onClickSearchIcon,
                onSearchTextChanged = onSearchTextChanged,
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = onClickDeselect,
        onBackClick = if (showSearchBar) onCloseSearchBar else onClickBack,
        snackbarHostState = snackbarState,
        onNavigateToScreen = onNavigateToScreen,
    ) {
        Crossfade(
            targetState = uiState,
            label = "AbsentList::UiState",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) ABSENT_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_ABSENT,
                        onClick = onClickCreateNew,
                    )
                }

                is UiState.Success -> {
                    AbsentEmployeeList(
                        modifier = Modifier,
                        items = state.data,
                        showTrailingIcon = true,
                        expanded = selectedEmployees::contains,
                        onExpandChanged = onSelectEmployee,
                        doesSelected = selectedItems::contains,
                        onClick = {
                            if (selectedItems.isNotEmpty()) {
                                onClickSelectItem(it)
                            }
                        },
                        onLongClick = onClickSelectItem,
                        onChipClick = onAbsentAddClick,
                        lazyListState = lazyListState,
                    )
                }
            }
        }
    }

    AnimatedVisibility(
        visible = openDialog.value,
    ) {
        StandardDialog(
            title = DELETE_ABSENT_TITLE,
            message = DELETE_ABSENT_MESSAGE,
            onConfirm = {
                openDialog.value = false
                onClickDelete()
            },
            onDismiss = {
                openDialog.value = false
                onClickDeselect()
            },
        )
    }
}

@Composable
private fun HandleResultRecipients(
    resultRecipient: ResultRecipient<AddEditAbsentScreenDestination, String>,
    exportRecipient: ResultRecipient<AbsentExportScreenDestination, String>,
    importRecipient: ResultRecipient<AbsentImportScreenDestination, String>,
    event: UiEvent?,
    onDeselectItems: () -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                onDeselectItems()
            }

            is NavResult.Value -> {
                onDeselectItems()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    exportRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(data.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(data.successMessage)
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AbsentScreenPreview(
    @PreviewParameter(AbsentPreviewParameter::class)
    uiState: UiState<List<EmployeeWithAbsents>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AbsentScreenContent(
            modifier = modifier,
            uiState = uiState,
            selectedItems = listOf(),
            selectedEmployees = listOf(1, 2, 3),
            showSearchBar = false,
            searchText = "",
            onClickSearchIcon = {},
            onSearchTextChanged = {},
            onClickClear = {},
            onCloseSearchBar = {},
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onClickDelete = {},
            onClickBack = {},
            onNavigateToScreen = {},
            onClickCreateNew = {},
            onClickEdit = {},
            onClickSettings = {},
            onAbsentAddClick = {},
            onSelectEmployee = {},
        )
    }
}
