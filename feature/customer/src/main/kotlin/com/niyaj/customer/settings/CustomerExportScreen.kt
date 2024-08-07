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

package com.niyaj.customer.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.CustomerTestTags.CREATE_NEW_CUSTOMER
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NOT_AVAILABLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CustomerTestTags.EXPORT_CUSTOMER_FILE_NAME
import com.niyaj.common.tags.CustomerTestTags.EXPORT_CUSTOMER_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.createExportNote
import com.niyaj.customer.components.CustomersData
import com.niyaj.customer.destinations.AddEditCustomerScreenDestination
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.domain.utils.ImportExport
import com.niyaj.model.Customer
import com.niyaj.ui.components.ExportScaffold
import com.niyaj.ui.parameterProvider.CustomerPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.CUSTOMER_EXPORT_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Destination(route = CUSTOMER_EXPORT_SCREEN)
@Composable
fun CustomerExportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: CustomerSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val exportedItems by viewModel.exportedItems.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val searchText = viewModel.searchText.value
    val selectedItems = viewModel.selectedItems.toList()

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeDataAsync(context, it, exportedItems)
                    if (result.isSuccess) {
                        resultBackNavigator.navigateBack("${exportedItems.size} Customers has been exported.")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export customers.")
                    }
                }
            }
        }

    CustomerExportScreenContent(
        items = customers.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
        showSearchBar = showSearchBar,
        searchText = searchText,
        onClearClick = viewModel::clearSearchText,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClickOpenSearch = viewModel::openSearchBar,
        onClickCloseSearch = viewModel::closeSearchBar,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onSelectItem = viewModel::selectItem,
        onClickExport = {
            scope.launch {
                val result = ImportExport.createFile(
                    context = context,
                    fileName = EXPORT_CUSTOMER_FILE_NAME,
                )
                exportLauncher.launch(result)
                viewModel.onEvent(CustomerSettingsEvent.GetExportedItems)
            }
        },
        onBackClick = navigator::navigateUp,
        onClickToAddItem = {
            navigator.navigate(AddEditCustomerScreenDestination())
        },
        modifier = Modifier,
    )
}

@VisibleForTesting
@Composable
internal fun CustomerExportScreenContent(
    items: ImmutableList<Customer>,
    selectedItems: ImmutableList<Int>,
    showSearchBar: Boolean,
    searchText: String,
    onClearClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClickOpenSearch: () -> Unit,
    onClickCloseSearch: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onSelectItem: (Int) -> Unit,
    onClickExport: () -> Unit,
    onBackClick: () -> Unit,
    onClickToAddItem: () -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = CUSTOMER_EXPORT_SCREEN)

    val text = if (searchText.isEmpty()) CUSTOMER_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND
    val title =
        if (selectedItems.isEmpty()) EXPORT_CUSTOMER_TITLE else "${selectedItems.size} Selected"

    BackHandler {
        if (showSearchBar) {
            onClickCloseSearch()
        } else if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else {
            onBackClick()
        }
    }

    ExportScaffold(
        title = title,
        exportNote = createExportNote(selectedItems, items.size, "customer"),
        searchPlaceholder = CUSTOMER_SEARCH_PLACEHOLDER,
        exportButtonText = EXPORT_CUSTOMER_TITLE,
        emptyButtonText = CREATE_NEW_CUSTOMER,
        emptyText = text,
        showBottomBar = items.isNotEmpty(),
        showBackButton = selectedItems.isEmpty(),
        searchText = searchText,
        showSearchBar = showSearchBar,
        showScrollToTop = !lazyListState.isScrollingUp(),
        onBackClick = onBackClick,
        onClickDeselect = onClickDeselect,
        onClickSelectAll = onClickSelectAll,
        onClickOpenSearch = onClickOpenSearch,
        onClickCloseSearch = onClickCloseSearch,
        onClearClick = onClearClick,
        onSearchTextChanged = onSearchTextChanged,
        onClickExport = onClickExport,
        onClickEmptyBtn = onClickToAddItem,
        onClickScrollToTop = {
            scope.launch {
                lazyListState.animateScrollToItem(0)
            }
        },
        modifier = modifier,
    ) {
        CustomersData(
            customers = items,
            isInSelectionMode = true,
            doesSelected = selectedItems::contains,
            onClickSelectItem = onSelectItem,
            onNavigateToDetails = {},
            modifier = Modifier.fillMaxSize(),
            lazyListState = lazyListState,
        )
    }
}

@DevicePreviews
@Composable
private fun CustomerExportScreenContentPreview(
    items: ImmutableList<Customer> = CustomerPreviewData.customerList.toImmutableList(),
) {
    PoposRoomTheme {
        CustomerExportScreenContent(
            items = items,
            selectedItems = persistentListOf(),
            showSearchBar = false,
            searchText = "",
            onClearClick = {},
            onSearchTextChanged = {},
            onClickOpenSearch = {},
            onClickCloseSearch = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onSelectItem = {},
            onClickExport = {},
            onBackClick = {},
            onClickToAddItem = {},
            modifier = Modifier,
        )
    }
}
