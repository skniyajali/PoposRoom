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

package com.niyaj.market.market_list_item

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.util.fastForEachIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_ITEM
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NOT_AVAILABLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.MarketListTestTags.UPDATE_LIST
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.market.components.ListTypeHeader
import com.niyaj.market.components.MarketItemWithQuantityCard
import com.niyaj.market.components.MarketListItemHeader
import com.niyaj.market.components.ShareableMarketList
import com.niyaj.market.destinations.AddEditMarketItemScreenDestination
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.ItemNotFound
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.event.ShareViewModel
import com.niyaj.ui.event.UiState
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
    viewModel: MarketListItemsViewModel = hiltViewModel(),
    shareViewModel: ShareViewModel = hiltViewModel(),
) {
    val lazyListState = rememberScrollState()
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val captureController = rememberCaptureController()

    val uiState = viewModel.marketItems.collectAsStateWithLifecycle().value
    val marketDetails = viewModel.marketDetail.collectAsStateWithLifecycle().value

    val showFab = uiState is UiState.Success

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showDialog = shareViewModel.showDialog.collectAsStateWithLifecycle().value

    val groupByType = remember(uiState) {
        if (uiState is UiState.Success) uiState.data.groupBy { it.typeName } else emptyMap()
    }

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

    BackHandler {
        if(showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navigator.navigateUp()
        }
    }

    TrackScreenViewEvent(screenName = "$UPDATE_LIST/listIds/${listTypeIds.joinToString(", ")}")

    StandardScaffoldRouteNew(
        title = UPDATE_LIST,
        showBackButton = true,
        showFab = false,
        snackbarHostState = snackbarState,
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = MARKET_ITEM_SEARCH_PLACEHOLDER,
                    onClearClick = viewModel::clearSearchText,
                    onSearchTextChanged = viewModel::searchTextChanged,
                )
            } else if (showFab) {
                IconButton(
                    onClick = viewModel::openSearchBar,
                    modifier = Modifier.testTag(NAV_SEARCH_BTN),
                ) {
                    Icon(
                        imageVector = PoposIcons.Search,
                        contentDescription = "Search Icon",
                    )
                }

                IconButton(
                    onClick = viewModel::printMarketList
                ) {
                    Icon(
                        imageVector = PoposIcons.OutlinedPrint,
                        contentDescription = "Print List",
                    )
                }

                IconButton(
                    onClick = shareViewModel::onShowDialog
                ) {
                    Icon(
                        imageVector = PoposIcons.Share,
                        contentDescription = "Share List",
                    )
                }
            }
        },
        onBackClick = navigator::navigateUp,
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

            Crossfade(
                targetState = uiState,
                label = "MarketListItems::State",
            ) { state ->
                when (state) {
                    is UiState.Loading -> LoadingIndicator()

                    is UiState.Empty -> {
                        ItemNotAvailable(
                            text = MARKET_ITEM_NOT_AVAILABLE,
                            buttonText = CREATE_NEW_ITEM,
                            onClick = {
                                navigator.navigate(AddEditMarketItemScreenDestination())
                            },
                        )
                    }

                    is UiState.Success -> {
                        TrackScrollJank(scrollableState = lazyListState, stateName = "MarketListItem::State")

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(SpaceSmall)
                                .verticalScroll(lazyListState),
                            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                        ) {
                            groupByType.forEach { (itemType, groupedByType) ->
                                val groupByListType = remember(groupedByType) {
                                    groupedByType.groupBy { it.listType }
                                }

                                groupByListType.forEach { (listType, groupedByList) ->
                                    ListTypeHeader(
                                        itemType = itemType,
                                        listType = listType,
                                        listCount = groupedByList.size,
                                    )

                                    groupedByList.fastForEachIndexed { i, item ->
                                        key("${item.listType}(${item.listWithTypeId}, ${item.itemId})") {
                                            MarketItemWithQuantityCard(
                                                item = item,
                                                onAddItem = viewModel::onAddItem,
                                                onRemoveItem = viewModel::onRemoveItem,
                                                onDecreaseQuantity = viewModel::onDecreaseQuantity,
                                                onIncreaseQuantity = viewModel::onIncreaseQuantity,
                                            )
                                        }

                                        if (i != groupedByList.size - 1) {
                                            Spacer(modifier = Modifier.height(SpaceMini))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))

                            ItemNotFound(
                                onBtnClick = {
                                    navigator.navigate(AddEditMarketItemScreenDestination())
                                },
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = showDialog
    ) {
        val items = viewModel.shareableMarketList.collectAsStateWithLifecycle().value

        if (items.isNotEmpty() && marketDetails != null) {
            ShareableMarketList(
                captureController = captureController,
                marketDate = marketDetails.marketDate,
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
                }
            )
        }
    }
}