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

package com.niyaj.order

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.searchOrder
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.parameterProvider.OrderPreviewData
import dagger.hilt.android.testing.HiltTestApplication
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
class OrderScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val dineInOrders = OrderPreviewData.dineInOrders
    private val dineOutOrders = OrderPreviewData.dineOutOrders

    @Test
    fun orderScreenLoading() {
        composeTestRule.captureForPhone("OrderScreenLoading") {
            PoposRoomTheme {
                OrderScreenContent(
                    dineInOrders = OrderState.Loading,
                    dineOutOrders = OrderState.Loading,
                    selectedDate = "",
                    showSearchBar = false,
                    searchText = "",
                    onOpenSearchBar = {},
                    onSearchTextChanged = {},
                    onClearClick = {},
                    onBackClick = {},
                    onClickPrintDeliveryReport = {},
                    onClickPrintOrder = {},
                    onOrderEvent = {},
                    onNavigateToHomeScreen = {},
                    onClickOrderDetails = {},
                    onClickEditOrder = {},
                    onClickShareOrder = {},
                )
            }
        }
    }

    @Test
    fun orderScreenEmptyContent() {
        composeTestRule.captureForPhone("OrderScreenEmptyContent") {
            PoposRoomTheme {
                OrderScreenContent(
                    dineInOrders = OrderState.Empty,
                    dineOutOrders = OrderState.Empty,
                    selectedDate = "",
                    showSearchBar = false,
                    searchText = "",
                    onOpenSearchBar = {},
                    onSearchTextChanged = {},
                    onClearClick = {},
                    onBackClick = {},
                    onClickPrintDeliveryReport = {},
                    onClickPrintOrder = {},
                    onOrderEvent = {},
                    onNavigateToHomeScreen = {},
                    onClickOrderDetails = {},
                    onClickEditOrder = {},
                    onClickShareOrder = {},
                )
            }
        }
    }

    @Test
    fun orderScreenSuccessContent() {
        composeTestRule.captureForPhone("OrderScreenSuccessContent") {
            PoposRoomTheme {
                OrderScreenContent(
                    dineInOrders = OrderState.Success(dineInOrders),
                    dineOutOrders = OrderState.Success(dineOutOrders),
                    selectedDate = "",
                    showSearchBar = false,
                    searchText = "",
                    onOpenSearchBar = {},
                    onSearchTextChanged = {},
                    onClearClick = {},
                    onBackClick = {},
                    onClickPrintDeliveryReport = {},
                    onClickPrintOrder = {},
                    onOrderEvent = {},
                    onNavigateToHomeScreen = {},
                    onClickOrderDetails = {},
                    onClickEditOrder = {},
                    onClickShareOrder = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetEmptyResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetEmptyResult") {
            PoposRoomTheme {
                OrderScreenContent(
                    dineInOrders = OrderState.Success(dineInOrders.searchOrder("search")),
                    dineOutOrders = OrderState.Success(dineOutOrders.searchOrder("search")),
                    selectedDate = "",
                    showSearchBar = true,
                    searchText = "search",
                    onOpenSearchBar = {},
                    onSearchTextChanged = {},
                    onClearClick = {},
                    onBackClick = {},
                    onClickPrintDeliveryReport = {},
                    onClickPrintOrder = {},
                    onOrderEvent = {},
                    onNavigateToHomeScreen = {},
                    onClickOrderDetails = {},
                    onClickEditOrder = {},
                    onClickShareOrder = {},
                )
            }
        }
    }

    @Test
    fun showSearchBarAndGetSuccessResult() {
        composeTestRule.captureForPhone("ShowSearchBarAndGetSuccessResult") {
            PoposRoomTheme {
                OrderScreenContent(
                    dineInOrders = OrderState.Success(dineInOrders.searchOrder("main")),
                    dineOutOrders = OrderState.Success(dineOutOrders.searchOrder("main")),
                    selectedDate = "",
                    showSearchBar = true,
                    searchText = "main",
                    onOpenSearchBar = {},
                    onSearchTextChanged = {},
                    onClearClick = {},
                    onBackClick = {},
                    onClickPrintDeliveryReport = {},
                    onClickPrintOrder = {},
                    onOrderEvent = {},
                    onNavigateToHomeScreen = {},
                    onClickOrderDetails = {},
                    onClickEditOrder = {},
                    onClickShareOrder = {},
                )
            }
        }
    }
}
