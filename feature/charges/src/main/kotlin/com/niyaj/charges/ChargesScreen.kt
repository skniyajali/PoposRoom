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

package com.niyaj.charges

import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.lazy.grid.LazyGridState
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
import com.niyaj.charges.components.ChargesList
import com.niyaj.charges.destinations.AddEditChargesScreenDestination
import com.niyaj.charges.destinations.ChargesExportScreenDestination
import com.niyaj.charges.destinations.ChargesImportScreenDestination
import com.niyaj.charges.destinations.ChargesSettingsScreenDestination
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NOT_AVAILABLE
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SCREEN_TITLE
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.ChargesTestTags.CREATE_NEW_CHARGES
import com.niyaj.common.tags.ChargesTestTags.DELETE_CHARGES_MESSAGE
import com.niyaj.common.tags.ChargesTestTags.DELETE_CHARGES_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.model.Charges
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ChargesPreviewParameterProvider
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
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.CHARGES_SCREEN)
@Composable
fun ChargesScreen(
    navigator: DestinationsNavigator,
    viewModel: ChargesViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditChargesScreenDestination, String>,
    exportRecipient: ResultRecipient<ChargesExportScreenDestination, String>,
    importRecipient: ResultRecipient<ChargesImportScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    val state by viewModel.charges.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val searchText = viewModel.searchText.value
    val selectedItems = viewModel.selectedItems.toList()

    ChargesScreenContent(
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
            navigator.navigate(AddEditChargesScreenDestination())
        },
        onClickEdit = {
            navigator.navigate(AddEditChargesScreenDestination(it))
        },
        onClickSettings = {
            navigator.navigate(ChargesSettingsScreenDestination())
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

@VisibleForTesting
@Composable
internal fun ChargesScreenContent(
    modifier: Modifier = Modifier,
    uiState: UiState<List<Charges>>,
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
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    TrackScreenViewEvent(screenName = Screens.CHARGES_SCREEN)

    BackHandler {
        if (showSearchBar) {
            onCloseSearchBar()
        } else if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else {
            onClickBack()
        }
    }

    val showFab = uiState is UiState.Success
    val openDialog = remember { mutableStateOf(false) }

    PoposPrimaryScaffold(
        modifier = modifier,
        currentRoute = Screens.CHARGES_SCREEN,
        title = if (selectedItems.isEmpty()) CHARGES_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = onClickCreateNew,
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_CHARGES,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = CHARGES_SEARCH_PLACEHOLDER,
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
        snackbarHostState = snackbarState,
        onNavigateToScreen = onNavigateToScreen,
    ) {
        Crossfade(
            targetState = uiState,
            label = "Charges::UiState",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) CHARGES_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_CHARGES,
                        onClick = onClickCreateNew,
                    )
                }

                is UiState.Success -> {
                    ChargesList(
                        chargesList = state.data.toImmutableList(),
                        doesSelected = selectedItems::contains,
                        padding = SpaceMedium,
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

    AnimatedVisibility(
        visible = openDialog.value,
    ) {
        StandardDialog(
            title = DELETE_CHARGES_TITLE,
            message = DELETE_CHARGES_MESSAGE,
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
    resultRecipient: ResultRecipient<AddEditChargesScreenDestination, String>,
    exportRecipient: ResultRecipient<ChargesExportScreenDestination, String>,
    importRecipient: ResultRecipient<ChargesImportScreenDestination, String>,
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
private fun ChargesScreenContentPreview(
    @PreviewParameter(ChargesPreviewParameterProvider::class)
    uiState: UiState<List<Charges>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ChargesScreenContent(
            modifier = modifier,
            uiState = uiState,
            selectedItems = listOf(),
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
