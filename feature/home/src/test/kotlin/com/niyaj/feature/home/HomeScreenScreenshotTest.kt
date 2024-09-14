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

package com.niyaj.feature.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.filterByCategory
import com.niyaj.model.filterBySearch
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
import com.niyaj.ui.event.UiState.Loading
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.parameterProvider.ProductPreviewData
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.collections.immutable.toImmutableList
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class)
@LooperMode(LooperMode.Mode.PAUSED)
class HomeScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val productList = ProductPreviewData.productWithQuantityList.toImmutableList()
    private val emptyQtyList = ProductPreviewData.emptyProductQtyList.toImmutableList()
    private val categoryList = CategoryPreviewData.categoryList

    @Test
    fun homeScreenLoading() {
        composeTestRule.captureForPhone("HomeScreenLoading") {
            PoposRoomTheme {
                HomeScreenContent(
                    productState = Loading,
                    categoryState = Loading,
                    showSearchBar = false,
                    searchText = "",
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onNavigateToScreen = {},
                    selectedCategory = 0,
                    selectedId = "0",
                    onOpenSearchBar = {},
                    onClearClick = {},
                    onSelectCategory = {},
                    onIncreaseQuantity = {},
                    onDecreaseQuantity = {},
                    onClickScrollToTop = {},
                    onClickCreateProduct = {},
                    onCartClick = {},
                    onOrderClick = {},
                    showKonfetti = false,
                    hideKonfetti = {},
                )
            }
        }
    }

    @Test
    fun homeScreenEmptyContent() {
        composeTestRule.captureForPhone("HomeScreenEmptyContent") {
            PoposRoomTheme {
                HomeScreenContent(
                    productState = UiState.Empty,
                    categoryState = UiState.Empty,
                    showSearchBar = false,
                    searchText = "",
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onNavigateToScreen = {},
                    selectedCategory = 0,
                    selectedId = "0",
                    onOpenSearchBar = {},
                    onClearClick = {},
                    onSelectCategory = {},
                    onIncreaseQuantity = {},
                    onDecreaseQuantity = {},
                    onClickScrollToTop = {},
                    onClickCreateProduct = {},
                    onCartClick = {},
                    onOrderClick = {},
                    showKonfetti = false,
                    hideKonfetti = {},
                )
            }
        }
    }

    @Test
    fun homeScreenSuccessContent() {
        composeTestRule.captureForPhone("HomeScreenSuccessContent") {
            PoposRoomTheme {
                HomeScreenContent(
                    productState = UiState.Success(emptyQtyList),
                    categoryState = UiState.Success(categoryList),
                    showSearchBar = false,
                    searchText = "",
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onNavigateToScreen = {},
                    selectedCategory = 0,
                    selectedId = "0",
                    onOpenSearchBar = {},
                    onClearClick = {},
                    onSelectCategory = {},
                    onIncreaseQuantity = {},
                    onDecreaseQuantity = {},
                    onClickScrollToTop = {},
                    onClickCreateProduct = {},
                    onCartClick = {},
                    onOrderClick = {},
                    showKonfetti = false,
                    hideKonfetti = {},
                )
            }
        }
    }

    @Test
    fun itemsPopulatedAndAdded() {
        composeTestRule.captureForPhone("ItemsPopulatedAndAdded") {
            PoposRoomTheme {
                HomeScreenContent(
                    productState = UiState.Success(productList),
                    categoryState = UiState.Success(categoryList),
                    showSearchBar = false,
                    searchText = "",
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onNavigateToScreen = {},
                    selectedCategory = 0,
                    selectedId = "1",
                    onOpenSearchBar = {},
                    onClearClick = {},
                    onSelectCategory = {},
                    onIncreaseQuantity = {},
                    onDecreaseQuantity = {},
                    onClickScrollToTop = {},
                    onClickCreateProduct = {},
                    onCartClick = {},
                    onOrderClick = {},
                    showKonfetti = false,
                    hideKonfetti = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                HomeScreenContent(
                    productState = UiState.Empty,
                    categoryState = UiState.Empty,
                    showSearchBar = true,
                    searchText = "search",
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onNavigateToScreen = {},
                    selectedCategory = 0,
                    selectedId = "",
                    onOpenSearchBar = {},
                    onClearClick = {},
                    onSelectCategory = {},
                    onIncreaseQuantity = {},
                    onDecreaseQuantity = {},
                    onClickScrollToTop = {},
                    onClickCreateProduct = {},
                    onCartClick = {},
                    onOrderClick = {},
                    showKonfetti = false,
                    hideKonfetti = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                val data = emptyQtyList.filterBySearch("50").toImmutableList()
                HomeScreenContent(
                    productState = UiState.Success(data),
                    categoryState = UiState.Success(categoryList),
                    showSearchBar = true,
                    searchText = "50",
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onNavigateToScreen = {},
                    selectedCategory = 0,
                    selectedId = "",
                    onOpenSearchBar = {},
                    onClearClick = {},
                    onSelectCategory = {},
                    onIncreaseQuantity = {},
                    onDecreaseQuantity = {},
                    onClickScrollToTop = {},
                    onClickCreateProduct = {},
                    onCartClick = {},
                    onOrderClick = {},
                    showKonfetti = false,
                    hideKonfetti = {},
                )
            }
        }
    }

    @Test
    fun onSelectCategoryOnlySelectedCategoryProductsWillVisible() {
        composeTestRule.captureForPhone("OnSelectCategory") {
            PoposRoomTheme {
                val data = emptyQtyList.filterByCategory(2).toImmutableList()
                HomeScreenContent(
                    productState = UiState.Success(data),
                    categoryState = UiState.Success(categoryList),
                    showSearchBar = false,
                    searchText = "",
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onNavigateToScreen = {},
                    selectedCategory = 2,
                    selectedId = "",
                    onOpenSearchBar = {},
                    onClearClick = {},
                    onSelectCategory = {},
                    onIncreaseQuantity = {},
                    onDecreaseQuantity = {},
                    onClickScrollToTop = {},
                    onClickCreateProduct = {},
                    onCartClick = {},
                    onOrderClick = {},
                    showKonfetti = false,
                    hideKonfetti = {},
                )
            }
        }
    }
}
