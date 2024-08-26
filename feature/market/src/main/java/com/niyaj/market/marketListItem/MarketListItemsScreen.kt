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

package com.niyaj.market.marketListItem

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_ITEM
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NOT_AVAILABLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.MarketListTestTags.UPDATE_LIST
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.market.components.ListTypeHeader
import com.niyaj.market.components.MarketItemWithQuantityWithListTypeCard
import com.niyaj.market.components.MarketListItemHeader
import com.niyaj.market.components.ShareableMarketList
import com.niyaj.market.destinations.AddEditMarketItemScreenDestination
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketListAndType
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.ItemNotFound
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.event.ShareViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketItemAndQuantityData.maretListAndType
import com.niyaj.ui.parameterProvider.MarketItemAndQuantityWithDifferentTypePreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun MarketListItemsScreen(
    listTypeIds: IntArray,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: MarketListItemsViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel(),
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val captureController = rememberCaptureController()

    val uiState by viewModel.marketItems.collectAsStateWithLifecycle()
    val marketDetails by viewModel.marketDetail.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val showDialog by shareViewModel.showDialog.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val searchText = viewModel.searchText.value

    LaunchedEffect(
        key1 = event,
    ) {
        event?.let {
            when (it) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarState.showSnackbar(it.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> Unit
            }
        }
    }

    TrackScreenViewEvent(screenName = "$UPDATE_LIST/listIds/${listTypeIds.joinToString(", ")}")

    MarketListItemsScreenContent(
        uiState = uiState,
        marketDetails = marketDetails,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onClearClick = viewModel::clearSearchText,
        onSearchTextChanged = viewModel::searchTextChanged,
        onOpenSearchBar = viewModel::openSearchBar,
        onCloseSearchBar = viewModel::closeSearchBar,
        onPrintList = viewModel::printMarketList,
        onClickShare = shareViewModel::onShowDialog,
        onAddItem = viewModel::onAddItem,
        onRemoveItem = viewModel::onRemoveItem,
        onDecreaseQuantity = viewModel::onDecreaseQuantity,
        onIncreaseQuantity = viewModel::onIncreaseQuantity,
        onBackClick = navigator::navigateUp,
        onClickAddNewMarketItem = {
            navigator.navigate(AddEditMarketItemScreenDestination())
        },
        modifier = modifier,
        snackbarState = snackbarState,
    )

    AnimatedVisibility(
        visible = showDialog,
    ) {
        val items by viewModel.shareableMarketList.collectAsStateWithLifecycle()

        if (items.isNotEmpty() && marketDetails != null) {
            ShareableMarketList(
                captureController = captureController,
                marketDate = marketDetails!!.marketDate,
                marketLists = items,
                onDismiss = shareViewModel::onDismissDialog,
                onClickShare = {
                    captureController.captureLongScreenshot()
                },
                onCaptured = { bitmap, error ->
                    bitmap?.let {
                        scope.launch {
                            val uri = shareViewModel.saveImage(it, context)
                            uri?.let {
                                shareViewModel.shareContent(context, "Share Image", uri)
                            }
                        }
                    }
                    error?.let {
                        Log.d("Capturable", "Error: ${it.message}\n${it.stackTrace.joinToString()}")
                    }
                },
            )
        }
    }
}

