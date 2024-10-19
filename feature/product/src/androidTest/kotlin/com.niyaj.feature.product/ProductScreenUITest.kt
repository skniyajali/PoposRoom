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

package com.niyaj.feature.product

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.niyaj.common.tags.ProductTestTags.CREATE_NEW_PRODUCT
import com.niyaj.common.tags.ProductTestTags.PRODUCT_LIST
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NOT_AVAILABLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SCREEN_TITLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.ProductTestTags.PRODUCT_TAG
import com.niyaj.common.utils.Constants.CLEAR_ICON
import com.niyaj.common.utils.Constants.DRAWER_ICON
import com.niyaj.common.utils.Constants.SEARCH_BAR_CLEAR_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_FAB_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_SEARCH_BAR
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.model.searchProducts
import com.niyaj.ui.components.NAV_DELETE_BTN
import com.niyaj.ui.components.NAV_EDIT_BTN
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.NAV_SELECT_ALL_BTN
import com.niyaj.ui.components.NAV_SETTING_BTN
import com.niyaj.ui.event.UiState
import com.niyaj.ui.event.UiState.Empty
import com.niyaj.ui.event.UiState.Loading
import com.niyaj.ui.event.UiState.Success
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.parameterProvider.ProductPreviewData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import org.junit.Rule
import org.junit.Test

class ProductScreenUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val itemList = mutableStateOf(ProductPreviewData.productList)
    private val categoryList = mutableStateOf(CategoryPreviewData.categoryList)

    private val selectedItems = mutableStateListOf<Int>()
    private var showSearchBar = mutableStateOf(false)
    private var searchText = mutableStateOf("")
    private val selectedCategory = mutableStateOf(0)

    @Test
    fun loadingState_whenStateIsLoading_showLoadingIndicator() {
        composeTestRule.apply {
            setContent {
                ProductScreen(uiState = Loading)
            }

            onNodeWithText(PRODUCT_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithContentDescription("loadingIndicator").assertIsDisplayed()
        }
    }

    @Test
    fun emptyState_whenStateIsEmpty_thenShowEmptyState() {
        composeTestRule.setContent {
            ProductScreen(uiState = Empty)
        }

        composeTestRule.onNodeWithText(PRODUCT_NOT_AVAILABLE).assertIsDisplayed()
    }

    @Test
    fun successState_whenStateIsSuccess_thenShowList() {
        composeTestRule.apply {
            setContent {
                ProductScreen(uiState = Success(itemList.value))
            }

            onNodeWithTag(PRODUCT_LIST).assertIsDisplayed()

            // Check first 2 item is displayed or not
            itemList.value.take(2).forEach {
                onNodeWithTag(PRODUCT_TAG.plus(it.productId))
                    .assertIsDisplayed()
                    .assertHasClickAction()
            }

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(STANDARD_FAB_BUTTON)
                .assertIsDisplayed()
                .assertContentDescriptionEquals(CREATE_NEW_PRODUCT)
        }
    }

    @Test
    fun itemSelected_whenPerformLongClick_thenShowSelectionCount() {
        composeTestRule.apply {
            setContent {
                ProductScreen(
                    uiState = Success(itemList.value),
                    selectedItems = listOf(1),
                )
            }

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
        }
    }

    @Test
    fun itemSelected_whenPerformSelectAll_thenShowTotalSelectionCount() {
        composeTestRule.apply {
            setContent {
                ProductScreen(
                    uiState = Success(itemList.value),
                    selectedItems = selectedItems.toList(),
                    onClickSelectAll = {
                        selectAllItems()
                    },
                    onClickDeselect = {
                        selectedItems.clear()
                    },
                    onClickSelectItem = {
                        if (selectedItems.contains(it)) {
                            selectedItems.remove(it)
                        } else {
                            selectedItems.add(it)
                        }
                    },
                )
            }

            onNodeWithTag(PRODUCT_TAG.plus(1))
                .assertIsDisplayed()
                .assertHasClickAction()
                .performTouchInput {
                    longClick()
                }

            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithText("${itemList.value.size} Selected").assertIsDisplayed()
            onNodeWithTag(NAV_EDIT_BTN).assertIsNotDisplayed()
            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed()

            onNodeWithTag(CLEAR_ICON)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithText(PRODUCT_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed()
        }
    }

    @Test
    fun onSearch_whenClickSearch_thenShowSearchBarAndResult() {
        composeTestRule.apply {
            setContent {
                ProductScreen(
                    uiState = Success(itemList.value),
                    searchText = searchText.value,
                    showSearchBar = showSearchBar.value,
                    onClickSearchIcon = {
                        showSearchBar.value = true
                    },
                    onCloseSearchBar = {
                        // Close search bar
                        showSearchBar.value = false
                    },
                    onSearchTextChanged = {
                        onSearchTextChanged(it)
                    },
                )
            }

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertTextContains(PRODUCT_SEARCH_PLACEHOLDER)
                .assertIsDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Chicken")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Composable
    private fun ProductScreen(
        uiState: UiState<List<Product>>,
        categories: ImmutableList<Category> = persistentListOf(),
        selectedCategory: Int = 0,
        selectedItems: List<Int> = emptyList(),
        showSearchBar: Boolean = false,
        searchText: String = "",
        onClickSearchIcon: () -> Unit = {},
        onSearchTextChanged: (String) -> Unit = {},
        onClickClear: () -> Unit = {},
        onCloseSearchBar: () -> Unit = {},
        onClickSelectItem: (Int) -> Unit = {},
        onClickSelectAll: () -> Unit = {},
        onClickDeselect: () -> Unit = {},
        onClickDelete: () -> Unit = {},
        onSelectCategory: (Int) -> Unit = {},
        onClickBack: () -> Unit = {},
        onNavigateToScreen: (String) -> Unit = {},
        onClickCreateNew: () -> Unit = {},
        onClickEdit: (Int) -> Unit = {},
        onClickSettings: () -> Unit = {},
        onNavigateToDetails: (Int) -> Unit = {},
        modifier: Modifier = Modifier,
        snackbarState: SnackbarHostState = remember { SnackbarHostState() },
        scope: CoroutineScope = rememberCoroutineScope(),
        lazyListState: LazyListState = rememberLazyListState(),
        lazyRowState: LazyListState = rememberLazyListState(),
    ) {
        PoposRoomTheme {
            ProductScreenContent(
                uiState = uiState,
                categories = categories,
                selectedCategory = selectedCategory,
                selectedItems = selectedItems,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onClickSearchIcon = onClickSearchIcon,
                onSearchTextChanged = onSearchTextChanged,
                onClickClear = onClickClear,
                onCloseSearchBar = onCloseSearchBar,
                onClickSelectItem = onClickSelectItem,
                onClickSelectAll = onClickSelectAll,
                onClickDeselect = onClickDeselect,
                onClickDelete = onClickDelete,
                onSelectCategory = onSelectCategory,
                onClickBack = onClickBack,
                onNavigateToScreen = onNavigateToScreen,
                onClickCreateNew = onClickCreateNew,
                onClickEdit = onClickEdit,
                onClickSettings = onClickSettings,
                onNavigateToDetails = onNavigateToDetails,
                scope = scope,
                lazyListState = lazyListState,
                lazyRowState = lazyRowState,
                modifier = modifier,
                snackbarState = snackbarState,
            )
        }
    }

    private fun selectAllItems() {
        selectedItems.clear()
        selectedItems.addAll(itemList.value.map { it.productId })
    }

    private fun onSearchTextChanged(text: String) {
        searchText.value += text
        itemList.value = itemList.value.searchProducts(text).toPersistentList()
    }
}
