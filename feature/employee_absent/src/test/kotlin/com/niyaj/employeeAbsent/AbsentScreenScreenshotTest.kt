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

package com.niyaj.employeeAbsent

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.employeeAbsent.createOrUpdate.AddEditAbsentScreenContent
import com.niyaj.employeeAbsent.createOrUpdate.AddEditAbsentState
import com.niyaj.employeeAbsent.settings.AbsentExportScreenContent
import com.niyaj.employeeAbsent.settings.AbsentImportScreenContent
import com.niyaj.employeeAbsent.settings.AbsentSettingsScreenContent
import com.niyaj.model.Employee
import com.niyaj.model.searchAbsentees
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AbsentPreviewData
import com.niyaj.ui.parameterProvider.EmployeePreviewData
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
class AbsentScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val absentsList = AbsentPreviewData.employeesWithAbsents
    private val employeeList: List<Employee> = EmployeePreviewData.employeeList

    @Test
    fun absentScreenLoading() {
        composeTestRule.captureForPhone("AbsentScreenLoading") {
            PoposRoomTheme {
                AbsentScreenContent(
                    uiState = UiState.Loading,
                    selectedItems = listOf(),
                    selectedEmployees = listOf(),
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
                    onSelectEmployee = {},
                    onAbsentAddClick = {},
                )
            }
        }
    }

    @Test
    fun absentScreenEmptyContent() {
        composeTestRule.captureForPhone("AbsentScreenEmptyContent") {
            PoposRoomTheme {
                AbsentScreenContent(
                    uiState = UiState.Empty,
                    selectedItems = listOf(),
                    selectedEmployees = listOf(),
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
                    onSelectEmployee = {},
                    onAbsentAddClick = {},
                )
            }
        }
    }

    @Test
    fun absentScreenSuccessContent() {
        composeTestRule.captureForPhone("AbsentScreenSuccessContent") {
            PoposRoomTheme {
                AbsentScreenContent(
                    uiState = UiState.Success(absentsList),
                    selectedItems = listOf(),
                    selectedEmployees = listOf(1, 2, 3),
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
                    onSelectEmployee = {},
                    onAbsentAddClick = {},
                )
            }
        }
    }

    @Test
    fun itemsPopulatedAndSelected() {
        composeTestRule.captureForPhone("ItemsPopulatedAndSelected") {
            PoposRoomTheme {
                AbsentScreenContent(
                    uiState = UiState.Success(absentsList),
                    selectedItems = listOf(2, 5, 8),
                    selectedEmployees = listOf(1, 2, 3),
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
                    onSelectEmployee = {},
                    onAbsentAddClick = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                AbsentScreenContent(
                    uiState = UiState.Empty,
                    selectedItems = listOf(),
                    selectedEmployees = listOf(),
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
                    onSelectEmployee = {},
                    onAbsentAddClick = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                AbsentScreenContent(
                    uiState = UiState.Success(absentsList.searchAbsentees("Personal")),
                    selectedItems = listOf(),
                    selectedEmployees = listOf(1),
                    showSearchBar = true,
                    searchText = "Personal",
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
                    onSelectEmployee = {},
                    onAbsentAddClick = {},
                )
            }
        }
    }

    @Test
    fun showSettingsBottomSheet() {
        composeTestRule.captureForPhone("ShowSettingsBottomSheet") {
            PoposRoomTheme {
                AbsentSettingsScreenContent(
                    onBackClick = {},
                    onImportClick = {},
                    onExportClick = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithEmptyData() {
        composeTestRule.captureForPhone("AddEditItemScreenWithEmptyData") {
            PoposRoomTheme {
                AddEditAbsentScreenContent(
                    employees = emptyList(),
                    state = AddEditAbsentState(),
                    selectedEmployee = Employee(),
                    onEvent = {},
                    onBackClick = {},
                    onClickAddEmployee = {},
                    employeeError = "Employee name should not empty",
                    dateError = "Absent date should not empty",
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithDummyData() {
        composeTestRule.captureForPhone("AddEditItemScreenWithDummyData") {
            PoposRoomTheme {
                AddEditAbsentScreenContent(
                    employees = employeeList,
                    state = AddEditAbsentState(
                        absentReason = "Sick Leave",
                    ),
                    selectedEmployee = employeeList.first(),
                    onEvent = {},
                    onBackClick = {},
                    onClickAddEmployee = {},
                )
            }
        }
    }

    @Test
    fun importScreenWithEmptyData() {
        composeTestRule.captureForPhone("ImportScreenWithEmptyData") {
            PoposRoomTheme {
                AbsentImportScreenContent(
                    importedItems = persistentListOf(),
                    selectedItems = persistentListOf(),
                    selectedEmployees = emptyList(),
                    isLoading = false,
                    onSelectEmployee = {},
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
                AbsentImportScreenContent(
                    importedItems = absentsList.toImmutableList(),
                    selectedItems = persistentListOf(2, 3),
                    selectedEmployees = listOf(1, 2, 3),
                    isLoading = false,
                    onSelectEmployee = {},
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
                AbsentExportScreenContent(
                    items = persistentListOf(),
                    selectedItems = persistentListOf(),
                    selectedEmployees = emptyList(),
                    showSearchBar = false,
                    searchText = "",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onSelectEmployee = {},
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
                AbsentExportScreenContent(
                    items = absentsList.toImmutableList(),
                    selectedItems = persistentListOf(1, 3, 6),
                    selectedEmployees = listOf(1, 2, 3),
                    showSearchBar = false,
                    searchText = "",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onSelectEmployee = {},
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
                AbsentExportScreenContent(
                    items = absentsList
                        .searchAbsentees("text").toImmutableList(),
                    selectedItems = persistentListOf(),
                    selectedEmployees = emptyList(),
                    showSearchBar = true,
                    searchText = "text",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onSelectEmployee = {},
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
                AbsentExportScreenContent(
                    items = absentsList
                        .searchAbsentees("Sick").toImmutableList(),
                    selectedItems = persistentListOf(),
                    selectedEmployees = listOf(1, 2, 3),
                    showSearchBar = true,
                    searchText = "Sick",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onSelectEmployee = {},
                    onClickExport = {},
                    onBackClick = {},
                    onClickToAddItem = {},
                )
            }
        }
    }
}
