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

package com.niyaj.feature.market.marketListItem

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.searchMarketType
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketItemAndQuantityData
import com.niyaj.ui.parameterProvider.MarketItemAndQuantityData.maretListAndType
import dagger.hilt.android.testing.HiltTestApplication
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
class MarketListItemScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val marketItemAndQuantity = MarketItemAndQuantityData.marketItemsAndQuantity

    @Test
    fun marketListItemScreenLoading() {
        composeTestRule.captureForPhone("MarketListItemScreenLoading") {
            PoposRoomTheme {
                MarketListItemScreenContent(
                    uiState = UiState.Loading,
                    marketDetails = maretListAndType,
                    showSearchBar = false,
                    searchText = "",
                    onClickSearchIcon = {},
                    onSearchTextChanged = {},
                    onClickClear = {},
                    onCloseSearchBar = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onAddItem = {},
                    onRemoveItem = {},
                    onDecreaseQuantity = {},
                    onIncreaseQuantity = {},
                    onClickBack = {},
                    onClickCreateNew = {},
                )
            }
        }
    }

    @Test
    fun marketListItemScreenEmptyContent() {
        composeTestRule.captureForPhone("MarketListItemScreenEmptyContent") {
            PoposRoomTheme {
                MarketListItemScreenContent(
                    uiState = UiState.Empty,
                    marketDetails = maretListAndType,
                    showSearchBar = false,
                    searchText = "",
                    onClickSearchIcon = {},
                    onSearchTextChanged = {},
                    onClickClear = {},
                    onCloseSearchBar = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onAddItem = {},
                    onRemoveItem = {},
                    onDecreaseQuantity = {},
                    onIncreaseQuantity = {},
                    onClickBack = {},
                    onClickCreateNew = {},
                )
            }
        }
    }

    @Test
    fun marketListItemScreenSuccessContent() {
        composeTestRule.captureForPhone("MarketListItemScreenSuccessContent") {
            PoposRoomTheme {
                MarketListItemScreenContent(
                    uiState = UiState.Success(marketItemAndQuantity),
                    marketDetails = maretListAndType,
                    showSearchBar = false,
                    searchText = "",
                    onClickSearchIcon = {},
                    onSearchTextChanged = {},
                    onClickClear = {},
                    onCloseSearchBar = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onAddItem = {},
                    onRemoveItem = {},
                    onDecreaseQuantity = {},
                    onIncreaseQuantity = {},
                    onClickBack = {},
                    onClickCreateNew = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("MarketListItemShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                MarketListItemScreenContent(
                    uiState = UiState.Success(marketItemAndQuantity.searchMarketType("search")),
                    marketDetails = maretListAndType,
                    showSearchBar = true,
                    searchText = "search",
                    onClickSearchIcon = {},
                    onSearchTextChanged = {},
                    onClickClear = {},
                    onCloseSearchBar = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onAddItem = {},
                    onRemoveItem = {},
                    onDecreaseQuantity = {},
                    onIncreaseQuantity = {},
                    onClickBack = {},
                    onClickCreateNew = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("MarketListItemShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                MarketListItemScreenContent(
                    uiState = UiState.Success(marketItemAndQuantity.searchMarketType("Tomatoes")),
                    marketDetails = maretListAndType,
                    showSearchBar = true,
                    searchText = "Tomatoes",
                    onClickSearchIcon = {},
                    onSearchTextChanged = {},
                    onClickClear = {},
                    onCloseSearchBar = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onAddItem = {},
                    onRemoveItem = {},
                    onDecreaseQuantity = {},
                    onIncreaseQuantity = {},
                    onClickBack = {},
                    onClickCreateNew = {},
                )
            }
        }
    }
}
