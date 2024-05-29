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

package com.niyaj.addonitem

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.addonitem.components.AddOnItemData
import com.niyaj.addonitem.destinations.AddEditAddOnItemScreenDestination
import com.niyaj.addonitem.destinations.AddOnExportScreenDestination
import com.niyaj.addonitem.destinations.AddOnImportScreenDestination
import com.niyaj.addonitem.destinations.AddOnSettingsScreenDestination
import com.niyaj.common.tags.AddOnTestTags.ADDON_NOT_AVAILABLE
import com.niyaj.common.tags.AddOnTestTags.ADDON_SCREEN_TITLE
import com.niyaj.common.tags.AddOnTestTags.ADDON_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AddOnTestTags.CREATE_NEW_ADD_ON
import com.niyaj.common.tags.AddOnTestTags.DELETE_ADD_ON_ITEM_MESSAGE
import com.niyaj.common.tags.AddOnTestTags.DELETE_ADD_ON_ITEM_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddOnItemPreviewParameterProvider
import com.niyaj.ui.utils.DevicePreviews
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.ADD_ON_ITEM_SCREEN)
@Composable
fun AddOnItemScreen(
    navigator: DestinationsNavigator,
    viewModel: AddOnViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditAddOnItemScreenDestination, String>,
    exportRecipient: ResultRecipient<AddOnExportScreenDestination, String>,
    importRecipient: ResultRecipient<AddOnImportScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    val state by viewModel.addOnItems.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val selectedItems = viewModel.selectedItems.toList()

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

    AddOnItemScreenContent(
        uiState = state,
        selectedItems = selectedItems,
        showSearchBar = showSearchBar,
        searchText = viewModel.searchText.value,
        onItemClick = viewModel::selectItem,
        onCreateNewClick = { navigator.navigate(AddEditAddOnItemScreenDestination()) },
        onEditClick = { navigator.navigate(AddEditAddOnItemScreenDestination(selectedItems.first())) },
        onDeleteClick = viewModel::deleteItems,
        onSettingsClick = { navigator.navigate(AddOnSettingsScreenDestination) },
        onSelectAllClick = viewModel::selectAllItems,
        onClearSearchClick = viewModel::clearSearchText,
        onSearchClick = viewModel::openSearchBar,
        onSearchTextChanged = viewModel::searchTextChanged,
        onCloseSearchBar = viewModel::closeSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = navigator::popBackStack,
        onNavigateToScreen = navigator::navigate,
        snackbarHostState = snackbarState,
    )

    HandleResultRecipients(
        resultRecipient = resultRecipient,
        exportRecipient = exportRecipient,
        importRecipient = importRecipient,
        onDeselectItems = viewModel::deselectItems,
        snackbarHostState = snackbarState,
    )
}

@Composable
private fun AddOnItemScreenContent(
    uiState: UiState<List<AddOnItem>>,
    selectedItems: List<Int>,
    showSearchBar: Boolean,
    searchText: String,
    onItemClick: (Int) -> Unit,
    onCreateNewClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onClearSearchClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onCloseSearchBar: () -> Unit,
    onBackClick: () -> Unit,
    onDeselect: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    val openDialog = remember { mutableStateOf(false) }

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onDeselect()
        } else if (showSearchBar) {
            onCloseSearchBar()
        } else {
            onBackClick()
        }
    }

    TrackScreenViewEvent(screenName = Screens.ADD_ON_ITEM_SCREEN)

    PoposPrimaryScaffold(
        currentRoute = Screens.ADD_ON_ITEM_SCREEN,
        title = if (selectedItems.isEmpty()) ADDON_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (uiState !is UiState.Empty && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = onCreateNewClick,
                onClickScroll = { coroutineScope.launch { lazyGridState.animateScrollToItem(0) } },
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_ADD_ON,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = ADDON_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = uiState is UiState.Success,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onSettingsClick = onSettingsClick,
                onSelectAllClick = onSelectAllClick,
                onClearClick = onClearSearchClick,
                onSearchClick = onSearchClick,
                onSearchTextChanged = onSearchTextChanged,
            )
        },
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = onDeselect,
        onBackClick = onBackClick,
        snackbarHostState = snackbarHostState,
        onNavigateToScreen = onNavigateToScreen,
    ) {
        Crossfade(
            targetState = uiState,
            label = "AddOn State",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator(contentDesc = "AddOn:LoadingIndicator")

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) ADDON_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_ADD_ON,
                        onClick = onCreateNewClick,
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(scrollableState = lazyGridState, stateName = "addons:list")

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall)
                            .testTag("addon:list"),
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                    ) {
                        items(
                            items = state.data,
                            key = { it.itemId },
                        ) { item: AddOnItem ->
                            AddOnItemData(
                                item = item,
                                doesSelected = selectedItems::contains,
                                onClick = {
                                    if (selectedItems.isNotEmpty()) {
                                        onItemClick(it)
                                    }
                                },
                                onLongClick = onItemClick,
                            )
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = openDialog.value,
    ) {
        StandardDialog(
            title = DELETE_ADD_ON_ITEM_TITLE,
            message = DELETE_ADD_ON_ITEM_MESSAGE,
            onConfirm = {
                openDialog.value = false
                onDeleteClick()
            },
            onDismiss = {
                openDialog.value = false
                onDeselect()
            },
        )
    }
}

@Composable
private fun HandleResultRecipients(
    resultRecipient: ResultRecipient<AddEditAddOnItemScreenDestination, String>,
    exportRecipient: ResultRecipient<AddOnExportScreenDestination, String>,
    importRecipient: ResultRecipient<AddOnImportScreenDestination, String>,
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
}

@DevicePreviews
@Composable
fun AddOnItemScreenLoading() {
    AddOnItemScreenContent(
        uiState = UiState.Loading,
        selectedItems = listOf(),
        showSearchBar = false,
        searchText = "",
        onItemClick = {},
        onCreateNewClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onSettingsClick = {},
        onSelectAllClick = {},
        onClearSearchClick = {},
        onSearchClick = {},
        onSearchTextChanged = {},
        onCloseSearchBar = {},
        onBackClick = {},
        onDeselect = {},
        onNavigateToScreen = {},
    )
}

@DevicePreviews
@Composable
fun AddOnItemScreenEmpty() {
    AddOnItemScreenContent(
        uiState = UiState.Empty,
        selectedItems = listOf(),
        showSearchBar = false,
        searchText = "",
        onItemClick = {},
        onCreateNewClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onSettingsClick = {},
        onSelectAllClick = {},
        onClearSearchClick = {},
        onSearchClick = {},
        onSearchTextChanged = {},
        onCloseSearchBar = {},
        onBackClick = {},
        onDeselect = {},
        onNavigateToScreen = {},
    )
}

@DevicePreviews
@Composable
fun AddOnItemScreenPopulated(
    @PreviewParameter(AddOnItemPreviewParameterProvider::class)
    items: List<AddOnItem>,
) {
    AddOnItemScreenContent(
        uiState = UiState.Success(items),
        selectedItems = listOf(),
        showSearchBar = false,
        searchText = "",
        onItemClick = {},
        onCreateNewClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onSettingsClick = {},
        onSelectAllClick = {},
        onClearSearchClick = {},
        onSearchClick = {},
        onSearchTextChanged = {},
        onCloseSearchBar = {},
        onBackClick = {},
        onDeselect = {},
        onNavigateToScreen = {},
    )
}
