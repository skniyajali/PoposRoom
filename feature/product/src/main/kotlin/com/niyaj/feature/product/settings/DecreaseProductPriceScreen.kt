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

package com.niyaj.feature.product.settings

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.tags.ProductTestTags.DECREASE_PRODUCTS_TITLE
import com.niyaj.common.tags.ProductTestTags.NO_ITEMS_IN_PRODUCT
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.safeString
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.feature.product.components.ProductList
import com.niyaj.feature.product.destinations.AddEditProductScreenDestination
import com.niyaj.feature.product.settings.ProductSettingsEvent.OnChangeProductPrice
import com.niyaj.feature.product.settings.ProductSettingsEvent.OnDecreaseProductPrice
import com.niyaj.feature.product.settings.ProductSettingsEvent.OnSelectCategory
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.ui.components.CategoryList
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.components.StandardTextField
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
fun DecreaseProductPriceScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    viewModel: ProductSettingsViewModel = hiltViewModel(),
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val selectedItems = viewModel.selectedItems.toList()
    val selectedCategory = viewModel.selectedCategory.toList()
    val searchText = viewModel.searchText.value
    val productPrice = viewModel.productPrice.value.safeString

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

    DecreaseProductPriceScreenContent(
        modifier = modifier,
        items = products.toImmutableList(),
        categories = categories,
        selectedItems = selectedItems.toImmutableList(),
        selectedCategories = selectedCategory,
        productPrice = productPrice,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onClearClick = viewModel::clearSearchText,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClickOpenSearch = viewModel::openSearchBar,
        onClickCloseSearch = viewModel::closeSearchBar,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onSelectItem = viewModel::selectItem,
        onSelectCategory = {
            viewModel.onEvent(OnSelectCategory(it))
        },
        onDecreaseClick = {
            viewModel.onEvent(OnDecreaseProductPrice)
        },
        onPriceChanged = {
            viewModel.onEvent(OnChangeProductPrice(it))
        },
        onBackClick = navigator::navigateUp,
        onClickToAddItem = {
            navigator.navigate(AddEditProductScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
internal fun DecreaseProductPriceScreenContent(
    items: ImmutableList<Product>,
    categories: ImmutableList<Category>,
    selectedItems: ImmutableList<Int>,
    selectedCategories: List<Int>,
    productPrice: String,
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
    onPriceChanged: (String) -> Unit,
    onDecreaseClick: () -> Unit,
    onBackClick: () -> Unit,
    onClickToAddItem: () -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    lazyRowState: LazyListState = rememberLazyListState(),
    padding: PaddingValues = PaddingValues(SpaceSmallMax, 0.dp, SpaceSmallMax, SpaceLarge),
) {
    TrackScreenViewEvent(screenName = "DecreaseProductPriceScreen")

    val text = if (searchText.isEmpty()) NO_ITEMS_IN_PRODUCT else Constants.SEARCH_ITEM_NOT_FOUND
    val title =
        if (selectedItems.isEmpty()) DECREASE_PRODUCTS_TITLE else "${selectedItems.size} Selected"

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
        title = title,
        onBackClick = if (showSearchBar) onClickCloseSearch else onBackClick,
        modifier = modifier,
        showBackButton = selectedItems.isEmpty() || showSearchBar,
        showBottomBar = items.isNotEmpty(),
        showSecondaryBottomBar = true,
        fabPosition = FabPosition.End,
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
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(
                    text = "${
                        if (selectedItems.isEmpty()) {
                            "All"
                        } else {
                            "${selectedItems.size}"
                        }
                    } product price will be decrease.",
                )

                PoposButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(DECREASE_PRODUCTS_TITLE),
                    enabled = items.isNotEmpty() && productPrice.isNotEmpty(),
                    text = DECREASE_PRODUCTS_TITLE,
                    icon = PoposIcons.Upload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                    onClick = onDecreaseClick,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            StandardTextField(
                label = ProductTestTags.PRODUCT_PRICE_TEXT_FIELD,
                leadingIcon = PoposIcons.Rupee,
                value = productPrice,
                onValueChange = onPriceChanged,
                modifier = Modifier,
                isError = productPrice.isEmpty(),
                keyboardType = KeyboardType.Number,
            )

            CategoryList(
                categories = categories,
                selected = selectedCategories::contains,
                onSelect = onSelectCategory,
                contentPadding = PaddingValues(SpaceSmall),
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
                lazyRowState = lazyRowState,
            )

            if (items.isEmpty()) {
                ItemNotAvailableHalf(
                    text = text,
                    buttonText = ProductTestTags.CREATE_NEW_PRODUCT,
                    onClick = onClickToAddItem,
                )
            } else {
                ProductList(
                    items = items,
                    isInSelectionMode = true,
                    doesSelected = selectedItems::contains,
                    onSelectItem = onSelectItem,
                    modifier = Modifier,
                    lazyListState = lazyListState,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun DecreaseProductPriceScreenContentEmptyPreview(
    categories: ImmutableList<Category> = CategoryPreviewData.categories.toImmutableList(),
) {
    PoposRoomTheme {
        DecreaseProductPriceScreenContent(
            modifier = Modifier,
            items = persistentListOf(),
            categories = categories,
            selectedItems = persistentListOf(),
            productPrice = "",
            showSearchBar = false,
            searchText = "",
            onClearClick = {},
            onSearchTextChanged = {},
            onClickOpenSearch = {},
            onClickCloseSearch = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onSelectItem = {},
            onDecreaseClick = {},
            onBackClick = {},
            onClickToAddItem = {},
            selectedCategories = listOf(),
            onSelectCategory = {},
            onPriceChanged = {},
        )
    }
}

@DevicePreviews
@Composable
private fun DecreaseProductPriceScreenContentPreview(
    items: ImmutableList<Product> = ProductPreviewData.productList.toImmutableList(),
    categories: ImmutableList<Category> = CategoryPreviewData.categories.toImmutableList(),
) {
    PoposRoomTheme {
        DecreaseProductPriceScreenContent(
            modifier = Modifier,
            items = items,
            categories = categories,
            selectedItems = persistentListOf(),
            productPrice = "",
            showSearchBar = false,
            searchText = "",
            onClearClick = {},
            onSearchTextChanged = {},
            onClickOpenSearch = {},
            onClickCloseSearch = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onSelectItem = {},
            onDecreaseClick = {},
            onBackClick = {},
            onClickToAddItem = {},
            selectedCategories = listOf(),
            onSelectCategory = {},
            onPriceChanged = {},
        )
    }
}
