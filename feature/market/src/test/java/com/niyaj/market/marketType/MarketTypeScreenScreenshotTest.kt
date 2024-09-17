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

package com.niyaj.market.marketType

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.market.marketType.createOrUpdate.AddEditMarketTypeScreenContent
import com.niyaj.market.marketType.createOrUpdate.AddEditMarketTypeState
import com.niyaj.market.marketType.createOrUpdate.defaultListTypes
import com.niyaj.market.marketType.settings.ExportMarketTypeScreenContent
import com.niyaj.market.marketType.settings.ImportMarketTypeScreenContent
import com.niyaj.market.marketType.settings.MarketTypeSettingsScreenContent
import com.niyaj.model.searchMarketType
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MarketTypePreviewData
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
class MarketTypeScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val marketTypes = MarketTypePreviewData.marketTypes

    @Test
    fun marketTypeScreenLoading() {
        composeTestRule.captureForPhone("MarketTypeScreenLoading") {
            PoposRoomTheme {
                MarketTypeScreenContent(
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
                    onClickCreateNewItem = {},
                )
            }
        }
    }

    @Test
    fun marketTypeScreenEmptyContent() {
        composeTestRule.captureForPhone("MarketTypeScreenEmptyContent") {
            PoposRoomTheme {
                MarketTypeScreenContent(
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
                    onClickCreateNewItem = {},
                )
            }
        }
    }

    @Test
    fun marketTypeScreenSuccessContent() {
        composeTestRule.captureForPhone("MarketTypeScreenSuccessContent") {
            PoposRoomTheme {
                MarketTypeScreenContent(
                    uiState = UiState.Success(marketTypes),
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
                    onClickCreateNewItem = {},
                )
            }
        }
    }

    @Test
    fun itemsPopulatedAndSelected() {
        composeTestRule.captureForPhone("MarketTypePopulatedAndSelected") {
            PoposRoomTheme {
                MarketTypeScreenContent(
                    uiState = UiState.Success(marketTypes),
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
                    onClickCreateNewItem = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("MarketTypeShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                MarketTypeScreenContent(
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
                    onClickCreateNewItem = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("MarketTypeShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                MarketTypeScreenContent(
                    uiState = UiState.Success(marketTypes.searchMarketType("Fruits")),
                    selectedItems = listOf(),
                    showSearchBar = true,
                    searchText = "Fruits",
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
                    onClickCreateNewItem = {},
                )
            }
        }
    }

    @Test
    fun showSettingsBottomSheet() {
        composeTestRule.captureForPhone("MarketTypeShowSettingsBottomSheet") {
            PoposRoomTheme {
                MarketTypeSettingsScreenContent(
                    onBackClick = {},
                    onImportClick = {},
                    onExportClick = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithEmptyData() {
        composeTestRule.captureForPhone("AddEditMarketTypeScreenWithEmptyData") {
            PoposRoomTheme {
                AddEditMarketTypeScreenContent(
                    state = AddEditMarketTypeState(),
                    listTypes = defaultListTypes,
                    selectedList = emptyList(),
                    typeError = "Type name should not be empty",
                    listNameError = "List name should not be empty",
                    listTypesError = "At least one type must be selected",
                    onEvent = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithDummyData() {
        composeTestRule.captureForPhone("AddEditMarketTypeScreenWithDummyData") {
            PoposRoomTheme {
                AddEditMarketTypeScreenContent(
                    state = AddEditMarketTypeState(
                        typeName = "Vegetables",
                        typeDesc = "Fresh vegetables",
                        supplierId = 101,
                        listType = "",
                    ),
                    listTypes = defaultListTypes,
                    selectedList = listOf("IN_STOCK", "NEEDED"),
                    typeError = null,
                    listNameError = null,
                    listTypesError = null,
                    onEvent = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun importScreenWithEmptyData() {
        composeTestRule.captureForPhone("MarketTypeImportScreenWithEmptyData") {
            PoposRoomTheme {
                ImportMarketTypeScreenContent(
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
        composeTestRule.captureForPhone("MarketTypeImportScreenWithSomeData") {
            PoposRoomTheme {
                ImportMarketTypeScreenContent(
                    importedItems = marketTypes.toImmutableList(),
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
        composeTestRule.captureForPhone("MarketTypeExportScreenWithEmptyData") {
            PoposRoomTheme {
                ExportMarketTypeScreenContent(
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
        composeTestRule.captureForPhone("MarketTypeExportScreenWithSomeData") {
            PoposRoomTheme {
                ExportMarketTypeScreenContent(
                    items = marketTypes.toImmutableList(),
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
        composeTestRule.captureForPhone("MarketTypeExportScreenPerformSearchAndGetEmptyResult") {
            PoposRoomTheme {
                ExportMarketTypeScreenContent(
                    items = marketTypes
                        .searchMarketType("text").toImmutableList(),
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
        composeTestRule.captureForPhone("MarketTypeExportScreenPerformSearchAndGetSomeResult") {
            PoposRoomTheme {
                ExportMarketTypeScreenContent(
                    items = marketTypes
                        .searchMarketType("Bakery").toImmutableList(),
                    selectedItems = persistentListOf(),
                    showSearchBar = true,
                    isLoading = false,
                    searchText = "Bakery",
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
