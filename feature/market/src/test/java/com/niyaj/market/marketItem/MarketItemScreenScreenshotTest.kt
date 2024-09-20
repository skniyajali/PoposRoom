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

package com.niyaj.market.marketItem

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.market.marketItem.createOrUpdate.AddEditMarketItemScreenContent
import com.niyaj.market.marketItem.createOrUpdate.AddEditMarketItemState
import com.niyaj.market.marketItem.settings.ExportMarketItemScreenContent
import com.niyaj.market.marketItem.settings.ImportMarketItemScreenContent
import com.niyaj.market.marketItem.settings.MarketItemSettingsScreenContent
import com.niyaj.model.searchMarketItems
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketItemPreviewData
import com.niyaj.ui.parameterProvider.MarketTypePreviewData
import com.niyaj.ui.parameterProvider.MeasureUnitPreviewData
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.collections.immutable.persistentListOf
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
class MarketItemScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val marketItems = MarketItemPreviewData.marketItems
    private val measureUnits = MeasureUnitPreviewData.measureUnits
    private val marketTypes = MarketTypePreviewData.marketTypeIdAndNames

    @Test
    fun marketItemScreenLoading() {
        composeTestRule.captureForPhone("MarketItemScreenLoading") {
            PoposRoomTheme {
                MarketItemScreenContent(
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
                    onNavigateToListScreen = {},
                )
            }
        }
    }

    @Test
    fun marketItemScreenEmptyContent() {
        composeTestRule.captureForPhone("MarketItemScreenEmptyContent") {
            PoposRoomTheme {
                MarketItemScreenContent(
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
                    onNavigateToListScreen = {},
                )
            }
        }
    }

    @Test
    fun marketItemScreenSuccessContent() {
        composeTestRule.captureForPhone("MarketItemScreenSuccessContent") {
            PoposRoomTheme {
                MarketItemScreenContent(
                    uiState = UiState.Success(marketItems),
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
                    onNavigateToListScreen = {},
                )
            }
        }
    }

    @Test
    fun itemsPopulatedAndSelected() {
        composeTestRule.captureForPhone("MarketItemPopulatedAndSelected") {
            PoposRoomTheme {
                MarketItemScreenContent(
                    uiState = UiState.Success(marketItems),
                    selectedItems = listOf(2, 5, 8),
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
                    onNavigateToListScreen = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("MarketItemShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                MarketItemScreenContent(
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
                    onNavigateToListScreen = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("MarketItemShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                MarketItemScreenContent(
                    uiState = UiState.Success(marketItems.searchMarketItems("Tomatoes")),
                    selectedItems = listOf(),
                    showSearchBar = true,
                    searchText = "Tomatoes",
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
                    onNavigateToListScreen = {},
                )
            }
        }
    }

    @Test
    fun showSettingsBottomSheet() {
        composeTestRule.captureForPhone("MarketItemShowSettingsBottomSheet") {
            PoposRoomTheme {
                MarketItemSettingsScreenContent(
                    onBackClick = {},
                    onNavigateToMarketTypes = {},
                    onNavigateToMeasureUnit = {},
                    onImportMarketType = {},
                    onImportMeasureUnit = {},
                    onImportMarketItem = {},
                    onExportMarketItem = {},
                    onExportMarketType = {},
                    onExportMeasureUnit = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithEmptyData() {
        composeTestRule.captureForPhone("AddEditMarketItemScreenWithEmptyData") {
            PoposRoomTheme {
                AddEditMarketItemScreenContent(
                    state = AddEditMarketItemState(),
                    nameError = "Item name should not be empty",
                    typeError = "Item type should not be empty",
                    unitError = "Item unit should not be empty",
                    amountError = null,
                    typeNames = emptyList(),
                    measureUnits = emptyList(),
                    onEvent = {},
                    onBackClick = {},
                    onClickAddMeasureUnit = {},
                    onClickAddMarketType = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithDummyData() {
        composeTestRule.captureForPhone("AddEditMarketItemScreenWithDummyData") {
            PoposRoomTheme {
                AddEditMarketItemScreenContent(
                    state = AddEditMarketItemState(
                        marketType = marketTypes[0],
                        itemName = "Item Name",
                        itemMeasureUnit = measureUnits[0],
                    ),
                    nameError = null,
                    typeError = null,
                    amountError = null,
                    unitError = null,
                    typeNames = marketTypes,
                    measureUnits = measureUnits,
                    onEvent = {},
                    onBackClick = {},
                    onClickAddMeasureUnit = {},
                    onClickAddMarketType = {},
                )
            }
        }
    }

    @Test
    fun importScreenWithEmptyData() {
        composeTestRule.captureForPhone("MarketItemImportScreenWithEmptyData") {
            PoposRoomTheme {
                ImportMarketItemScreenContent(
                    importedItems = persistentListOf(),
                    selectedItems = persistentListOf(),
                    isLoading = false,
                    onClickSelectItem = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onClickImport = {},
                    onClickOpenFile = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun importScreenWithSomeData() {
        composeTestRule.captureForPhone("MarketItemImportScreenWithSomeData") {
            PoposRoomTheme {
                ImportMarketItemScreenContent(
                    importedItems = marketItems.toImmutableList(),
                    selectedItems = persistentListOf(1, 2, 3),
                    isLoading = false,
                    onClickSelectItem = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onClickImport = {},
                    onClickOpenFile = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun exportScreenWithEmptyData() {
        composeTestRule.captureForPhone("MarketItemExportScreenWithEmptyData") {
            PoposRoomTheme {
                ExportMarketItemScreenContent(
                    items = persistentListOf(),
                    selectedItems = persistentListOf(),
                    isLoading = false,
                    showSearchBar = false,
                    searchText = "",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onClickExport = {},
                    onBackClick = {},
                    onClickToAddItem = {},
                )
            }
        }
    }

    @Test
    fun exportScreenWithSomeData() {
        composeTestRule.captureForPhone("MarketItemExportScreenWithSomeData") {
            PoposRoomTheme {
                ExportMarketItemScreenContent(
                    items = marketItems.toImmutableList(),
                    selectedItems = persistentListOf(1, 3, 6),
                    isLoading = false,
                    showSearchBar = false,
                    searchText = "",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onClickExport = {},
                    onBackClick = {},
                    onClickToAddItem = {},
                )
            }
        }
    }

    @Test
    fun exportScreenPerformSearchAndGetEmptyResult() {
        composeTestRule.captureForPhone("MarketItemExportScreenPerformSearchAndGetEmptyResult") {
            PoposRoomTheme {
                ExportMarketItemScreenContent(
                    items = marketItems
                        .searchMarketItems("text").toImmutableList(),
                    selectedItems = persistentListOf(),
                    isLoading = false,
                    showSearchBar = true,
                    searchText = "text",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onClickExport = {},
                    onBackClick = {},
                    onClickToAddItem = {},
                )
            }
        }
    }

    @Test
    fun exportScreenPerformSearchAndGetSomeResult() {
        composeTestRule.captureForPhone("MarketItemExportScreenPerformSearchAndGetSomeResult") {
            PoposRoomTheme {
                ExportMarketItemScreenContent(
                    items = marketItems
                        .searchMarketItems("Apples").toImmutableList(),
                    selectedItems = persistentListOf(),
                    showSearchBar = true,
                    isLoading = false,
                    searchText = "Apples",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onClickExport = {},
                    onBackClick = {},
                    onClickToAddItem = {},
                )
            }
        }
    }
}
