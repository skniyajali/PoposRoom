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

package com.niyaj.category

import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.category.components.CategoryData
import com.niyaj.category.destinations.AddEditCategoryScreenDestination
import com.niyaj.category.destinations.CategorySettingsScreenDestination
import com.niyaj.category.destinations.ExportCategoryScreenDestination
import com.niyaj.category.destinations.ImportCategoryScreenDestination
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NOT_AVAILABLE
import com.niyaj.common.tags.CategoryConstants.CATEGORY_SCREEN_TITLE
import com.niyaj.common.tags.CategoryConstants.CATEGORY_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CategoryConstants.CREATE_NEW_CATEGORY
import com.niyaj.common.tags.CategoryConstants.DELETE_CATEGORY_ITEM_MESSAGE
import com.niyaj.common.tags.CategoryConstants.DELETE_CATEGORY_ITEM_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.model.Category
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CategoryPreviewParameter
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
@Destination(route = Screens.CATEGORY_SCREEN)
@Composable
fun CategoryScreen(
    navigator: DestinationsNavigator,
    viewModel: CategoryViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCategoryScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportCategoryScreenDestination, String>,
    importRecipient: ResultRecipient<ImportCategoryScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    val state by viewModel.addOnItems.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val selectedItems = viewModel.selectedItems.toList()
    val searchText = viewModel.searchText.value

    CategoryScreenContent(
        modifier = Modifier,
        uiState = state,
        selectedItems = selectedItems,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onClickSearchIcon = viewModel::openSearchBar,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClickClear = viewModel::clearSearchText,
        onCloseSearchBar = viewModel::closeSearchBar,
        onClickSelectItem = viewModel::selectItem,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onClickDelete = viewModel::deleteItems,
        onClickBack = navigator::popBackStack,
        onNavigateToScreen = navigator::navigate,
        onClickCreateNew = {
            navigator.navigate(AddEditCategoryScreenDestination())
        },
        onClickEdit = {
            navigator.navigate(AddEditCategoryScreenDestination(it))
        },
        onClickSettings = {
            navigator.navigate(CategorySettingsScreenDestination)
        },
        snackbarHostState = snackbarState,
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

@Composable
private fun HandleResultRecipients(
    resultRecipient: ResultRecipient<AddEditCategoryScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportCategoryScreenDestination, String>,
    importRecipient: ResultRecipient<ImportCategoryScreenDestination, String>,
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

@VisibleForTesting
@Composable
internal fun CategoryScreenContent(
    modifier: Modifier = Modifier,
    uiState: UiState<List<Category>>,
    selectedItems: List<Int>,
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
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    TrackScreenViewEvent(screenName = Screens.CATEGORY_SCREEN)

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
        currentRoute = Screens.CATEGORY_SCREEN,
        title = if (selectedItems.isEmpty()) CATEGORY_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = onClickCreateNew,
                onClickScroll = {
                    coroutineScope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_CATEGORY,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = CATEGORY_SEARCH_PLACEHOLDER,
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
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = onClickDeselect,
        onBackClick = if (showSearchBar) onCloseSearchBar else onClickBack,
        snackbarHostState = snackbarHostState,
        onNavigateToScreen = onNavigateToScreen,
    ) { _ ->
        Crossfade(
            targetState = uiState,
            label = "Category::UiState",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) CATEGORY_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_CATEGORY,
                        onClick = onClickCreateNew,
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(scrollableState = lazyGridState, stateName = "Category::List")

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(SpaceMedium),
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                        horizontalArrangement = Arrangement.spacedBy(SpaceMedium),
                        verticalArrangement = Arrangement.spacedBy(SpaceMedium),
                    ) {
                        items(
                            items = state.data,
                            key = { it.categoryId },
                        ) { item: Category ->
                            CategoryData(
                                item = item,
                                doesSelected = {
                                    selectedItems.contains(it)
                                },
                                onClick = {
                                    if (selectedItems.isNotEmpty()) {
                                        onClickSelectItem(it)
                                    }
                                },
                                onLongClick = onClickSelectItem,
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
            title = DELETE_CATEGORY_ITEM_TITLE,
            message = DELETE_CATEGORY_ITEM_MESSAGE,
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

@DevicePreviews
@Composable
private fun CategoryScreenContentPreview(
    @PreviewParameter(CategoryPreviewParameter::class)
    uiState: UiState<List<Category>>,
    modifier: Modifier = Modifier,
    selectedItems: List<Int> = if (uiState is UiState.Success) listOf(2, 5, 8) else listOf(),
) {
    PoposRoomTheme {
        CategoryScreenContent(
            modifier = modifier,
            uiState = uiState,
            selectedItems = selectedItems,
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
        )
    }
}
