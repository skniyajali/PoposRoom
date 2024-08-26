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

package com.niyaj.employeePayment

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.employeePayment.createOrUpdate.AddEditPaymentScreenContent
import com.niyaj.employeePayment.createOrUpdate.AddEditPaymentState
import com.niyaj.employeePayment.settings.PaymentExportScreenContent
import com.niyaj.employeePayment.settings.PaymentImportScreenContent
import com.niyaj.employeePayment.settings.PaymentSettingsScreenContent
import com.niyaj.model.Employee
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import com.niyaj.model.searchEmployeeWithPayments
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.EmployeePreviewData
import com.niyaj.ui.parameterProvider.PaymentPreviewData
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
class PaymentScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val paymentsList = PaymentPreviewData.employeesWithPayments
    private val employeeList: List<Employee> = EmployeePreviewData.employeeList

    @Test
    fun paymentScreenLoading() {
        composeTestRule.captureForPhone("PaymentScreenLoading") {
            PoposRoomTheme {
                PaymentScreenContent(
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
                    onClickAddPayment = {},
                    onClickEmployee = {},
                )
            }
        }
    }

    @Test
    fun paymentScreenEmptyContent() {
        composeTestRule.captureForPhone("PaymentScreenEmptyContent") {
            PoposRoomTheme {
                PaymentScreenContent(
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
                    onClickAddPayment = {},
                    onClickEmployee = {},
                )
            }
        }
    }

    @Test
    fun paymentScreenSuccessContent() {
        composeTestRule.captureForPhone("PaymentScreenSuccessContent") {
            PoposRoomTheme {
                PaymentScreenContent(
                    uiState = UiState.Success(paymentsList),
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
                    onClickAddPayment = {},
                    onClickEmployee = {},
                )
            }
        }
    }

    @Test
    fun itemsPopulatedAndSelected() {
        composeTestRule.captureForPhone("ItemsPopulatedAndSelected") {
            PoposRoomTheme {
                PaymentScreenContent(
                    uiState = UiState.Success(paymentsList),
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
                    onClickAddPayment = {},
                    onClickEmployee = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                PaymentScreenContent(
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
                    onClickAddPayment = {},
                    onClickEmployee = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                PaymentScreenContent(
                    uiState = UiState.Success(paymentsList.searchEmployeeWithPayments("400")),
                    selectedItems = listOf(),
                    showSearchBar = true,
                    searchText = "400",
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
                    onClickAddPayment = {},
                    onClickEmployee = {},
                )
            }
        }
    }

    @Test
    fun showSettingsBottomSheet() {
        composeTestRule.captureForPhone("ShowSettingsBottomSheet") {
            PoposRoomTheme {
                PaymentSettingsScreenContent(
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
                AddEditPaymentScreenContent(
                    state = AddEditPaymentState(),
                    employees = persistentListOf(),
                    selectedEmployee = Employee(),
                    onEvent = {},
                    onBackClick = {},
                    onClickNewEmployee = {},
                    employeeError = "Employee name should not empty",
                    amountError = "Payment amount should not empty",
                    dateError = null,
                    typeError = null,
                    modeError = null,
                    noteError = null,
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithDummyData() {
        composeTestRule.captureForPhone("AddEditItemScreenWithDummyData") {
            PoposRoomTheme {
                AddEditPaymentScreenContent(
                    state = AddEditPaymentState(
                        paymentAmount = "200",
                        paymentNote = "Advance Payment",
                        paymentType = PaymentType.Advanced,
                        paymentMode = PaymentMode.Cash,
                    ),
                    employees = employeeList.toImmutableList(),
                    selectedEmployee = employeeList.first(),
                    onEvent = {},
                    onBackClick = {},
                    onClickNewEmployee = {},
                    employeeError = null,
                    amountError = null,
                    dateError = null,
                    typeError = null,
                    modeError = null,
                    noteError = null,
                )
            }
        }
    }

    @Test
    fun importScreenWithEmptyData() {
        composeTestRule.captureForPhone("ImportScreenWithEmptyData") {
            PoposRoomTheme {
                PaymentImportScreenContent(
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
                PaymentImportScreenContent(
                    importedItems = paymentsList.toImmutableList(),
                    selectedItems = persistentListOf(2, 3),
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
                PaymentExportScreenContent(
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
                PaymentExportScreenContent(
                    items = paymentsList.toImmutableList(),
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
                PaymentExportScreenContent(
                    items = paymentsList
                        .searchEmployeeWithPayments("text").toImmutableList(),
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
                PaymentExportScreenContent(
                    items = paymentsList
                        .searchEmployeeWithPayments("400").toImmutableList(),
                    selectedItems = persistentListOf(),
                    showSearchBar = true,
                    searchText = "400",
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
