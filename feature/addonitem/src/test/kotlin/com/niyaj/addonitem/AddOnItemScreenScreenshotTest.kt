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

package com.niyaj.addonitem

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.addonitem.createOrUpdate.AddEditAddOnItemScreenContent
import com.niyaj.addonitem.createOrUpdate.AddEditAddOnItemState
import com.niyaj.addonitem.settings.AddOnExportScreenContent
import com.niyaj.addonitem.settings.AddOnImportScreenContent
import com.niyaj.addonitem.settings.AddOnSettingsScreenContent
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.searchAddOnItem
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddOnPreviewData
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
class AddOnItemScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val addOnItemList = AddOnPreviewData.addOnItemList

    @Test
    fun addOnItemScreenLoading() {
        composeTestRule.captureForPhone("AddOnItemScreenLoading") {
            PoposRoomTheme {
                AddOnItemScreenContent(
                    uiState = UiState.Loading,
                    selectedItems = listOf(),
                    showSearchBar = false,
                    searchText = "",
                    onItemClick = {},
                    onCreateNewClick = {},
                    onEditClick = {},
                    onDeleteClick = {},
                    onSettingsClick = {},
                    onSelectAllClick = {},
                    onClearSearchClick = {},
                    onSearchClick = {},
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onBackClick = {},
                    onDeselect = {},
                    onNavigateToScreen = {},
                )
            }
        }
    }

    @Test
    fun addOnItemScreenEmptyContent() {
        composeTestRule.captureForPhone("AddOnItemScreenEmptyContent") {
            PoposRoomTheme {
                AddOnItemScreenContent(
                    uiState = UiState.Empty,
                    selectedItems = listOf(),
                    showSearchBar = false,
                    searchText = "",
                    onItemClick = {},
                    onCreateNewClick = {},
                    onEditClick = {},
                    onDeleteClick = {},
                    onSettingsClick = {},
                    onSelectAllClick = {},
                    onClearSearchClick = {},
                    onSearchClick = {},
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onBackClick = {},
                    onDeselect = {},
                    onNavigateToScreen = {},
                )
            }
        }
    }

    @Test
    fun addOnItemScreenSuccessContent() {
        composeTestRule.captureForPhone("AddOnItemScreenSuccessContent") {
            PoposRoomTheme {
                AddOnItemScreenContent(
                    uiState = UiState.Success(addOnItemList),
                    selectedItems = listOf(),
                    showSearchBar = false,
                    searchText = "",
                    onItemClick = {},
                    onCreateNewClick = {},
                    onEditClick = {},
                    onDeleteClick = {},
                    onSettingsClick = {},
                    onSelectAllClick = {},
                    onClearSearchClick = {},
                    onSearchClick = {},
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onBackClick = {},
                    onDeselect = {},
                    onNavigateToScreen = {},
                )
            }
        }
    }

    @Test
    fun itemsPopulatedAndSelected() {
        composeTestRule.captureForPhone("ItemsPopulatedAndSelected") {
            PoposRoomTheme {
                AddOnItemScreenContent(
                    uiState = UiState.Success(addOnItemList),
                    selectedItems = listOf(1, 3, 5),
                    showSearchBar = false,
                    searchText = "",
                    onItemClick = {},
                    onCreateNewClick = {},
                    onEditClick = {},
                    onDeleteClick = {},
                    onSettingsClick = {},
                    onSelectAllClick = {},
                    onClearSearchClick = {},
                    onSearchClick = {},
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onBackClick = {},
                    onDeselect = {},
                    onNavigateToScreen = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                AddOnItemScreenContent(
                    uiState = UiState.Empty,
                    selectedItems = listOf(),
                    showSearchBar = true,
                    searchText = "search",
                    onItemClick = {},
                    onCreateNewClick = {},
                    onEditClick = {},
                    onDeleteClick = {},
                    onSettingsClick = {},
                    onSelectAllClick = {},
                    onClearSearchClick = {},
                    onSearchClick = {},
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onBackClick = {},
                    onDeselect = {},
                    onNavigateToScreen = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                AddOnItemScreenContent(
                    uiState = UiState.Success(
                        addOnItemList.searchAddOnItem("Extra"),
                    ),
                    selectedItems = listOf(),
                    showSearchBar = true,
                    searchText = "Extra",
                    onItemClick = {},
                    onCreateNewClick = {},
                    onEditClick = {},
                    onDeleteClick = {},
                    onSettingsClick = {},
                    onSelectAllClick = {},
                    onClearSearchClick = {},
                    onSearchClick = {},
                    onSearchTextChanged = {},
                    onCloseSearchBar = {},
                    onBackClick = {},
                    onDeselect = {},
                    onNavigateToScreen = {},
                )
            }
        }
    }

    @Test
    fun showSettingsBottomSheet() {
        composeTestRule.captureForPhone("ShowSettingsBottomSheet") {
            PoposRoomTheme {
                AddOnSettingsScreenContent()
            }
        }
    }

    @Test
    fun addEditItemScreenWithEmptyData() {
        composeTestRule.captureForPhone("AddEditItemScreenWithEmptyData") {
            PoposRoomTheme {
                AddEditAddOnItemScreenContent(
                    state = AddEditAddOnItemState(),
                    nameError = "Item name should not empty",
                    priceError = "Item price should not empty",
                    onEvent = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithDummyData() {
        composeTestRule.captureForPhone("AddEditItemScreenWithDummyData") {
            PoposRoomTheme {
                AddEditAddOnItemScreenContent(
                    state = AddEditAddOnItemState(
                        itemName = "New Item",
                        itemPrice = 20,
                        isApplicable = true,
                    ),
                    onEvent = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun importScreenWithEmptyData() {
        composeTestRule.captureForPhone("ImportScreenWithEmptyData") {
            PoposRoomTheme {
                AddOnImportScreenContent(
                    importedItems = persistentListOf(),
                    selectedItems = persistentListOf(),
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
        composeTestRule.captureForPhone("ImportScreenWithSomeData") {
            PoposRoomTheme {
                AddOnImportScreenContent(
                    importedItems = addOnItemList.toImmutableList(),
                    selectedItems = persistentListOf(),
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
        composeTestRule.captureForPhone("ExportScreenWithEmptyData") {
            PoposRoomTheme {
                AddOnExportScreenContent(
                    addOnItems = persistentListOf(),
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
                    onClickExport = {},
                    onBackClick = {},
                    onClickToAddItem = {},
                )
            }
        }
    }

    @Test
    fun exportScreenWithSomeData() {
        composeTestRule.captureForPhone("ExportScreenWithSomeData") {
            PoposRoomTheme {
                AddOnExportScreenContent(
                    addOnItems = addOnItemList.toImmutableList(),
                    selectedItems = persistentListOf(1, 3, 6),
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
        composeTestRule.captureForPhone("ExportScreenPerformSearchAndGetEmptyResult") {
            PoposRoomTheme {
                AddOnExportScreenContent(
                    addOnItems = addOnItemList
                        .searchAddOnItem("search").toImmutableList(),
                    selectedItems = persistentListOf(),
                    showSearchBar = true,
                    searchText = "search",
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
        composeTestRule.captureForPhone("ExportScreenPerformSearchAndGetSomeResult") {
            PoposRoomTheme {
                AddOnExportScreenContent(
                    addOnItems = addOnItemList
                        .searchAddOnItem("extra").toImmutableList(),
                    selectedItems = persistentListOf(),
                    showSearchBar = true,
                    searchText = "extra",
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
