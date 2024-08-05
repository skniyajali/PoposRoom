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

package com.niyaj.address

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.address.createOrUpdate.AddEditAddressScreenContent
import com.niyaj.address.createOrUpdate.AddEditAddressState
import com.niyaj.address.details.AddressDetailsScreenContent
import com.niyaj.address.settings.AddressExportScreenContent
import com.niyaj.address.settings.AddressImportScreenContent
import com.niyaj.address.settings.AddressSettingsScreenContent
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.TotalOrderDetails
import com.niyaj.model.searchAddress
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddressPreviewData
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
class AddressScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val addressList = AddressPreviewData.addressList

    @Test
    fun addressScreenLoading() {
        composeTestRule.captureForPhone("AddressScreenLoading") {
            PoposRoomTheme {
                AddressScreenContent(
                    uiState = UiState.Loading,
                    selectedItems = persistentListOf(),
                    showSearchBar = false,
                    searchText = "",
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
                    onClickSelectItem = {},
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun addressScreenEmptyContent() {
        composeTestRule.captureForPhone("AddressScreenEmptyContent") {
            PoposRoomTheme {
                AddressScreenContent(
                    uiState = UiState.Empty,
                    selectedItems = persistentListOf(),
                    showSearchBar = false,
                    searchText = "",
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
                    onClickSelectItem = {},
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun addressScreenSuccessContent() {
        composeTestRule.captureForPhone("AddressScreenSuccessContent") {
            PoposRoomTheme {
                AddressScreenContent(
                    uiState = UiState.Success(addressList),
                    selectedItems = persistentListOf(),
                    showSearchBar = false,
                    searchText = "",
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
                    onClickSelectItem = {},
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun itemsPopulatedAndSelected() {
        composeTestRule.captureForPhone("AddressPopulatedAndSelected") {
            PoposRoomTheme {
                AddressScreenContent(
                    uiState = UiState.Success(addressList),
                    selectedItems = persistentListOf(1, 3, 5),
                    showSearchBar = false,
                    searchText = "",
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
                    onClickSelectItem = {},
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                AddressScreenContent(
                    uiState = UiState.Empty,
                    selectedItems = persistentListOf(),
                    showSearchBar = true,
                    searchText = "address",
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
                    onClickSelectItem = {},
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                AddressScreenContent(
                    uiState = UiState.Success(
                        addressList.searchAddress("Pine"),
                    ),
                    selectedItems = persistentListOf(),
                    showSearchBar = true,
                    searchText = "Pine",
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
                    onClickSelectItem = {},
                    onNavigateToDetails = {},
                )
            }
        }
    }

    @Test
    fun showSettingsBottomSheet() {
        composeTestRule.captureForPhone("ShowSettingsBottomSheet") {
            PoposRoomTheme {
                AddressSettingsScreenContent(
                    onBackClick = {},
                    onExportClick = {},
                    onImportClick = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithEmptyData() {
        composeTestRule.captureForPhone("AddEditAddressScreenWithEmptyData") {
            PoposRoomTheme {
                AddEditAddressScreenContent(
                    state = AddEditAddressState(),
                    nameError = "Address name should not empty",
                    shortNameError = "Short name should not empty",
                    onEvent = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithDummyData() {
        composeTestRule.captureForPhone("AddEditAddressScreenWithDummyData") {
            PoposRoomTheme {
                AddEditAddressScreenContent(
                    state = AddEditAddressState(
                        addressName = "Main Street",
                        shortName = "MS",
                    ),
                    onEvent = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun importScreenWithEmptyData() {
        composeTestRule.captureForPhone("AddressImportScreenWithEmptyData") {
            PoposRoomTheme {
                AddressImportScreenContent(
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
        composeTestRule.captureForPhone("AddressImportScreenWithSomeData") {
            PoposRoomTheme {
                AddressImportScreenContent(
                    importedItems = addressList.toImmutableList(),
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
        composeTestRule.captureForPhone("AddressExportScreenWithEmptyData") {
            PoposRoomTheme {
                AddressExportScreenContent(
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
        composeTestRule.captureForPhone("AddressExportScreenWithSomeData") {
            PoposRoomTheme {
                AddressExportScreenContent(
                    items = addressList.toImmutableList(),
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
        composeTestRule.captureForPhone("AddressExportScreenPerformSearchAndGetEmptyResult") {
            PoposRoomTheme {
                AddressExportScreenContent(
                    items = addressList
                        .searchAddress("search").toImmutableList(),
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
        composeTestRule.captureForPhone("AddressExportScreenPerformSearchAndGetSomeResult") {
            PoposRoomTheme {
                AddressExportScreenContent(
                    items = addressList
                        .searchAddress("Oak").toImmutableList(),
                    selectedItems = persistentListOf(),
                    showSearchBar = true,
                    searchText = "Oak",
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
    fun addressDetailsScreenLoading() {
        composeTestRule.captureForPhone("AddressDetailsScreenLoading") {
            PoposRoomTheme {
                AddressDetailsScreenContent(
                    addressState = UiState.Loading,
                    orderDetailsState = UiState.Loading,
                    totalOrdersState = TotalOrderDetails(),
                    onClickEdit = {},
                    onClickOrder = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun addressDetailsScreenEmpty() {
        composeTestRule.captureForPhone("AddressDetailsScreenEmpty") {
            PoposRoomTheme {
                AddressDetailsScreenContent(
                    addressState = UiState.Empty,
                    orderDetailsState = UiState.Empty,
                    totalOrdersState = TotalOrderDetails(),
                    onClickEdit = {},
                    onClickOrder = {},
                    onBackClick = {},
                )
            }
        }
    }

    @Test
    fun addressDetailsScreenPopulated() {
        composeTestRule.captureForPhone("AddressDetailsScreenPopulated") {
            PoposRoomTheme {
                AddressDetailsScreenContent(
                    addressState = UiState.Success(addressList.first()),
                    orderDetailsState = UiState.Success(AddressPreviewData.sampleAddressWiseOrders),
                    totalOrdersState = AddressPreviewData.sampleTotalOrder,
                    onClickEdit = {},
                    onClickOrder = {},
                    onBackClick = {},
                )
            }
        }
    }
}
