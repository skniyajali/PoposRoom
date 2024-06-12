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

package com.niyaj.employeePayment.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.PaymentScreenTags.CREATE_NEW_PAYMENT
import com.niyaj.common.tags.PaymentScreenTags.EXPORT_PAYMENT_FILE_NAME
import com.niyaj.common.tags.PaymentScreenTags.EXPORT_PAYMENT_TITLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_NOT_AVAILABLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport
import com.niyaj.employeePayment.components.EmployeePaymentList
import com.niyaj.employeePayment.components.ViewType
import com.niyaj.employeePayment.destinations.AddEditPaymentScreenDestination
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.parameterProvider.PaymentPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.PAYMENT_EXPORT_SCREEN
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

@Destination(route = PAYMENT_EXPORT_SCREEN)
@Composable
fun PaymentExportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: PaymentSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val items by viewModel.items.collectAsStateWithLifecycle()
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
        ) { activityResult ->
            activityResult.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeDataAsync(context, it, exportedItems)
                    val exportedSize = exportedItems.sumOf { it.payments.size }

                    if (result.isSuccess) {
                        resultBackNavigator.navigateBack("$exportedSize payments has been exported.")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export items.")
                    }
                }
            }
        }

    PaymentExportScreenContent(
        modifier = Modifier,
        items = items.toImmutableList(),
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
                    fileName = EXPORT_PAYMENT_FILE_NAME,
                )
                exportLauncher.launch(result)
                viewModel.onEvent(PaymentSettingsEvent.GetExportedItems)
            }
        },
        onBackClick = navigator::navigateUp,
        onClickToAddItem = {
            navigator.navigate(AddEditPaymentScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
internal fun PaymentExportScreenContent(
    modifier: Modifier = Modifier,
    items: ImmutableList<EmployeeWithPayments>,
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
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    padding: PaddingValues = PaddingValues(SpaceSmallMax, 0.dp, SpaceSmallMax, SpaceLarge),
) {
    TrackScreenViewEvent(screenName = "PaymentExportScreen")

    var viewType by remember { mutableStateOf(ViewType.CARD) }
    val text = if (searchText.isEmpty()) PAYMENT_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND
    val title = if (selectedItems.isEmpty()) EXPORT_PAYMENT_TITLE else "${selectedItems.size} Selected"

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else if (showSearchBar) {
            onClickCloseSearch()
        } else {
            onBackClick()
        }
    }

    PoposSecondaryScaffold(
        modifier = modifier,
        title = title,
        showBackButton = selectedItems.isEmpty() || showSearchBar,
        showBottomBar = items.isNotEmpty(),
        showSecondaryBottomBar = true,
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = PAYMENT_SEARCH_PLACEHOLDER,
                    onClearClick = onClearClick,
                    onSearchTextChanged = onSearchTextChanged,
                )
            } else {
                if (items.isNotEmpty()) {
                    IconButton(
                        onClick = onClickSelectAll,
                    ) {
                        Icon(
                            imageVector = PoposIcons.Checklist,
                            contentDescription = Constants.SELECT_ALL_ICON,
                        )
                    }

                    IconButton(
                        onClick = onClickOpenSearch,
                        modifier = Modifier.testTag(NAV_SEARCH_BTN),
                    ) {
                        Icon(
                            imageVector = PoposIcons.Search,
                            contentDescription = "Search Icon",
                        )
                    }

                    IconButton(
                        onClick = {
                            viewType = when (viewType) {
                                ViewType.LIST -> ViewType.CARD
                                ViewType.CARD -> ViewType.LIST
                            }
                        },
                    ) {
                        Icon(
                            imageVector = if (viewType == ViewType.LIST) {
                                PoposIcons.ViewAgenda
                            } else {
                                PoposIcons.CalendarViewDay
                            },
                            contentDescription = "Change View",
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} payments will be exported.")

                PoposButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPORT_PAYMENT_TITLE),
                    enabled = items.isNotEmpty(),
                    text = EXPORT_PAYMENT_TITLE,
                    icon = PoposIcons.Upload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                    onClick = onClickExport,
                )
            }
        },
        onBackClick = if (showSearchBar) onClickCloseSearch else onBackClick,
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onClickDeselect,
            ) {
                Icon(
                    imageVector = PoposIcons.Close,
                    contentDescription = "Deselect All",
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (items.isEmpty()) {
                ItemNotAvailable(
                    text = text,
                    buttonText = CREATE_NEW_PAYMENT,
                    onClick = onClickToAddItem,
                )
            } else {
                EmployeePaymentList(
                    modifier = Modifier,
                    viewType = viewType,
                    items = items,
                    doesSelected = selectedItems::contains,
                    onSelectItem = onSelectItem,
                    isInSelectionMode = true,
                    lazyListState = lazyListState,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun PaymentExportScreenEmptyDataPreview() {
    PoposRoomTheme {
        PaymentExportScreenContent(
            modifier = Modifier,
            items = persistentListOf(),
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
        )
    }
}

@DevicePreviews
@Composable
private fun PaymentExportScreenContentPreview(
    items: ImmutableList<EmployeeWithPayments> = PaymentPreviewData.employeesWithPayments.toImmutableList(),
) {
    PoposRoomTheme {
        PaymentExportScreenContent(
            modifier = Modifier,
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
        )
    }
}