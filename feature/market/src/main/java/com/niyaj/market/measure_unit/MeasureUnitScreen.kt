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

package com.niyaj.market.measure_unit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import com.niyaj.common.tags.MeasureUnitTestTags.CREATE_NEW_UNIT
import com.niyaj.common.tags.MeasureUnitTestTags.DELETE_ITEM_MESSAGE
import com.niyaj.common.tags.MeasureUnitTestTags.DELETE_ITEM_TITLE
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NOT_AVAILABLE
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_SCREEN_TITLE
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.market.components.MeasureUnitItem
import com.niyaj.market.destinations.AddEditMeasureUnitScreenDestination
import com.niyaj.market.destinations.ExportMeasureUnitScreenDestination
import com.niyaj.market.destinations.ImportMeasureUnitScreenDestination
import com.niyaj.market.destinations.MeasureUnitSettingsScreenDestination
import com.niyaj.model.MeasureUnit
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldRoute
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens.MEASURE_UNIT_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination(route = MEASURE_UNIT_SCREEN)
@Composable
fun MeasureUnitScreen(
    navigator: DestinationsNavigator,
    viewModel: MeasureUnitViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditMeasureUnitScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportMeasureUnitScreenDestination, String>,
    importRecipient: ResultRecipient<ImportMeasureUnitScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val snackbarState = remember { SnackbarHostState() }

    val state = viewModel.measureUnits.collectAsStateWithLifecycle().value
    val selectedItems = viewModel.selectedItems.toList()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value
    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val showFab = viewModel.totalItems.isNotEmpty()
    val searchText = viewModel.searchText.value

    val openDialog = remember { mutableStateOf(false) }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
            }

            is NavResult.Value -> {
                viewModel.deselectItems()
                scope.launch {
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

    TrackScreenViewEvent(screenName = MEASURE_UNIT_SCREEN)

    StandardScaffoldRoute(
        currentRoute = MEASURE_UNIT_SCREEN,
        title = if (selectedItems.isEmpty()) UNIT_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditMeasureUnitScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_UNIT,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = UNIT_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navigator.navigate(AddEditMeasureUnitScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navigator.navigate(MeasureUnitSettingsScreenDestination)
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
        onBackClick = {
            if (showSearchBar) viewModel.closeSearchBar() else navigator.navigateUp()
        },
        snackbarHostState = snackbarState,
        onNavigateToScreen = navigator::navigate,
    ) { _ ->
        Crossfade(
            targetState = state,
            label = "Item State",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) UNIT_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_UNIT,
                        onClick = {
                            navigator.navigate(AddEditMeasureUnitScreenDestination())
                        },
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(
                        scrollableState = lazyGridState,
                        stateName = "MeasureUnit::List",
                    )

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(SpaceSmall),
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                    ) {
                        items(
                            items = state.data,
                            key = { it.unitId },
                        ) { item: MeasureUnit ->
                            MeasureUnitItem(
                                item = item,
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


    AnimatedVisibility(
        visible = openDialog.value
    ) {
        StandardDialog(
            title = DELETE_ITEM_TITLE,
            message = DELETE_ITEM_MESSAGE,
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