@VisibleForTesting
@Composable
internal fun MarketListItemsScreenContent(
    uiState: UiState<List<MarketItemAndQuantity>>,
    marketDetails: MarketListAndType?,
    showSearchBar: Boolean,
    searchText: String,
    onClearClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onOpenSearchBar: () -> Unit,
    onCloseSearchBar: () -> Unit,
    onPrintList: () -> Unit,
    onClickShare: () -> Unit,
    onAddItem: (listTypeId: Int, itemId: Int) -> Unit,
    onRemoveItem: (listTypeId: Int, itemId: Int) -> Unit,
    onDecreaseQuantity: (listTypeId: Int, itemId: Int) -> Unit,
    onIncreaseQuantity: (listTypeId: Int, itemId: Int) -> Unit,
    onBackClick: () -> Unit,
    onClickAddNewMarketItem: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    lazyListState: LazyListState = rememberLazyListState(),
) {
    BackHandler {
        if (showSearchBar) {
            onCloseSearchBar()
        } else {
            onBackClick()
        }
    }

    val showFab = uiState is UiState.Success

    PoposSecondaryScaffold(
        title = UPDATE_LIST,
        onBackClick = if (showSearchBar) onCloseSearchBar else onBackClick,
        modifier = modifier,
        showBackButton = true,
        showFab = false,
        snackbarHostState = snackbarState,
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = MARKET_ITEM_SEARCH_PLACEHOLDER,
                    onClearClick = onClearClick,
                    onSearchTextChanged = onSearchTextChanged,
                )
            } else if (showFab) {
                IconButton(
                    onClick = onOpenSearchBar,
                    modifier = Modifier.testTag(NAV_SEARCH_BTN),
                ) {
                    Icon(
                        imageVector = PoposIcons.Search,
                        contentDescription = "Search Icon",
                    )
                }

                IconButton(
                    onClick = onPrintList,
                ) {
                    Icon(
                        imageVector = PoposIcons.OutlinedPrint,
                        contentDescription = "Print List",
                    )
                }

                IconButton(
                    onClick = onClickShare,
                ) {
                    Icon(
                        imageVector = PoposIcons.Share,
                        contentDescription = "Share List",
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            marketDetails?.let { marketDetail ->
                MarketListItemHeader(marketDate = marketDetail.marketDate)
            }

            when (uiState) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = MARKET_ITEM_NOT_AVAILABLE,
                        buttonText = CREATE_NEW_ITEM,
                        onClick = onClickAddNewMarketItem,
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(scrollableState = lazyListState, stateName = "MarketListItem::State")

                    val groupByType =
                        remember(uiState.data) { uiState.data.groupBy { it.typeName } }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(vertical = SpaceMini),
                        verticalArrangement = Arrangement.spacedBy(SpaceMini),
                    ) {
                        groupByType.forEach { (itemType, groupedByType) ->
                            val groupByListType = groupedByType.groupBy { it.listType }

                            groupByListType.forEach { (listType, groupedByList) ->
                                item("${itemType}_$listType") {
                                    ListTypeHeader(
                                        modifier = Modifier.padding(horizontal = SpaceSmall),
                                        itemType = itemType,
                                        listType = listType,
                                        listCount = groupedByList.size,
                                    )
                                }

                                items(
                                    items = groupedByList,
                                    key = {
                                        "${it.listType}(${it.listWithTypeId}, ${it.itemId})"
                                    },
                                ) { item ->
                                    MarketItemWithQuantityWithListTypeCard(
                                        item = item,
                                        onAddItem = onAddItem,
                                        onRemoveItem = onRemoveItem,
                                        onDecreaseQuantity = onDecreaseQuantity,
                                        onIncreaseQuantity = onIncreaseQuantity,
                                    )
                                }
                            }
                        }

                        item {
                            ItemNotFound(onBtnClick = onClickAddNewMarketItem)
                        }
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun MarketListItemsScreenContentPreview(
    @PreviewParameter(MarketItemAndQuantityWithDifferentTypePreviewParameter::class)
    uiState: UiState<List<MarketItemAndQuantity>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        MarketListItemsScreenContent(
            modifier = modifier,
            uiState = uiState,
            marketDetails = maretListAndType,
            showSearchBar = false,
            searchText = "",
            onClearClick = {},
            onSearchTextChanged = {},
            onOpenSearchBar = {},
            onCloseSearchBar = {},
            onPrintList = {},
            onClickShare = {},
            onAddItem = { _, _ -> },
            onRemoveItem = { _, _ -> },
            onDecreaseQuantity = { _, _ -> },
            onIncreaseQuantity = { _, _ -> },
            onBackClick = {},
            onClickAddNewMarketItem = {},
        )
    }
}
