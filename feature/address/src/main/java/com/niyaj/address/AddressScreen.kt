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
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.address.destinations.AddEditAddressScreenDestination
import com.niyaj.address.destinations.AddressDetailsScreenDestination
import com.niyaj.address.destinations.AddressExportScreenDestination
import com.niyaj.address.destinations.AddressImportScreenDestination
import com.niyaj.address.destinations.AddressSettingsScreenDestination
import com.niyaj.common.tags.AddressTestTags.ADDRESS_ITEM_TAG
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NOT_AVAILABLE
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SCREEN_NOTE_TEXT
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SCREEN_TITLE
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AddressTestTags.CREATE_NEW_ADDRESS
import com.niyaj.common.tags.AddressTestTags.DELETE_ADDRESS_ITEM_MESSAGE
import com.niyaj.common.tags.AddressTestTags.DELETE_ADDRESS_ITEM_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Address
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
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
@Destination(route = Screens.ADDRESS_SCREEN)
@Composable
fun AddressScreen(
    navigator: DestinationsNavigator,
    viewModel: AddressViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditAddressScreenDestination, String>,
    exportRecipient: ResultRecipient<AddressExportScreenDestination, String>,
    importRecipient: ResultRecipient<AddressImportScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val snackbarState = remember { SnackbarHostState() }

    val uiState = viewModel.addresses.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()
    val showFab = viewModel.totalItems.isNotEmpty()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

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

    TrackScreenViewEvent(screenName = Screens.ADDRESS_SCREEN)

    PoposPrimaryScaffold(
        currentRoute = Screens.ADDRESS_SCREEN,
        title = if (selectedItems.isEmpty()) ADDRESS_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditAddressScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_ADDRESS,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = ADDRESS_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navigator.navigate(AddEditAddressScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navigator.navigate(AddressSettingsScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
            )
        },
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = viewModel::closeSearchBar,
        snackbarHostState = snackbarState,
        onNavigateToScreen = navigator::navigate,
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
                        onClick = {
                            navigator.navigate(AddEditAddressScreenDestination())
                        },
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(scrollableState = lazyGridState, stateName = "address:list")

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall),
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                    ) {
                        item(
                            span = { GridItemSpan(2) },
                        ) {
                            NoteCard(
                                modifier = Modifier.padding(SpaceSmall),
                                text = ADDRESS_SCREEN_NOTE_TEXT,
                            )
                        }

                        items(
                            items = state.data,
                            key = {
                                it.addressName.plus(it.addressId)
                            },
                        ) { address ->
                            AddressData(
                                modifier = Modifier.testTag(ADDRESS_ITEM_TAG.plus(address.addressId)),
                                item = address,
                                doesSelected = {
                                    selectedItems.contains(it)
                                },
                                onClick = {
                                    if (selectedItems.isNotEmpty()) {
                                        viewModel.selectItem(it)
                                    } else {
                                        navigator.navigate(AddressDetailsScreenDestination(it))
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

    if (openDialog.value) {
        StandardDialog(
            title = DELETE_ADDRESS_ITEM_TITLE,
            message = DELETE_ADDRESS_ITEM_MESSAGE,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddressData(
    modifier: Modifier = Modifier,
    item: Address,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) = trace("Address::Data") {
    val borderStroke = if (doesSelected(item.addressId)) border else null

    ElevatedCard(
        modifier = modifier
            .padding(SpaceSmall)
            .then(
                borderStroke?.let {
                    Modifier.border(it, CardDefaults.elevatedShape)
                } ?: Modifier,
            )
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    onClick(item.addressId)
                },
                onLongClick = {
                    onLongClick(item.addressId)
                },
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = item.addressName)
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(text = item.shortName)
            }

            CircularBox(
                icon = PoposIcons.Address,
                doesSelected = doesSelected(item.addressId),
                text = item.addressName,
            )
        }
    }
}
