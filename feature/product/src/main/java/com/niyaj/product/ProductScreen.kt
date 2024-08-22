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

package com.niyaj.product

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.niyaj.common.tags.ProductTestTags.CREATE_NEW_PRODUCT
import com.niyaj.common.tags.ProductTestTags.DELETE_PRODUCT_MESSAGE
import com.niyaj.common.tags.ProductTestTags.DELETE_PRODUCT_TITLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NOT_AVAILABLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SCREEN_TITLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.product.components.ProductList
import com.niyaj.product.destinations.AddEditProductScreenDestination
import com.niyaj.product.destinations.DecreaseProductPriceScreenDestination
import com.niyaj.product.destinations.ExportProductScreenDestination
import com.niyaj.product.destinations.ImportProductScreenDestination
import com.niyaj.product.destinations.IncreaseProductPriceScreenDestination
import com.niyaj.product.destinations.ProductDetailsScreenDestination
import com.niyaj.product.destinations.ProductSettingScreenDestination
import com.niyaj.ui.components.CategoryList
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CategoryPreviewData.categoryList
import com.niyaj.ui.parameterProvider.ProductListPreviewParameter
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
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
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
    val snackbarState = remember { SnackbarHostState() }

    val state by viewModel.products.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val searchText = viewModel.searchText.value
    val selectedItems = viewModel.selectedItems.toList()

    ProductScreenContent(
        uiState = state,
        categories = categories,
        selectedCategory = selectedCategory,
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
        onSelectCategory = viewModel::selectCategory,
        onClickBack = navigator::popBackStack,
        onNavigateToScreen = navigator::navigate,
        onClickCreateNew = {
            navigator.navigate(AddEditProductScreenDestination())
        },
        onClickEdit = {
            navigator.navigate(AddEditProductScreenDestination(it))
        },
        onClickSettings = {
            navigator.navigate(ProductSettingScreenDestination())
        },
        onNavigateToDetails = {
            navigator.navigate(ProductDetailsScreenDestination(it))
        },
        modifier = Modifier,
        snackbarState = snackbarState,
    )

    HandleResultRecipients(
        resultRecipient = resultRecipient,
        exportRecipient = exportRecipient,
        importRecipient = importRecipient,
        increaseRecipient = increaseRecipient,
        decreaseRecipient = decreaseRecipient,
        event = event,
        onDeselectItems = viewModel::deselectItems,
        coroutineScope = scope,
        snackbarHostState = snackbarState,
    )
}

@androidx.annotation.VisibleForTesting
@Composable
internal fun ProductScreenContent(
    uiState: UiState<List<Product>>,
    categories: ImmutableList<Category>,
    selectedCategory: Int,
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
    onSelectCategory: (Int) -> Unit,
    onClickBack: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    onClickCreateNew: () -> Unit,
    onClickEdit: (Int) -> Unit,
    onClickSettings: () -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    lazyRowState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = Screens.PRODUCT_SCREEN)

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else if (selectedCategory != 0) {
            onSelectCategory(selectedCategory)
        } else if (showSearchBar) {
            onCloseSearchBar()
        } else {
            onClickBack()
        }
    }

    val showFab = uiState is UiState.Success
    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = selectedCategory) {
        scope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    PoposPrimaryScaffold(
        modifier = modifier,
        currentRoute = Screens.PRODUCT_SCREEN,
        title = if (selectedItems.isEmpty()) PRODUCT_SCREEN_TITLE else "${selectedItems.size} Selected",
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
                fabText = CREATE_NEW_PRODUCT,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = PRODUCT_SEARCH_PLACEHOLDER,
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
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            CategoryList(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
                contentPadding = PaddingValues(SpaceSmall),
                lazyRowState = lazyRowState,
                categories = categories,
                doesSelected = { it == selectedCategory },
                onSelect = onSelectCategory,
            )

            Crossfade(
                targetState = uiState,
                label = "Product::UiState",
            ) { state ->
                when (state) {
                    is UiState.Loading -> LoadingIndicator()

                    is UiState.Empty -> {
                        ItemNotAvailable(
                            text = if (searchText.isEmpty()) PRODUCT_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                            buttonText = CREATE_NEW_PRODUCT,
                            onClick = onClickCreateNew,
                        )
                    }

                    is UiState.Success -> {
                        ProductList(
                            items = state.data.toImmutableList(),
                            isInSelectionMode = selectedItems.isNotEmpty(),
                            doesSelected = selectedItems::contains,
                            onSelectItem = onClickSelectItem,
                            modifier = Modifier.weight(1f),
                            onNavigateToDetails = onNavigateToDetails,
                            showItemNotFound = true,
                            onClickCreateNew = onClickCreateNew,
                            lazyListState = lazyListState,
                        )
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
    resultRecipient: ResultRecipient<AddEditProductScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportProductScreenDestination, String>,
    importRecipient: ResultRecipient<ImportProductScreenDestination, String>,
    increaseRecipient: ResultRecipient<IncreaseProductPriceScreenDestination, String>,
    decreaseRecipient: ResultRecipient<DecreaseProductPriceScreenDestination, String>,
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

    increaseRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    decreaseRecipient.onNavResult { result ->
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
private fun ProductScreenPreview(
    @PreviewParameter(ProductListPreviewParameter::class)
    uiState: UiState<List<Product>>,
    modifier: Modifier = Modifier,
    categories: ImmutableList<Category> = categoryList.toImmutableList(),
) {
    PoposRoomTheme {
        ProductScreenContent(
            uiState = uiState,
            categories = categories,
            selectedCategory = 0,
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
            onSelectCategory = {},
            onClickBack = {},
            onNavigateToScreen = {},
            onClickCreateNew = {},
            onClickEdit = {},
            onClickSettings = {},
            onNavigateToDetails = {},
            modifier = modifier,
        )
    }
}
