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

package com.niyaj.product

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.niyaj.common.tags.ProductTestTags.CREATE_NEW_PRODUCT
import com.niyaj.common.tags.ProductTestTags.DELETE_PRODUCT_MESSAGE
import com.niyaj.common.tags.ProductTestTags.DELETE_PRODUCT_TITLE
import com.niyaj.common.tags.ProductTestTags.NO_ITEMS_IN_PRODUCT
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NOT_AVAIlABLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SCREEN_TITLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SEARCH_PLACEHOLDER
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.product.components.ProductCard
import com.niyaj.product.destinations.AddEditProductScreenDestination
import com.niyaj.product.destinations.DecreaseProductPriceScreenDestination
import com.niyaj.product.destinations.ExportProductScreenDestination
import com.niyaj.product.destinations.ImportProductScreenDestination
import com.niyaj.product.destinations.IncreaseProductPriceScreenDestination
import com.niyaj.product.destinations.ProductDetailsScreenDestination
import com.niyaj.product.destinations.ProductSettingScreenDestination
import com.niyaj.ui.components.CategoriesData
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.ItemNotFound
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldRoute
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
@Destination(route = Screens.PRODUCT_SCREEN)
@Composable
fun ProductScreen(
    navigator: DestinationsNavigator,
    viewModel: ProductViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditProductScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportProductScreenDestination, String>,
    importRecipient: ResultRecipient<ImportProductScreenDestination, String>,
    increaseRecipient: ResultRecipient<IncreaseProductPriceScreenDestination, String>,
    decreaseRecipient: ResultRecipient<DecreaseProductPriceScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyRowState = rememberLazyListState()
    val snackbarState = remember { SnackbarHostState() }

    val state = viewModel.products.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle().value

    val showFab = viewModel.totalItems.isNotEmpty() && selectedCategory == 0

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val categories = viewModel.categories.collectAsStateWithLifecycle().value

    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.errorMessage)
                    }
                    viewModel.deselectItems()
                }

                is UiEvent.OnSuccess -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.successMessage)
                    }
                    viewModel.deselectItems()
                }
            }
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

    increaseRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    decreaseRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    LaunchedEffect(key1 = selectedCategory) {
        scope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        } else if (selectedCategory != 0) {
            viewModel.selectCategory(selectedCategory)
        } else {
            navigator.navigateUp()
        }
    }

    TrackScreenViewEvent(screenName = Screens.PRODUCT_SCREEN)

    StandardScaffoldRoute(
        currentRoute = Screens.PRODUCT_SCREEN,
        title = if (selectedItems.isEmpty()) PRODUCT_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_PRODUCT,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditProductScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = PRODUCT_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navigator.navigate(AddEditProductScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navigator.navigate(ProductSettingScreenDestination())
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = viewModel::closeSearchBar,
        snackbarHostState = snackbarState,
        onNavigateToScreen = navigator::navigate,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
        ) {
            CategoriesData(
                lazyRowState = lazyRowState,
                categories = categories,
                doesSelected = { it == selectedCategory },
                onSelect = viewModel::selectCategory,
            )

            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailableHalf(
                        modifier = Modifier.weight(2f),
                        text = if (searchText.isEmpty()) PRODUCT_NOT_AVAIlABLE else NO_ITEMS_IN_PRODUCT,
                        buttonText = CREATE_NEW_PRODUCT,
                        onClick = {
                            navigator.navigate(AddEditProductScreenDestination())
                        },
                    )
                }

                is UiState.Success -> {
                    TrackScrollJank(scrollableState = lazyListState, stateName = "Product::List")

                    LazyColumn(
                        state = lazyListState,
                    ) {
                        itemsIndexed(
                            items = state.data,
                            key = { index, item ->
                                item.productName.plus(index).plus(item.productId)
                            },
                        ) { index, item ->
                            ProductCard(
                                item = item,
                                doesSelected = selectedItems::contains,
                                onClick = {
                                    if (selectedItems.isNotEmpty()) {
                                        viewModel.selectItem(it)
                                    } else {
                                        navigator.navigate(ProductDetailsScreenDestination(it))
                                    }
                                },
                                onLongClick = viewModel::selectItem,
                            )

                            if (index == state.data.size - 1) {
                                Spacer(modifier = Modifier.height(SpaceLarge))
                                Spacer(modifier = Modifier.height(SpaceLarge))
                            }
                        }

                        item {
                            ItemNotFound(
                                btnText = CREATE_NEW_PRODUCT,
                                onBtnClick = {
                                    navigator.navigate(AddEditProductScreenDestination())
                                },
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
            title = DELETE_PRODUCT_TITLE,
            message = DELETE_PRODUCT_MESSAGE,
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