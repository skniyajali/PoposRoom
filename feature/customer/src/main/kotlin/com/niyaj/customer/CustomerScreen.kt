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

package com.niyaj.customer

import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
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
import com.niyaj.common.tags.CustomerTestTags.CREATE_NEW_CUSTOMER
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NOT_AVAILABLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SCREEN_TITLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CustomerTestTags.DELETE_CUSTOMER_MESSAGE
import com.niyaj.common.tags.CustomerTestTags.DELETE_CUSTOMER_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.customer.components.CustomersData
import com.niyaj.customer.destinations.AddEditCustomerScreenDestination
import com.niyaj.customer.destinations.CustomerDetailsScreenDestination
import com.niyaj.customer.destinations.CustomerExportScreenDestination
import com.niyaj.customer.destinations.CustomerImportScreenDestination
import com.niyaj.customer.destinations.CustomerSettingsScreenDestination
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.Customer
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CustomersPreviewParameter
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
@Destination(route = Screens.CUSTOMER_SCREEN)
@Composable
fun CustomerScreen(
    navigator: DestinationsNavigator,
    viewModel: CustomerViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCustomerScreenDestination, String>,
    exportRecipient: ResultRecipient<CustomerExportScreenDestination, String>,
    importRecipient: ResultRecipient<CustomerImportScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    val uiState by viewModel.customers.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val selectedItems = viewModel.selectedItems.toList()
    val searchText = viewModel.searchText.value

    CustomerScreenContent(
        modifier = Modifier,
        uiState = uiState,
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
            navigator.navigate(AddEditCustomerScreenDestination())
        },
        onClickEdit = {
            navigator.navigate(AddEditCustomerScreenDestination(it))
        },
        onClickSettings = {
            navigator.navigate(CustomerSettingsScreenDestination())
        },
        onNavigateToDetails = {
            navigator.navigate(CustomerDetailsScreenDestination(it))
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
internal fun CustomerScreenContent(
    modifier: Modifier = Modifier,
    uiState: UiState<List<Customer>>,
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
    onNavigateToDetails: (Int) -> Unit,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = Screens.CUSTOMER_SCREEN)

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
        currentRoute = Screens.CUSTOMER_SCREEN,
        title = if (selectedItems.isEmpty()) CUSTOMER_SCREEN_TITLE else "${selectedItems.size} Selected",
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
                fabText = CREATE_NEW_CUSTOMER,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = CUSTOMER_SEARCH_PLACEHOLDER,
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
            label = "Customer::UiState",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) CUSTOMER_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_CUSTOMER,
                        onClick = onClickCreateNew,
                    )
                }

                is UiState.Success -> {
                    CustomersData(
                        customers = state.data,
                        isInSelectionMode = selectedItems.isNotEmpty(),
                        doesSelected = selectedItems::contains,
                        onClickSelectItem = onClickSelectItem,
                        onNavigateToDetails = onNavigateToDetails,
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
            title = DELETE_CUSTOMER_TITLE,
            message = DELETE_CUSTOMER_MESSAGE,
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
    resultRecipient: ResultRecipient<AddEditCustomerScreenDestination, String>,
    exportRecipient: ResultRecipient<CustomerExportScreenDestination, String>,
    importRecipient: ResultRecipient<CustomerImportScreenDestination, String>,
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
private fun CustomerScreenPreview(
    @PreviewParameter(CustomersPreviewParameter::class)
    uiState: UiState<List<Customer>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CustomerScreenContent(
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
            onNavigateToDetails = {},
        )
    }
}
