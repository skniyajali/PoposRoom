/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.order.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.compose.ui.window.DialogProperties
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposOutlinedIconButton
import com.niyaj.designsystem.components.PoposSuggestionChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.gradient6
import com.niyaj.designsystem.theme.rainbowColorsBrush
import com.niyaj.model.TotalOrders
import com.niyaj.order.deliveryPartner.DeliveryReportCard
import com.niyaj.order.deliveryPartner.PartnerReportState
import com.niyaj.order.deliveryPartner.PartnerState
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.utils.CaptureController
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.ScrollableCapturable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareableDeliveryPartner(
    modifier: Modifier = Modifier,
    captureController: CaptureController,
    selectedDate: String,
    partnerState: PartnerState,
    onDismiss: () -> Unit,
    onClickPrintOrder: () -> Unit,
    onClickShare: () -> Unit,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
) = trace("ShareableDeliveryPartner") {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
            .fillMaxSize(),
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        ) {
            Crossfade(
                targetState = partnerState,
                label = "ShareableDialog",
            ) { state ->
                when (state) {
                    is PartnerState.Loading -> LoadingIndicator()

                    is PartnerState.Empty -> {
                        ItemNotAvailable(
                            text = "Seems like, you have not place any order yet, click below to create new.",
                            showImage = false,
                        )
                    }

                    is PartnerState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            CapturableDeliveryPartnerCard(
                                modifier = Modifier,
                                captureController = captureController,
                                selectedDate = selectedDate,
                                state = state,
                                containerColor = MaterialTheme.colorScheme.background,
                                gradientColor = gradient6,
                                icon = PoposIcons.DeliveryDining,
                                onCaptured = onCaptured,
                            )

                            DialogButtons(
                                modifier = Modifier.padding(SpaceSmall),
                                onDismiss = onDismiss,
                                onClickShare = onClickShare,
                                onClickPrintOrder = onClickPrintOrder,
                                shareButtonColor = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareablePartnerDetails(
    modifier: Modifier = Modifier,
    captureController: CaptureController,
    selectedDate: String,
    reportState: PartnerReportState,
    onDismiss: () -> Unit,
    onClickPrintOrder: () -> Unit,
    onClickShare: () -> Unit,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
) = trace("ShareablePartnerDetails") {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
            .fillMaxSize(),
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        ) {
            Crossfade(
                targetState = reportState,
                label = "ShareableDialog",
            ) { state ->
                when (state) {
                    is PartnerReportState.Loading -> LoadingIndicator()

                    is PartnerReportState.Empty -> {
                        ItemNotAvailable(
                            text = "Seems like, you did not delivered any order yet, click below to create new.",
                            showImage = false,
                        )
                    }

                    is PartnerReportState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            CapturablePartnerDetailsCard(
                                modifier = Modifier,
                                captureController = captureController,
                                selectedDate = selectedDate,
                                state = state,
                                onCaptured = onCaptured,
                            )

                            DialogButtons(
                                modifier = Modifier.padding(SpaceSmall),
                                onDismiss = onDismiss,
                                onClickShare = onClickShare,
                                onClickPrintOrder = onClickPrintOrder,
                                shareButtonColor = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CapturableDeliveryPartnerCard(
    modifier: Modifier,
    captureController: CaptureController,
    selectedDate: String,
    state: PartnerState.Success,
    containerColor: Color,
    gradientColor: Brush,
    icon: ImageVector,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
) = trace("CapturableCard") {
    ScrollableCapturable(
        controller = captureController,
        onCaptured = onCaptured,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        ShareableDeliveryPartnerCard(
            modifier = modifier,
            state = state,
            selectedDate = selectedDate,
            containerColor = containerColor,
            gradientColor = gradientColor,
            icon = icon,
        )
    }
}

@Composable
private fun CapturablePartnerDetailsCard(
    modifier: Modifier,
    captureController: CaptureController,
    selectedDate: String,
    state: PartnerReportState.Success,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
) = trace("CapturableCard") {
    ScrollableCapturable(
        controller = captureController,
        onCaptured = onCaptured,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        ShareablePartnerDetailsCard(
            state = state,
            selectedDate = selectedDate,
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun ShareableDeliveryPartnerCard(
    modifier: Modifier = Modifier,
    state: PartnerState.Success,
    selectedDate: String,
    containerColor: Color,
    gradientColor: Brush,
    icon: ImageVector = PoposIcons.DeliveryDining,
) = trace("CartItemOrderDetailsCard") {
    val totalOrders = remember {
        TotalOrders(
            totalOrders = state.orders.size.toLong(),
            totalAmount = state.orders.sumOf { it.totalAmount },
        )
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
        shape = RoundedCornerShape(SpaceSmall),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ShareableDeliveryPartnerHeader(
                modifier = Modifier,
                title = "Delivery Reports",
                totalOrders = totalOrders,
                selectedDate = selectedDate.ifEmpty { System.currentTimeMillis().toString() },
                gradientColor = gradientColor,
                icon = icon,
            )

            ShareableDeliveryPartnerBody(
                modifier = Modifier,
                state = state,
            )
        }
    }
}

@Composable
private fun ShareablePartnerDetailsCard(
    modifier: Modifier = Modifier,
    state: PartnerReportState.Success,
    selectedDate: String,
    containerColor: Color = MaterialTheme.colorScheme.background,
    gradientColor: Brush = gradient6,
    icon: ImageVector = PoposIcons.DeliveryDining,
) = trace("ShareablePartnerDetailsCard") {
    val totalOrders = remember {
        TotalOrders(
            totalOrders = state.orders.size.toLong(),
            totalAmount = state.orders.sumOf { it.orderPrice },
        )
    }
    val title = state.orders.first().partnerName ?: "Unmanaged"

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
        shape = RoundedCornerShape(SpaceSmall),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ShareableDeliveryPartnerHeader(
                modifier = Modifier,
                title = title,
                totalOrders = totalOrders,
                selectedDate = selectedDate.ifEmpty { System.currentTimeMillis().toString() },
                gradientColor = gradientColor,
                icon = icon,
            )

            ShareablePartnerDetailsBody(
                modifier = Modifier,
                state = state,
            )
        }
    }
}

@Composable
private fun ShareableDeliveryPartnerHeader(
    modifier: Modifier = Modifier,
    title: String,
    totalOrders: TotalOrders,
    selectedDate: String,
    gradientColor: Brush = gradient6,
    icon: ImageVector = PoposIcons.DeliveryDining,
) = trace("ShareableDeliveryPartnerHeader") {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(gradientColor),
            )

            CircularBox(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 40.dp),
                icon = icon,
                showBorder = true,
                doesSelected = false,
                borderStroke = BorderStroke(3.dp, rainbowColorsBrush),
                size = 80.dp,
            )
        }

        Spacer(modifier = Modifier.padding(top = 40.dp))

        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(SpaceMedium),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Total Orders",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = totalOrders.totalOrders.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = totalOrders.totalAmount.toRupee,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .testTag("deliveryTotalAmount"),
            )

            PoposSuggestionChip(
                icon = PoposIcons.CalenderMonth,
                text = selectedDate.toPrettyDate(),
            )
        }

        Spacer(modifier = Modifier.height(SpaceMedium))
    }
}

@Composable
private fun ShareableDeliveryPartnerBody(
    modifier: Modifier = Modifier,
    state: PartnerState.Success,
) = trace("ShareableDeliveryPartnerBody") {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
    ) {
        state.orders.forEach { totalDeliveryPartnerOrder ->
            key(totalDeliveryPartnerOrder.partnerId) {
                DeliveryPartnerCard(
                    order = totalDeliveryPartnerOrder,
                    showPrintBtn = false,
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    height = 60.dp,
                )
            }
        }
    }
}

@Composable
private fun ShareablePartnerDetailsBody(
    modifier: Modifier = Modifier,
    state: PartnerReportState.Success,
) = trace("ShareablePartnerDetailsBody") {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
    ) {
        state.orders.forEach { order ->
            key(order.orderId) {
                DeliveryReportCard(
                    order = order,
                    onClickOrder = {},
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun DialogButtons(
    modifier: Modifier = Modifier,
    shareButtonColor: Color = MaterialTheme.colorScheme.primary,
    onDismiss: () -> Unit = {},
    onClickShare: () -> Unit = {},
    onClickPrintOrder: () -> Unit = {},
) = trace("DialogButtons") {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = SpaceMedium, horizontal = SpaceSmall),
        horizontalArrangement = Arrangement.spacedBy(SpaceMedium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PoposOutlinedIconButton(
            modifier = Modifier
                .size(ButtonSize),
            icon = PoposIcons.Close,
            onClick = onDismiss,
            borderColor = MaterialTheme.colorScheme.error,
        )

        PoposOutlinedIconButton(
            modifier = Modifier
                .size(ButtonSize),
            icon = PoposIcons.Print,
            onClick = onClickPrintOrder,
            borderColor = MaterialTheme.colorScheme.secondary,
        )

        PoposButton(
            modifier = Modifier
                .heightIn(ButtonSize)
                .weight(1f),
            text = "Share Data",
            icon = PoposIcons.Share,
            colors = ButtonDefaults.buttonColors(
                containerColor = shareButtonColor,
            ),
            onClick = onClickShare,
        )
    }
}