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

package com.niyaj.product

import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.common.utils.toBarDate
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.Category
import com.niyaj.model.searchProducts
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.product.createOrUpdate.AddEditProductScreenContent
import com.niyaj.product.createOrUpdate.AddEditProductState
import com.niyaj.product.createOrUpdate.defaultTagList
import com.niyaj.product.details.ProductDetailsScreenContent
import com.niyaj.product.details.ProductTotalOrderDetails
import com.niyaj.product.settings.DecreaseProductPriceScreenContent
import com.niyaj.product.settings.ExportProductScreenContent
import com.niyaj.product.settings.ImportProductScreenContent
import com.niyaj.product.settings.IncreaseProductPriceScreenContent
import com.niyaj.product.settings.ProductSettingScreenContent
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.parameterProvider.CategoryPreviewData.categories
import com.niyaj.ui.parameterProvider.ProductPreviewData
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

@OptIn(ExperimentalFoundationApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class)
@LooperMode(LooperMode.Mode.PAUSED)
class ProductScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val productList = ProductPreviewData.productList
    private val productWiseOrder = ProductPreviewData.productWiseOrders
    private val categoryList = CategoryPreviewData.categoryList

    private val totalOrderDetails: ProductTotalOrderDetails = ProductTotalOrderDetails(
        totalAmount = "1200",
        dineInAmount = "600",
        dineInQty = 6,
        dineOutAmount = "600",
        dineOutQty = 6,
        mostOrderItemDate = "1686854400000".toBarDate,
        mostOrderQtyDate = "1687200000000".toBarDate,
        datePeriod = Pair("1685603200000", "1688195200000"),
    )

    @Test
    fun productScreenLoading() {
        composeTestRule.captureForPhone("ProductScreenLoading") {
            PoposRoomTheme {
                ProductScreenContent(
                    uiState = UiState.Loading,
                    categories = categoryList,
                    selectedCategory = 0,
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
                    onSelectCategory = {},
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
    fun productScreenEmptyContent() {
        composeTestRule.captureForPhone("ProductScreenEmptyContent") {
            PoposRoomTheme {
                ProductScreenContent(
                    uiState = UiState.Empty,
                    categories = categoryList,
                    selectedCategory = 0,
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
                    onSelectCategory = {},
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
    fun productScreenSuccessContent() {
        composeTestRule.captureForPhone("ProductScreenSuccessContent") {
            PoposRoomTheme {
                ProductScreenContent(
                    uiState = UiState.Success(productList),
                    categories = categoryList,
                    selectedCategory = 0,
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
                    onSelectCategory = {},
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
                ProductScreenContent(
                    uiState = UiState.Success(productList),
                    categories = categoryList,
                    selectedCategory = 0,
                    selectedItems = listOf(3, 6, 8),
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
                    onSelectCategory = {},
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
                ProductScreenContent(
                    uiState = UiState.Success(productList.searchProducts("search")),
                    categories = categoryList,
                    selectedCategory = 0,
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
                    onSelectCategory = {},
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
                ProductScreenContent(
                    uiState = UiState.Success(productList.searchProducts("Chicken")),
                    categories = categoryList,
                    selectedCategory = 0,
                    selectedItems = listOf(),
                    showSearchBar = true,
                    searchText = "Chicken",
                    onClickSearchIcon = {},
                    onSearchTextChanged = {},
                    onClickClear = {},
                    onCloseSearchBar = {},
                    onClickSelectItem = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onClickDelete = {},
                    onSelectCategory = {},
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
                ProductSettingScreenContent(
                    onBackClick = {},
                    onImportClick = {},
                    onExportClick = {},
                    onClickViewCategory = {},
                    onCategoryImportClick = {},
                    onCategoryExportClick = {},
                    onIncreaseClick = {},
                    onDecreaseClick = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithEmptyData() {
        composeTestRule.captureForPhone("AddEditItemScreenWithEmptyData") {
            PoposRoomTheme {
                AddEditProductScreenContent(
                    state = AddEditProductState(),
                    selectedCategory = Category(),
                    categories = categoryList,
                    tagList = defaultTagList,
                    selectedTags = emptyList(),
                    onEvent = {},
                    categoryError = "Category name should not be empty",
                    priceError = "Product Price should not be empty",
                    nameError = "Product Name should not be empty",
                    tagError = null,
                    onBackClick = {},
                    onClickAddCategory = {},
                )
            }
        }
    }

    @Test
    fun addEditItemScreenWithDummyData() {
        composeTestRule.captureForPhone("AddEditItemScreenWithDummyData") {
            PoposRoomTheme {
                AddEditProductScreenContent(
                    state = AddEditProductState(
                        productName = "New Product",
                        productPrice = "120",
                        productDesc = "Product description",
                        productAvailability = false,
                    ),
                    selectedCategory = categoryList.first(),
                    categories = categoryList,
                    tagList = defaultTagList,
                    selectedTags = emptyList(),
                    onEvent = {},
                    categoryError = null,
                    priceError = null,
                    nameError = null,
                    tagError = null,
                    onBackClick = {},
                    onClickAddCategory = {},
                )
            }
        }
    }

    @Test
    fun importScreenWithEmptyData() {
        composeTestRule.captureForPhone("ImportScreenWithEmptyData") {
            PoposRoomTheme {
                ImportProductScreenContent(
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
                ImportProductScreenContent(
                    importedItems = productList.toImmutableList(),
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
                ExportProductScreenContent(
                    items = persistentListOf(),
                    selectedItems = persistentListOf(),
                    selectedCategory = emptyList(),
                    categories = persistentListOf(),
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
                    onSelectCategory = {},
                )
            }
        }
    }

    @Test
    fun exportScreenWithSomeData() {
        composeTestRule.captureForPhone("ExportScreenWithSomeData") {
            PoposRoomTheme {
                ExportProductScreenContent(
                    items = productList.toImmutableList(),
                    selectedItems = persistentListOf(2, 4, 6),
                    selectedCategory = emptyList(),
                    categories = categoryList,
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
                    onSelectCategory = {},
                )
            }
        }
    }

    @Test
    fun increasePriceScreenWithEmptyData() {
        composeTestRule.captureForPhone("IncreasePriceScreenWithEmptyData") {
            PoposRoomTheme {
                IncreaseProductPriceScreenContent(
                    items = persistentListOf(),
                    categories = categories,
                    selectedItems = persistentListOf(),
                    productPrice = "",
                    showSearchBar = false,
                    searchText = "",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onIncreaseClick = {},
                    onBackClick = {},
                    onClickToAddItem = {},
                    selectedCategories = listOf(),
                    onSelectCategory = {},
                    onPriceChanged = {},
                )
            }
        }
    }

    @Test
    fun increasePriceScreenWithSomeData() {
        composeTestRule.captureForPhone("IncreasePriceScreenWithSomeData") {
            PoposRoomTheme {
                IncreaseProductPriceScreenContent(
                    modifier = Modifier,
                    items = productList.toImmutableList(),
                    categories = categories,
                    selectedItems = persistentListOf(),
                    productPrice = "",
                    showSearchBar = false,
                    searchText = "",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onIncreaseClick = {},
                    onBackClick = {},
                    onClickToAddItem = {},
                    selectedCategories = listOf(),
                    onSelectCategory = {},
                    onPriceChanged = {},
                )
            }
        }
    }

    @Test
    fun decreasePriceScreenWithEmptyData() {
        composeTestRule.captureForPhone("DecreasePriceScreenWithEmptyData") {
            PoposRoomTheme {
                DecreaseProductPriceScreenContent(
                    modifier = Modifier,
                    items = persistentListOf(),
                    categories = categories,
                    selectedItems = persistentListOf(),
                    productPrice = "",
                    showSearchBar = false,
                    searchText = "",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onDecreaseClick = {},
                    onBackClick = {},
                    onClickToAddItem = {},
                    selectedCategories = listOf(),
                    onSelectCategory = {},
                    onPriceChanged = {},
                )
            }
        }
    }

    @Test
    fun decreasePriceScreenWithSomeData() {
        composeTestRule.captureForPhone("DecreasePriceScreenWithSomeData") {
            PoposRoomTheme {
                DecreaseProductPriceScreenContent(
                    modifier = Modifier,
                    items = productList.toImmutableList(),
                    categories = categories,
                    selectedItems = persistentListOf(),
                    productPrice = "",
                    showSearchBar = false,
                    searchText = "",
                    onClearClick = {},
                    onSearchTextChanged = {},
                    onClickOpenSearch = {},
                    onClickCloseSearch = {},
                    onClickSelectAll = {},
                    onClickDeselect = {},
                    onSelectItem = {},
                    onDecreaseClick = {},
                    onBackClick = {},
                    onClickToAddItem = {},
                    selectedCategories = listOf(),
                    onSelectCategory = {},
                    onPriceChanged = {},
                )
            }
        }
    }

    @Test
    fun exportScreenPerformSearchAndGetEmptyResult() {
        composeTestRule.captureForPhone("ExportScreenPerformSearchAndGetEmptyResult") {
            PoposRoomTheme {
                ExportProductScreenContent(
                    items = productList.searchProducts("search").toImmutableList(),
                    selectedItems = persistentListOf(),
                    selectedCategory = emptyList(),
                    categories = categoryList,
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
                    onSelectCategory = {},
                )
            }
        }
    }

    @Test
    fun exportScreenPerformSearchAndGetSomeResult() {
        composeTestRule.captureForPhone("ExportScreenPerformSearchAndGetSomeResult") {
            PoposRoomTheme {
                ExportProductScreenContent(
                    items = productList.searchProducts("Vegetable").toImmutableList(),
                    selectedItems = persistentListOf(),
                    selectedCategory = emptyList(),
                    categories = categoryList,
                    showSearchBar = true,
                    searchText = "Vegetable",
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
                    onSelectCategory = {},
                )
            }
        }
    }

    @Test
    fun productDetailsScreenLoading() {
        composeTestRule.captureForPhone("ProductDetailsScreenLoading") {
            PoposRoomTheme {
                ProductDetailsScreenContent(
                    productState = UiState.Loading,
                    orderDetailsState = UiState.Loading,
                    totalOrderDetails = ProductTotalOrderDetails(),
                    productPrice = 100,
                    onClickOrder = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onBackClick = {},
                    onClickEditProduct = {},
                )
            }
        }
    }

    @Test
    fun productDetailsScreenEmpty() {
        composeTestRule.captureForPhone("ProductDetailsScreenEmpty") {
            PoposRoomTheme {
                ProductDetailsScreenContent(
                    productState = UiState.Empty,
                    orderDetailsState = UiState.Empty,
                    totalOrderDetails = ProductTotalOrderDetails(),
                    productPrice = 100,
                    onClickOrder = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onBackClick = {},
                    onClickEditProduct = {},
                )
            }
        }
    }

    @Test
    fun productDetailsScreenPopulated() {
        composeTestRule.captureForPhone("ProductDetailsScreenPopulated") {
            PoposRoomTheme {
                ProductDetailsScreenContent(
                    productState = UiState.Success(productList.first()),
                    orderDetailsState = UiState.Success(productWiseOrder),
                    totalOrderDetails = totalOrderDetails,
                    productPrice = 100,
                    onClickOrder = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onBackClick = {},
                    onClickEditProduct = {},
                )
            }
        }
    }
}
