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

package com.niyaj.address

import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import com.niyaj.address.components.AddressData
import com.niyaj.address.destinations.AddEditAddressScreenDestination
import com.niyaj.address.destinations.AddressDetailsScreenDestination
import com.niyaj.address.destinations.AddressExportScreenDestination
import com.niyaj.address.destinations.AddressImportScreenDestination
import com.niyaj.address.destinations.AddressSettingsScreenDestination
import com.niyaj.common.tags.AddressTestTags.ADDRESS_ITEM_TAG
import com.niyaj.common.tags.AddressTestTags.ADDRESS_LIST
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NOT_AVAILABLE
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SCREEN_NOTE_TEXT
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SCREEN_TITLE
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AddressTestTags.CREATE_NEW_ADDRESS
import com.niyaj.common.tags.AddressTestTags.DELETE_ADDRESS_ITEM_MESSAGE
import com.niyaj.common.tags.AddressTestTags.DELETE_ADDRESS_ITEM_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Address
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddressPreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.niyaj.ui.utils.navigate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.ADDRESS_SCREEN)
@Composable
fun AddressScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<AddEditAddressScreenDestination, String>,
    exportRecipient: ResultRecipient<AddressExportScreenDestination, String>,
    importRecipient: ResultRecipient<AddressImportScreenDestination, String>,
    modifier: Modifier = Modifier,
    viewModel: AddressViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    val uiState by viewModel.addresses.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val selectedItems = viewModel.selectedItems.toList()
    val searchText = viewModel.searchText.value

    TrackScreenViewEvent(screenName = Screens.ADDRESS_SCREEN)

    AddressScreenContent(
        uiState = uiState,
        selectedItems = selectedItems.toImmutableList(),
        showSearchBar = showSearchBar,
        searchText = searchText,
        onClickSelectItem = viewModel::selectItem,
        onDeleteClick = viewModel::deleteItems,
        onSelectAllClick = viewModel::selectAllItems,
        onClearSearchClick = viewModel::clearSearchText,
        onSearchClick = viewModel::openSearchBar,
        onSearchTextChanged = viewModel::searchTextChanged,
        onCloseSearchBar = viewModel::closeSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = navigator::popBackStack,
        onNavigateToScreen = navigator::navigate,
        onCreateNewClick = {
            navigator.navigate(AddEditAddressScreenDestination())
        },
        onEditClick = {
            navigator.navigate(AddEditAddressScreenDestination(selectedItems.first()))
        },
        onSettingsClick = {
            navigator.navigate(AddressSettingsScreenDestination())
        },
        onNavigateToDetails = {
            navigator.navigate(AddressDetailsScreenDestination(it))
        },
        snackbarHostState = snackbarState,
        modifier = modifier,
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
internal fun AddressScreenContent(
    uiState: UiState<List<Address>>,
    selectedItems: List<Int>,
    showSearchBar: Boolean,
    searchText: String,
    onClickSelectItem: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onClearSearchClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onCloseSearchBar: () -> Unit,
    onDeselect: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    onCreateNewClick: () -> Unit,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    val showFab = uiState is UiState.Success

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

    PoposPrimaryScaffold(
        currentRoute = Screens.ADDRESS_SCREEN,
        title = if (selectedItems.isEmpty()) ADDRESS_SCREEN_TITLE else "${selectedItems.size} Selected",
        selectionCount = selectedItems.size,
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = onCreateNewClick,
                onClickScroll = {
                    coroutineScope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_ADDRESS,
            )
        },
        navActions = {
            ScaffoldNavActions(
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                searchText = searchText,
                onEditClick = onEditClick,
                onDeleteClick = {
                    openDialog.value = true
                },
                onSelectAllClick = onSelectAllClick,
                onClearClick = onClearSearchClick,
                onSearchIconClick = onSearchClick,
                onSearchTextChanged = onSearchTextChanged,
                showSearchBar = showSearchBar,
                showSettingsIcon = true,
                onSettingsClick = onSettingsClick,
                placeholderText = ADDRESS_SEARCH_PLACEHOLDER,
            )
        },
        onBackClick = if (showSearchBar) onCloseSearchBar else onBackClick,
        onNavigateToScreen = onNavigateToScreen,
        modifier = modifier,
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        showBackButton = showSearchBar,
        onDeselect = onDeselect,
        snackbarHostState = snackbarHostState,
    ) {
        Crossfade(
            targetState = uiState,
            label = "Address State",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) ADDRESS_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_ADDRESS,
                        onClick = onCreateNewClick,
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(scrollableState = lazyGridState, stateName = "address:list")

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag(ADDRESS_LIST),
                        contentPadding = PaddingValues(SpaceSmall),
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                    ) {
                        item(
                            span = { GridItemSpan(2) },
                        ) {
                            NoteCard(
                                text = ADDRESS_SCREEN_NOTE_TEXT,
                                modifier = Modifier.padding(SpaceSmall),
                            )
                        }

                        items(
                            items = state.data,
                            key = {
                                it.addressName.plus(it.addressId)
                            },
                        ) { address ->
                            AddressData(
                                item = address,
                                selected = {
                                    selectedItems.contains(it)
                                },
                                onClick = {
                                    if (selectedItems.isNotEmpty()) {
                                        onClickSelectItem(it)
                                    } else {
                                        onNavigateToDetails(it)
                                    }
                                },
                                onLongClick = onClickSelectItem,
                                modifier = Modifier
                                    .testTag(ADDRESS_ITEM_TAG.plus(address.addressId)),
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
            title = DELETE_ADDRESS_ITEM_TITLE,
            message = DELETE_ADDRESS_ITEM_MESSAGE,
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
    resultRecipient: ResultRecipient<AddEditAddressScreenDestination, String>,
    exportRecipient: ResultRecipient<AddressExportScreenDestination, String>,
    importRecipient: ResultRecipient<AddressImportScreenDestination, String>,
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
private fun AddressScreenContentPreview(
    @PreviewParameter(AddressPreviewParameter::class)
    state: UiState<List<Address>>,
) {
    PoposRoomTheme {
        AddressScreenContent(
            modifier = Modifier,
            uiState = state,
            selectedItems = emptyList(),
            showSearchBar = false,
            searchText = "",
            onClickSelectItem = {},
            onDeleteClick = {},
            onSelectAllClick = {},
            onClearSearchClick = {},
            onSearchClick = {},
            onSearchTextChanged = {},
            onCloseSearchBar = {},
            onDeselect = {},
            onBackClick = {},
            onNavigateToScreen = {},
            onCreateNewClick = {},
            onEditClick = {},
            onSettingsClick = {},
            onNavigateToDetails = {},
        )
    }
}
