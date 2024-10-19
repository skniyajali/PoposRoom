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

package com.niyaj.feature.product

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import com.niyaj.common.tags.ProductTestTags.CATEGORY_LIST
import com.niyaj.common.tags.ProductTestTags.CATEGORY_TAG
import com.niyaj.common.tags.ProductTestTags.CREATE_NEW_PRODUCT
import com.niyaj.common.tags.ProductTestTags.PRODUCT_LIST
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NOT_AVAILABLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SCREEN_TITLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.ProductTestTags.PRODUCT_TAG
import com.niyaj.common.utils.Constants.DRAWER_ICON
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_SEARCH_BAR
import com.niyaj.common.utils.getAllCapitalizedLetters
import com.niyaj.common.utils.toRupee
import com.niyaj.poposroom.core.testing.util.BaseComposeTest
import com.niyaj.testing.repository.TestProductRepository
import com.niyaj.testing.util.OpenResultRecipient
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.testing.util.TestDestinationsNavigator
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.NAV_SETTING_BTN
import com.niyaj.ui.components.PRIMARY_APP_DRAWER
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.parameterProvider.ProductPreviewData
import org.junit.Before
import org.junit.Test

class ProductScreenTest : BaseComposeTest() {

    private val itemList = ProductPreviewData.productList
    private val categoryList = CategoryPreviewData.categoryList

    private val repository = TestProductRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val navigator = TestDestinationsNavigator()
    private val viewModel = ProductViewModel(
        productRepository = repository,
        analyticsHelper = analyticsHelper,
    )

    @Before
    fun setup() {
        composeTestRule.apply {
            setContent {
                ProductScreen(
                    navigator = navigator,
                    resultRecipient = OpenResultRecipient(),
                    exportRecipient = OpenResultRecipient(),
                    importRecipient = OpenResultRecipient(),
                    increaseRecipient = OpenResultRecipient(),
                    decreaseRecipient = OpenResultRecipient(),
                    viewModel = viewModel,
                )
            }
        }
    }

    @Test
    fun initialProductScreen_isDisplayedAndEmpty() {
        composeTestRule.apply {
            onNodeWithText(PRODUCT_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithText(PRODUCT_NOT_AVAILABLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(CREATE_NEW_PRODUCT).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
        }
    }

    @Test
    fun onClickDrawerBtn_shouldOpenDrawer() {
        composeTestRule.apply {
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction().performClick()
            onNodeWithTag(PRIMARY_APP_DRAWER).assertIsDisplayed()
        }
    }

    @Test
    fun categoryIsEmpty_initially() {
        composeTestRule.apply {
            onNodeWithTag(CATEGORY_LIST).assertExists()
            onNodeWithTag(CATEGORY_LIST).onChildren().assertCountEquals(0)
        }
    }

    @Test
    fun categoryList_populated_whenDataIsAvailable() {
        repository.setCategoryList(categoryList)

        composeTestRule.apply {
            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()

            categoryList.take(3).forEach {
                val tag = onNodeWithTag(CATEGORY_TAG.plus(it.categoryId))
                tag.assertIsDisplayed()
                tag.assertHasClickAction()
                tag.assertTextContains(it.categoryName)
            }
        }
    }

    @Test
    fun productList_populated_whenDataIsAvailable() {
        repository.setProductList(itemList)

        composeTestRule.apply {
            onNodeWithText(PRODUCT_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithText(PRODUCT_NOT_AVAILABLE).assertIsNotDisplayed()

            onNodeWithTag(PRODUCT_LIST).assertIsDisplayed()

            itemList.take(3).forEach { product ->
                val tag = onNodeWithTag(PRODUCT_TAG.plus(product.productId))
                tag.assertIsDisplayed()
                tag.assertHasClickAction()
                tag.onChildAt(0).assertTextContains(product.productName)
                tag.onChildAt(0).assertTextContains(product.productPrice.toRupee)
                tag.onChildAt(0)
                    .assertTextContains(getAllCapitalizedLetters(product.productName).take(2))
                if (product.tags.isNotEmpty()) {
                    product.tags.forEach {
                        tag.onChildAt(0).assertTextContains(it)
                    }
                }
                if (product.productDescription.isNotEmpty()) {
                    tag.onChildAt(1).assertTextContains(product.productDescription)
                }
            }
        }
    }

    @Test
    fun showSearchIcon_whenProductsIsAvailable() {
        repository.setProductList(itemList)

        composeTestRule.apply {
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }

    @Test
    fun onClick_searchIcon_searchBarVisible() {
        composeTestRule.apply {
            repository.setProductList(itemList)

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().assertHasClickAction().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(PRODUCT_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            repository.setProductList(itemList)

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().assertHasClickAction().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(PRODUCT_SEARCH_PLACEHOLDER).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            Espresso.pressBack()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }
}
