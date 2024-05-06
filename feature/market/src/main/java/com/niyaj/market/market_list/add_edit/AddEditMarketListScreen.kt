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

package com.niyaj.market.market_list.add_edit

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_ITEM
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_LIST
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NOT_AVAILABLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.MarketListTestTags.UPDATE_LIST
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.market.components.MarketItemWithQuantityCard
import com.niyaj.market.components.MarketListItemHeader
import com.niyaj.market.components.ShareableMarketList
import com.niyaj.market.destinations.AddEditMarketItemScreenDestination
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.ItemNotFound
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination
fun AddEditMarketListScreen(
    marketId: Int = 0,
    navigator: DestinationsNavigator,
    viewModel: AddEditMarketListViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyListState()
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val captureController = rememberCaptureController()

    val marketItems = viewModel.marketItems.collectAsStateWithLifecycle().value
    val marketList = viewModel.marketList.collectAsStateWithLifecycle().value

    val showFab = viewModel.totalItems.isNotEmpty()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val title = if (marketId == 0) CREATE_NEW_LIST else UPDATE_LIST

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value
    val selectedDate = viewModel.selectedDate.collectAsStateWithLifecycle().value

    val dialogState = rememberMaterialDialogState()
    val showList = viewModel.showList.collectAsStateWithLifecycle().value
    val marketLists = viewModel.listItems.collectAsStateWithLifecycle().value

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

    StandardScaffoldRouteNew(
        title = title,
        showBackButton = true,
        showFab = lazyListState.isScrollingUp() && showFab,
        snackbarHostState = snackbarState,
        fabPosition = FabPosition.EndOverlay,
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onShowList,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = PoposIcons.Share, contentDescription = "Share List")
            }
        },
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = MARKET_ITEM_SEARCH_PLACEHOLDER,
                    onClearClick = viewModel::clearSearchText,
                    onSearchTextChanged = viewModel::searchTextChanged
                )
            } else if (showFab){
                IconButton(
                    onClick = viewModel::openSearchBar,
                    modifier = Modifier.testTag(NAV_SEARCH_BTN)
                ) {
                    Icon(
                        imageVector = PoposIcons.Search,
                        contentDescription = "Search Icon",
                    )
                }
            }
        },
        onBackClick = navigator::navigateUp
    ) {
        Crossfade(
            targetState = marketItems,
            label = "Add Edit Market List State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = MARKET_ITEM_NOT_AVAILABLE,
                        buttonText = CREATE_NEW_ITEM,
                        onClick = {
                            navigator.navigate(AddEditMarketItemScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    val groupedByType = remember(state.data) {
                        state.data.groupBy { it.item.itemType }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        contentPadding = PaddingValues(SpaceSmall),
                        state = lazyListState,
                    ) {
                        item {
                            marketList?.let {
                                MarketListItemHeader(
                                    marketList = it,
                                    selectedDate = selectedDate.ifEmpty { it.marketDate.toString() },
                                    onClickDate = {
                                        dialogState.show()
                                    },
                                    onClickSaveChanges = {}
                                )
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }

                        groupedByType.forEach { (type, marketItems) ->
                            stickyHeader {
                                InfoText(
                                    text = type,
                                    icon = PoposIcons.Category,
                                    backgroundColor = MaterialTheme.colorScheme.surface,
                                    textStyle = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(SpaceMini))
                            }

                            items(
                                items = marketItems,
                                key = {
                                    it.item.itemId
                                }
                            ) { itemWithQuantity ->
                                val quantity =
                                    itemWithQuantity.quantity

                                val doesExist =
                                    itemWithQuantity.doesExist

                                MarketItemWithQuantityCard(
                                    item = itemWithQuantity.item,
                                    itemQuantity = quantity,
                                    itemState = {
                                        if (doesExist) {
                                            ToggleableState.On
                                        } else if (marketList?.whitelistItems?.contains(it) == true) {
                                            ToggleableState.Indeterminate
                                        } else {
                                            ToggleableState.Off
                                        }
                                    },
                                    onAddItem = viewModel::onAddItem,
                                    onRemoveItem = viewModel::onRemoveItem,
                                    onDecreaseQuantity = viewModel::onDecreaseQuantity,
                                    onIncreaseQuantity = viewModel::onIncreaseQuantity
                                )

                                Spacer(modifier = Modifier.height(SpaceMini))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            ItemNotFound(
                                onBtnClick = {
                                    navigator.navigate(AddEditMarketItemScreenDestination())
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker(
            allowedDateValidator = { date ->
                date <= LocalDate.now()
            }
        ) { date ->
            viewModel.selectDate(date.toMilliSecond)
        }
    }

    AnimatedVisibility(
        visible = showList && marketLists.isNotEmpty()
    ) {
        ShareableMarketList(
            captureController = captureController,
            marketDate = marketList?.marketDate ?: System.currentTimeMillis(),
            marketLists = marketLists,
            onDismiss = viewModel::onDismissList,
            onClickShare = {
                captureController.captureLongScreenshot()
            },
            onCaptured = { bitmap, error ->
                bitmap?.let {
                    scope.launch {
                        val uri = viewModel.saveImage(it, context)
                        uri?.let {
                            viewModel.shareContent(context, "Share Image", uri)
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