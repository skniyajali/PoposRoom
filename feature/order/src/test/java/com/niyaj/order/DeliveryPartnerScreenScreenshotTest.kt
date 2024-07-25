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
import com.niyaj.order.deliveryPartner.DeliveryPartnerDetailsScreenContent
import com.niyaj.order.deliveryPartner.DeliveryPartnerScreenContent
import com.niyaj.order.deliveryPartner.PartnerReportState
import com.niyaj.order.deliveryPartner.PartnerState
import com.niyaj.poposroom.core.testing.util.captureForPhone
import com.niyaj.ui.parameterProvider.CardOrderPreviewData
import com.niyaj.ui.parameterProvider.DeliveryPartnerPreviewData
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
class DeliveryPartnerScreenScreenshotTest {

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val partnerOrders = DeliveryPartnerPreviewData.partnerOrders
    private val deliveryReports = DeliveryPartnerPreviewData.deliveryReports
    private val partners = CardOrderPreviewData.sampleEmployeeNameAndIds.toImmutableList()

    @Test
    fun deliveryPartnerScreenLoading() {
        composeTestRule.captureForPhone("DeliveryPartnerScreenLoading") {
            PoposRoomTheme {
                DeliveryPartnerScreenContent(
                    selectedDate = System.currentTimeMillis().toString(),
                    partnerState = PartnerState.Loading,
                    onBackClick = {},
                    onSelectDate = {},
                    onClickPrintAll = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onClickViewDetails = {},
                    onNavigateToHomeScreen = {},
                )
            }
        }
    }

    @Test
    fun deliveryPartnerScreenEmptyContent() {
        composeTestRule.captureForPhone("DeliveryPartnerScreenEmptyContent") {
            PoposRoomTheme {
                DeliveryPartnerScreenContent(
                    selectedDate = System.currentTimeMillis().toString(),
                    partnerState = PartnerState.Empty,
                    onBackClick = {},
                    onSelectDate = {},
                    onClickPrintAll = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onClickViewDetails = {},
                    onNavigateToHomeScreen = {},
                )
            }
        }
    }

    @Test
    fun deliveryPartnerScreenSuccessContent() {
        composeTestRule.captureForPhone("DeliveryPartnerScreenSuccessContent") {
            PoposRoomTheme {
                DeliveryPartnerScreenContent(
                    selectedDate = System.currentTimeMillis().toString(),
                    partnerState = PartnerState.Success(partnerOrders),
                    onBackClick = {},
                    onSelectDate = {},
                    onClickPrintAll = {},
                    onClickShare = {},
                    onClickPrint = {},
                    onClickViewDetails = {},
                    onNavigateToHomeScreen = {},
                )
            }
        }
    }

    @Test
    fun deliveryReportScreenLoading() {
        composeTestRule.captureForPhone("DeliveryReportScreenLoading") {
            PoposRoomTheme {
                DeliveryPartnerDetailsScreenContent(
                    selectedDate = System.currentTimeMillis().toString(),
                    reportState = PartnerReportState.Loading,
                    partners = persistentListOf(),
                    selectedItems = persistentListOf(),
                    onClickPrint = {},
                    onClickShare = {},
                    onBackClick = {},
                    onNavigateToHomeScreen = {},
                    onClickOrder = {},
                    onSelectDate = {},
                    onSelectItem = {},
                    onDeselectItems = {},
                    onClickSelectItems = {},
                    onChangePartner = {},
                )
            }
        }
    }

    @Test
    fun deliveryReportScreenEmptyContent() {
        composeTestRule.captureForPhone("DeliveryReportScreenEmptyContent") {
            PoposRoomTheme {
                DeliveryPartnerDetailsScreenContent(
                    selectedDate = System.currentTimeMillis().toString(),
                    reportState = PartnerReportState.Empty,
                    partners = persistentListOf(),
                    selectedItems = persistentListOf(),
                    onClickPrint = {},
                    onClickShare = {},
                    onBackClick = {},
                    onNavigateToHomeScreen = {},
                    onClickOrder = {},
                    onSelectDate = {},
                    onSelectItem = {},
                    onDeselectItems = {},
                    onClickSelectItems = {},
                    onChangePartner = {},
                )
            }
        }
    }

    @Test
    fun deliveryReportScreenSuccessContent() {
        composeTestRule.captureForPhone("DeliveryReportScreenSuccessContent") {
            PoposRoomTheme {
                DeliveryPartnerDetailsScreenContent(
                    selectedDate = System.currentTimeMillis().toString(),
                    reportState = PartnerReportState.Success(deliveryReports),
                    partners = partners,
                    selectedItems = persistentListOf(),
                    onClickPrint = {},
                    onClickShare = {},
                    onBackClick = {},
                    onNavigateToHomeScreen = {},
                    onClickOrder = {},
                    onSelectDate = {},
                    onSelectItem = {},
                    onDeselectItems = {},
                    onClickSelectItems = {},
                    onChangePartner = {},
                )
            }
        }
    }

    @Test
    fun deliveryReportScreenSuccessAndSelectedContent() {
        composeTestRule.captureForPhone("DeliveryReportScreenSuccessAndSelectedContent") {
            PoposRoomTheme {
                DeliveryPartnerDetailsScreenContent(
                    selectedDate = System.currentTimeMillis().toString(),
                    reportState = PartnerReportState.Success(deliveryReports),
                    partners = partners,
                    selectedItems = deliveryReports.filter { it.orderId % 2 == 0 }.map { it.orderId }
                        .toImmutableList(),
                    onClickPrint = {},
                    onClickShare = {},
                    onBackClick = {},
                    onNavigateToHomeScreen = {},
                    onClickOrder = {},
                    onSelectDate = {},
                    onSelectItem = {},
                    onDeselectItems = {},
                    onClickSelectItems = {},
                    onChangePartner = {},
                )
            }
        }
    }
}
