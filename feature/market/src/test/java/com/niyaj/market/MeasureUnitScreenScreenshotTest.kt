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

package com.niyaj.market

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.market.measureUnit.MeasureUnitScreenContent
import com.niyaj.market.measureUnit.createOrUpdate.AddEditMeasureUnitScreenContent
import com.niyaj.market.measureUnit.createOrUpdate.AddEditMeasureUnitState
import com.niyaj.market.measureUnit.settings.ExportMeasureUnitScreenContent
import com.niyaj.market.measureUnit.settings.ImportMeasureUnitScreenContent
import com.niyaj.market.measureUnit.settings.MeasureUnitSettingsScreenContent
import com.niyaj.model.searchMeasureUnit
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
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
class MeasureUnitScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val measureUnits = MeasureUnitPreviewData.measureUnits

    @Test
    fun measureUnitScreenLoading() {
        composeTestRule.captureForPhone("MeasureUnitScreenLoading") {
            PoposRoomTheme {
                MeasureUnitScreenContent(
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
                )
            }
        }
    }

    @Test
    fun measureUnitScreenEmptyContent() {
        composeTestRule.captureForPhone("MeasureUnitScreenEmptyContent") {
            PoposRoomTheme {
                MeasureUnitScreenContent(
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
                )
            }
        }
    }

    @Test
    fun measureUnitScreenSuccessContent() {
        composeTestRule.captureForPhone("MeasureUnitScreenSuccessContent") {
            PoposRoomTheme {
                MeasureUnitScreenContent(
                    uiState = UiState.Success(measureUnits),
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
                )
            }
        }
    }

    @Test
    fun itemsPopulatedAndSelected() {
        composeTestRule.captureForPhone("MeasureUnitPopulatedAndSelected") {
            PoposRoomTheme {
                MeasureUnitScreenContent(
                    uiState = UiState.Success(measureUnits),
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
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("MeasureUnitShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                MeasureUnitScreenContent(
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
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("MeasureUnitShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                MeasureUnitScreenContent(
                    uiState = UiState.Success(measureUnits.searchMeasureUnit("Meter")),
                    selectedItems = listOf(),
                    showSearchBar = true,
                    searchText = "Meter",
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
                )
            }
        }
    }

    @Test
    fun showSettingsBottomSheet() {
        composeTestRule.captureForPhone("MeasureUnitShowSettingsBottomSheet") {
            PoposRoomTheme {
                MeasureUnitSettingsScreenContent(
                    onBackClick = {},
                    onImportClick = {},
                    onExportClick = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithEmptyData() {
        composeTestRule.captureForPhone("AddEditMeasureUnitScreenWithEmptyData") {
            PoposRoomTheme {
                AddEditMeasureUnitScreenContent(
                    state = AddEditMeasureUnitState(),
                    nameError = "Unit name should not be empty",
                    valueError = "Unit value should not be empty",
                    onEvent = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithDummyData() {
        composeTestRule.captureForPhone("AddEditMeasureUnitScreenWithDummyData") {
            PoposRoomTheme {
                AddEditMeasureUnitScreenContent(
                    state = AddEditMeasureUnitState(
                        unitName = "li",
                        unitValue = "0.5",
                    ),
                    nameError = null,
                    valueError = null,
                    onEvent = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun importScreenWithEmptyData() {
        composeTestRule.captureForPhone("MeasureUnitImportScreenWithEmptyData") {
            PoposRoomTheme {
                ImportMeasureUnitScreenContent(
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
        composeTestRule.captureForPhone("MeasureUnitImportScreenWithSomeData") {
            PoposRoomTheme {
                ImportMeasureUnitScreenContent(
                    importedItems = measureUnits.toImmutableList(),
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
        composeTestRule.captureForPhone("MeasureUnitExportScreenWithEmptyData") {
            PoposRoomTheme {
                ExportMeasureUnitScreenContent(
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
        composeTestRule.captureForPhone("MeasureUnitExportScreenWithSomeData") {
            PoposRoomTheme {
                ExportMeasureUnitScreenContent(
                    items = measureUnits.toImmutableList(),
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
        composeTestRule.captureForPhone("MeasureUnitExportScreenPerformSearchAndGetEmptyResult") {
            PoposRoomTheme {
                ExportMeasureUnitScreenContent(
                    items = measureUnits
                        .searchMeasureUnit("text").toImmutableList(),
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
        composeTestRule.captureForPhone("MeasureUnitExportScreenPerformSearchAndGetSomeResult") {
            PoposRoomTheme {
                ExportMeasureUnitScreenContent(
                    items = measureUnits
                        .searchMeasureUnit("Inch").toImmutableList(),
                    selectedItems = persistentListOf(),
                    showSearchBar = true,
                    isLoading = false,
                    searchText = "Inch",
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
