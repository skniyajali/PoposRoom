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

package com.niyaj.market.marketList

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.market.marketList.createOrUpdate.AddEditMarketListScreenContent
import com.niyaj.model.searchMarketList
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketListPreviewData
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock
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
class MarketListScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val marketListWithTypes = MarketListPreviewData.marketListWithTypes
    private val marketTypeAndList = MarketListPreviewData.marketTypeIdAndListTypes

    @Test
    fun marketListScreenLoading() {
        composeTestRule.captureForPhone("MarketListScreenLoading") {
            PoposRoomTheme {
                MarketListScreenContent(
                    uiState = UiState.Loading,
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
                    onClickBack = {},
                    onNavigateToScreen = {},
                    onClickCreateNew = {},
                    onClickEdit = {},
                    onClickSettings = {},
                    doesExpanded = { false },
                    onClickExpand = {},
                    onClickShare = { _, _ -> },
                    onClickPrint = { _, _ -> },
                    onClickViewDetails = {},
                    onClickManageList = {},
                )
            }
        }
    }

    @Test
    fun marketListScreenEmptyContent() {
        composeTestRule.captureForPhone("MarketListScreenEmptyContent") {
            PoposRoomTheme {
                MarketListScreenContent(
                    uiState = UiState.Empty,
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
                    onClickBack = {},
                    onNavigateToScreen = {},
                    onClickCreateNew = {},
                    onClickEdit = {},
                    onClickSettings = {},
                    doesExpanded = { false },
                    onClickExpand = {},
                    onClickShare = { _, _ -> },
                    onClickPrint = { _, _ -> },
                    onClickViewDetails = {},
                    onClickManageList = {},
                )
            }
        }
    }

    @Test
    fun marketListScreenSuccessContent() {
        composeTestRule.captureForPhone("MarketListScreenSuccessContent") {
            PoposRoomTheme {
                MarketListScreenContent(
                    uiState = UiState.Success(marketListWithTypes),
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
                    onClickBack = {},
                    onNavigateToScreen = {},
                    onClickCreateNew = {},
                    onClickEdit = {},
                    onClickSettings = {},
                    doesExpanded = { it % 2 == 0 },
                    onClickExpand = {},
                    onClickShare = { _, _ -> },
                    onClickPrint = { _, _ -> },
                    onClickViewDetails = {},
                    onClickManageList = {},
                )
            }
        }
    }

    @Test
    fun itemsPopulatedAndSelected() {
        composeTestRule.captureForPhone("MarketListPopulatedAndSelected") {
            PoposRoomTheme {
                MarketListScreenContent(
                    uiState = UiState.Success(marketListWithTypes),
                    selectedItems = listOf(2),
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
                    onClickBack = {},
                    onNavigateToScreen = {},
                    onClickCreateNew = {},
                    onClickEdit = {},
                    onClickSettings = {},
                    doesExpanded = { false },
                    onClickExpand = {},
                    onClickShare = { _, _ -> },
                    onClickPrint = { _, _ -> },
                    onClickViewDetails = {},
                    onClickManageList = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("MarketListShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                MarketListScreenContent(
                    uiState = UiState.Empty,
                    selectedItems = listOf(),
                    showSearchBar = true,
                    searchText = "search",
                    onClickSearchIcon = {},
                    onSearchTextChanged = {},
                    onClickClear = {},
                    onCloseSearchBar = {},
                    onClickSelectItem = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onClickDelete = {},
                    onClickBack = {},
                    onNavigateToScreen = {},
                    onClickCreateNew = {},
                    onClickEdit = {},
                    onClickSettings = {},
                    doesExpanded = { false },
                    onClickExpand = {},
                    onClickShare = { _, _ -> },
                    onClickPrint = { _, _ -> },
                    onClickViewDetails = {},
                    onClickManageList = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("MarketListShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                MarketListScreenContent(
                    uiState = UiState.Success(marketListWithTypes.searchMarketList("12")),
                    selectedItems = listOf(),
                    showSearchBar = true,
                    searchText = "12",
                    onClickSearchIcon = {},
                    onSearchTextChanged = {},
                    onClickClear = {},
                    onCloseSearchBar = {},
                    onClickSelectItem = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onClickDelete = {},
                    onClickBack = {},
                    onNavigateToScreen = {},
                    onClickCreateNew = {},
                    onClickEdit = {},
                    onClickSettings = {},
                    doesExpanded = { false },
                    onClickExpand = {},
                    onClickShare = { _, _ -> },
                    onClickPrint = { _, _ -> },
                    onClickViewDetails = {},
                    onClickManageList = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithEmptyData() {
        composeTestRule.captureForPhone("AddEditMarketListScreenWithEmptyData") {
            PoposRoomTheme {
                AddEditMarketListScreenContent(
                    listTypes = marketTypeAndList.toImmutableList(),
                    selectedDate = Clock.System.now().toEpochMilliseconds().toString(),
                    isError = true,
                    onDateChange = {},
                    isTypeChecked = { false },
                    isListTypeChecked = { _, _ -> false },
                    onListTypeClick = { _, _ -> },
                    onCreateOrUpdateClick = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithDummyData() {
        composeTestRule.captureForPhone("AddEditMarketListScreenWithDummyData") {
            PoposRoomTheme {
                AddEditMarketListScreenContent(
                    listTypes = marketTypeAndList.toImmutableList(),
                    selectedDate = Clock.System.now().toEpochMilliseconds().toString(),
                    isError = false,
                    onDateChange = {},
                    isTypeChecked = { it % 2 == 0 },
                    isListTypeChecked = { _, _ -> true },
                    onListTypeClick = { _, _ -> },
                    onCreateOrUpdateClick = {},
                    onBackClick = {},
                )
            }
        }
    }
}
