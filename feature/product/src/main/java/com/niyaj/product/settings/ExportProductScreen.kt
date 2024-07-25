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

package com.niyaj.product.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.tags.ProductTestTags.EXPORTED_PRODUCTS_FILE_NAME
import com.niyaj.common.tags.ProductTestTags.EXPORT_PRODUCTS_TITLE
import com.niyaj.common.tags.ProductTestTags.NO_ITEMS_IN_PRODUCT
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport
import com.niyaj.domain.utils.ImportExport.writeDataAsync
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.product.components.ProductList
import com.niyaj.product.destinations.AddEditProductScreenDestination
import com.niyaj.ui.components.CategoryList
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.parameterProvider.ProductPreviewData
import com.niyaj.ui.utils.DevicePreviews
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

@Destination
@Composable
fun ExportProductScreen(
    navigator: DestinationsNavigator,
    viewModel: ProductSettingsViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val exportedProducts by viewModel.exportedProducts.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val selectedItems = viewModel.selectedItems.toList()
    val selectedCategory = viewModel.selectedCategory.toList()
    val searchText = viewModel.searchText.value

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
                    val result = writeDataAsync(context, it, exportedProducts)

                    if (result.isSuccess) {
                        resultBackNavigator.navigateBack("${exportedProducts.size} Products has been exported")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export products")
                    }
                }
            }
        }

    ExportProductScreenContent(
        modifier = Modifier,
        items = products.toImmutableList(),
        categories = categories,
        selectedItems = selectedItems.toImmutableList(),
        selectedCategory = selectedCategory,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onClearClick = viewModel::clearSearchText,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClickOpenSearch = viewModel::openSearchBar,
        onClickCloseSearch = viewModel::closeSearchBar,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onSelectItem = viewModel::selectItem,
        onBackClick = navigator::navigateUp,
        onSelectCategory = {
            viewModel.onEvent(ProductSettingsEvent.OnSelectCategory(it))
        },
        onClickExport = {
            scope.launch {
                val result = ImportExport.createFile(
                    context = context,
                    fileName = EXPORTED_PRODUCTS_FILE_NAME,
                )
                exportLauncher.launch(result)
                viewModel.onEvent(ProductSettingsEvent.GetExportedProduct)
            }
        },
        onClickToAddItem = {
            navigator.navigate(AddEditProductScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
internal fun ExportProductScreenContent(
    modifier: Modifier = Modifier,
    items: ImmutableList<Product>,
    categories: ImmutableList<Category>,
    selectedCategory: List<Int>,
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
    onSelectCategory: (Int) -> Unit,
    onClickExport: () -> Unit,
    onBackClick: () -> Unit,
    onClickToAddItem: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    lazyRowState: LazyListState = rememberLazyListState(),
    padding: PaddingValues = PaddingValues(SpaceSmallMax, 0.dp, SpaceSmallMax, SpaceLarge),
) {
    TrackScreenViewEvent(screenName = "ExportProductScreen")

    val text = if (searchText.isEmpty()) NO_ITEMS_IN_PRODUCT else Constants.SEARCH_ITEM_NOT_FOUND
    val title = if (selectedItems.isEmpty()) EXPORT_PRODUCTS_TITLE else "${selectedItems.size} Selected"

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
                    placeholderText = PRODUCT_SEARCH_PLACEHOLDER,
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
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} products will be exported.")

                PoposButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPORT_PRODUCTS_TITLE),
                    enabled = items.isNotEmpty(),
                    text = EXPORT_PRODUCTS_TITLE,
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            CategoryList(
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
                contentPadding = PaddingValues(SpaceSmall),
                lazyRowState = lazyRowState,
                categories = categories,
                doesSelected = selectedCategory::contains,
                onSelect = onSelectCategory,
            )

            if (items.isEmpty()) {
                ItemNotAvailableHalf(
                    text = text,
                    buttonText = ProductTestTags.CREATE_NEW_PRODUCT,
                    onClick = onClickToAddItem,
                )
            } else {
                ProductList(
                    modifier = Modifier,
                    items = items,
                    isInSelectionMode = true,
                    doesSelected = selectedItems::contains,
                    onSelectItem = onSelectItem,
                    lazyListState = lazyListState,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ExportProductScreenContentEmptyPreview() {
    PoposRoomTheme {
        ExportProductScreenContent(
            modifier = Modifier,
            items = persistentListOf(),
            categories = persistentListOf(),
            selectedItems = persistentListOf(),
            selectedCategory = listOf(),
            showSearchBar = false,
            searchText = "",
            onClearClick = {},
            onSearchTextChanged = {},
            onClickOpenSearch = {},
            onClickCloseSearch = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onSelectItem = {},
            onSelectCategory = {},
            onClickExport = {},
            onBackClick = {},
            onClickToAddItem = {},
        )
    }
}

@DevicePreviews
@Composable
private fun ExportProductScreenContentPreview(
    items: ImmutableList<Product> = ProductPreviewData.productList.toImmutableList(),
    categories: ImmutableList<Category> = CategoryPreviewData.categories.toImmutableList(),
) {
    PoposRoomTheme {
        ExportProductScreenContent(
            modifier = Modifier,
            items = items,
            categories = categories,
            selectedCategory = listOf(),
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
            onSelectCategory = {},
            onClickExport = {},
            onBackClick = {},
            onClickToAddItem = {},
        )
    }
}
