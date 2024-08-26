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

package com.niyaj.employee

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.employee.createOrUpdate.AddEditEmployeeScreenContent
import com.niyaj.employee.createOrUpdate.AddEditEmployeeState
import com.niyaj.employee.details.EmployeeDetailsScreenContent
import com.niyaj.employee.settings.EmployeeExportScreenContent
import com.niyaj.employee.settings.EmployeeImportScreenContent
import com.niyaj.employee.settings.EmployeeSettingsScreenContent
import com.niyaj.model.searchEmployee
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
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
class EmployeeScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val employeeList = EmployeePreviewData.employeeList

    @Test
    fun employeeScreenLoading() {
        composeTestRule.captureForPhone("EmployeeScreenLoading") {
            PoposRoomTheme {
                EmployeeScreenContent(
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
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun employeeScreenEmptyContent() {
        composeTestRule.captureForPhone("EmployeeScreenEmptyContent") {
            PoposRoomTheme {
                EmployeeScreenContent(
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
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun employeeScreenSuccessContent() {
        composeTestRule.captureForPhone("EmployeeScreenSuccessContent") {
            PoposRoomTheme {
                EmployeeScreenContent(
                    uiState = UiState.Success(employeeList),
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
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun itemsPopulatedAndSelected() {
        composeTestRule.captureForPhone("ItemsPopulatedAndSelected") {
            PoposRoomTheme {
                EmployeeScreenContent(
                    uiState = UiState.Success(employeeList),
                    selectedItems = listOf(2, 3, 5),
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
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                EmployeeScreenContent(
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
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                EmployeeScreenContent(
                    uiState = UiState.Success(employeeList.searchEmployee("John")),
                    selectedItems = listOf(),
                    showSearchBar = true,
                    searchText = "John",
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
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun showSettingsBottomSheet() {
        composeTestRule.captureForPhone("ShowSettingsBottomSheet") {
            PoposRoomTheme {
                EmployeeSettingsScreenContent(
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
                AddEditEmployeeScreenContent(
                    state = AddEditEmployeeState(),
                    phoneError = "Employee phone should not empty",
                    nameError = "Employee name should not empty",
                    salaryError = "Employee salary should not empty",
                    positionError = "Employee position should not empty",
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
                AddEditEmployeeScreenContent(
                    state = AddEditEmployeeState(
                        employeeName = "New Employee",
                        employeePhone = "1234567890",
                        employeePosition = "Assistant",
                        employeeSalary = "12000",
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
                EmployeeImportScreenContent(
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
        composeTestRule.captureForPhone("ImportScreenWithSomeData") {
            PoposRoomTheme {
                EmployeeImportScreenContent(
                    importedItems = employeeList.toImmutableList(),
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
    fun exportScreenWithEmptyData() {
        composeTestRule.captureForPhone("ExportScreenWithEmptyData") {
            PoposRoomTheme {
                EmployeeExportScreenContent(
                    items = persistentListOf(),
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
                EmployeeExportScreenContent(
                    items = employeeList.toImmutableList(),
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
                EmployeeExportScreenContent(
                    items = employeeList
                        .searchEmployee("text").toImmutableList(),
                    selectedItems = persistentListOf(),
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
        composeTestRule.captureForPhone("ExportScreenPerformSearchAndGetSomeResult") {
            PoposRoomTheme {
                EmployeeExportScreenContent(
                    items = employeeList
                        .searchEmployee("John").toImmutableList(),
                    selectedItems = persistentListOf(),
                    showSearchBar = true,
                    searchText = "John",
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
    fun employeeDetailsScreenLoading() {
        composeTestRule.captureForPhone("EmployeeDetailsScreenLoading") {
            PoposRoomTheme {
                EmployeeDetailsScreenContent(
                    employeeState = UiState.Loading,
                    salaryEstimationState = UiState.Loading,
                    paymentsState = UiState.Loading,
                    absentState = UiState.Loading,
                    salaryDates = persistentListOf(),
                    onEvent = {},
                    onBackClick = {},
                    onClickAddPayment = {},
                    onClickAddAbsent = {},
                    onClickEdit = {},
                    selectedSalaryDate = null,
                )
            }
        }
    }

    @Test
    fun employeeDetailsScreenEmpty() {
        composeTestRule.captureForPhone("EmployeeDetailsScreenEmpty") {
            PoposRoomTheme {
                EmployeeDetailsScreenContent(
                    employeeState = UiState.Empty,
                    salaryEstimationState = UiState.Empty,
                    paymentsState = UiState.Empty,
                    absentState = UiState.Empty,
                    salaryDates = persistentListOf(),
                    onEvent = {},
                    onBackClick = {},
                    onClickAddPayment = {},
                    onClickAddAbsent = {},
                    onClickEdit = {},
                    selectedSalaryDate = null,
                )
            }
        }
    }

    @Test
    fun employeeDetailsScreenPopulated() {
        composeTestRule.captureForPhone("EmployeeDetailsScreenPopulated") {
            PoposRoomTheme {
                EmployeeDetailsScreenContent(
                    employeeState = UiState.Success(employeeList.first()),
                    salaryEstimationState = UiState.Success(EmployeePreviewData.employeeSalaryEstimations.last()),
                    paymentsState = UiState.Success(EmployeePreviewData.employeePayments),
                    absentState = UiState.Success(EmployeePreviewData.employeeAbsentDates),
                    salaryDates = EmployeePreviewData.employeeMonthlyDates.toImmutableList(),
                    onEvent = {},
                    onBackClick = {},
                    onClickAddPayment = {},
                    onClickAddAbsent = {},
                    onClickEdit = {},
                    selectedSalaryDate = null,
                )
            }
        }
    }
}
