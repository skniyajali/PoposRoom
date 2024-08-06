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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_NOTE_TEXT
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_TITLE
import com.niyaj.common.utils.createImportNote
import com.niyaj.customer.components.CustomersData
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.domain.utils.ImportExport
import com.niyaj.model.Customer
import com.niyaj.ui.components.ImportScaffold
import com.niyaj.ui.parameterProvider.CustomerPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.CUSTOMER_IMPORT_SCREEN
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Destination(route = CUSTOMER_IMPORT_SCREEN)
@Composable
fun CustomerImportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: CustomerSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val importedItems by viewModel.importedItems.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val selectedItems = viewModel.selectedItems.toList()

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val data = ImportExport.readDataAsync<Customer>(context, it)

                    viewModel.onEvent(CustomerSettingsEvent.OnImportCustomerItemsFromFile(data))
                }
            }
        }

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

    CustomerImportScreenContent(
        modifier = Modifier,
        isLoading = isLoading,
        importedItems = importedItems.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
        onClickSelectItem = viewModel::selectItem,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onClickImport = {
            viewModel.onEvent(CustomerSettingsEvent.ImportCustomerItemsToDatabase)
        },
        onClickOpenFile = {
            importLauncher.launch(ImportExport.openFile(context))
        },
        onBackClick = navigator::navigateUp,
    )
}

@VisibleForTesting
@Composable
internal fun CustomerImportScreenContent(
    isLoading: Boolean,
    importedItems: ImmutableList<Customer>,
    selectedItems: ImmutableList<Int>,
    onClickSelectItem: (Int) -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onClickImport: () -> Unit,
    onClickOpenFile: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    TrackScreenViewEvent(screenName = CUSTOMER_IMPORT_SCREEN)
    val title =
        if (selectedItems.isEmpty()) IMPORT_CUSTOMER_TITLE else "${selectedItems.size} Selected"

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else {
            onBackClick()
        }
    }

    ImportScaffold(
        title = title,
        modifier = modifier,
        isLoading = isLoading,
        importFileNote = IMPORT_CUSTOMER_NOTE_TEXT,
        importButtonText = IMPORT_CUSTOMER_TITLE,
        importNote = createImportNote(selectedItems, importedItems.size, "customer"),
        showBottomBar = importedItems.isNotEmpty(),
        showBackButton = selectedItems.isEmpty(),
        showScrollToTop = !lazyListState.isScrollingUp(),
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onClickDeselect = onClickDeselect,
        onClickSelectAll = onClickSelectAll,
        onClickImport = onClickImport,
        onClickOpenFile = onClickOpenFile,
        onClickScrollToTop = {
            scope.launch {
                lazyListState.animateScrollToItem(index = 0)
            }
        },
    ) {
        CustomersData(
            customers = importedItems,
            isInSelectionMode = true,
            doesSelected = selectedItems::contains,
            onClickSelectItem = onClickSelectItem,
            onNavigateToDetails = {},
            modifier = Modifier.fillMaxSize(),
            lazyListState = lazyListState,
        )
    }
}

@DevicePreviews
@Composable
private fun CustomerImportScreenEmptyContentPreview() {
    PoposRoomTheme {
        CustomerImportScreenContent(
            modifier = Modifier,
            isLoading = false,
            importedItems = persistentListOf(),
            selectedItems = persistentListOf(),
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onClickImport = {},
            onClickOpenFile = {},
            onBackClick = {},
        )
    }
}

@DevicePreviews
@Composable
private fun CustomerImportScreenContentPreview(
    items: ImmutableList<Customer> = CustomerPreviewData.customerList.toImmutableList(),
) {
    PoposRoomTheme {
        CustomerImportScreenContent(
            modifier = Modifier,
            isLoading = false,
            importedItems = items,
            selectedItems = persistentListOf(),
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onClickImport = {},
            onClickOpenFile = {},
            onBackClick = {},
        )
    }
}
