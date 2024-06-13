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

package com.niyaj.product.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.compose.ui.window.DialogProperties
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposOutlinedIconButton
import com.niyaj.designsystem.components.PoposSuggestionChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.Pewter
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.gradient6
import com.niyaj.designsystem.theme.rainbowColorsBrush
import com.niyaj.model.Product
import com.niyaj.model.ProductWiseOrder
import com.niyaj.product.details.ProductTotalOrderDetails
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ProductPreviewData
import com.niyaj.ui.parameterProvider.ProductWiseOrderPreviewParameter
import com.niyaj.ui.utils.CaptureController
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.ScrollableCapturable
import com.niyaj.ui.utils.rememberCaptureController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShareableProductOrderDetails(
    modifier: Modifier = Modifier,
    productState: UiState<Product>,
    totalOrderDetails: ProductTotalOrderDetails,
    ordersState: UiState<List<ProductWiseOrder>>,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
    onClickPrintOrder: () -> Unit,
    captureController: CaptureController = rememberCaptureController(),
) = trace("ShareableProductOrderDetails") {
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
                .fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        ) {
            Crossfade(
                targetState = productState,
                label = "Products State",
            ) { state ->
                when (state) {
                    is UiState.Success -> {
                        when (ordersState) {
                            is UiState.Success -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    CapturableOrderDetailsCard(
                                        modifier = Modifier.weight(3f),
                                        product = state.data,
                                        totalOrder = totalOrderDetails,
                                        orders = ordersState.data,
                                        captureController = captureController,
                                        onCaptured = onCaptured,
                                    )

                                    DialogButtons(
                                        modifier = Modifier,
                                        onDismiss = onDismiss,
                                        onClickShare = onClickShare,
                                        onClickPrintOrder = onClickPrintOrder,
                                    )
                                }
                            }

                            else -> LoadingIndicator()
                        }
                    }

                    else -> LoadingIndicator()
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CapturableOrderDetailsCard(
    modifier: Modifier = Modifier,
    product: Product,
    totalOrder: ProductTotalOrderDetails,
    orders: List<ProductWiseOrder>,
    captureController: CaptureController,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
) = trace("CapturableOrderDetailsCard") {
    ScrollableCapturable(
        controller = captureController,
        onCaptured = onCaptured,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 2.dp,
                ),
                shape = RoundedCornerShape(SpaceSmall),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(gradient6),
                        )

                        CircularBox(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 40.dp),
                            icon = PoposIcons.Feed,
                            text = product.productName,
                            showBorder = true,
                            doesSelected = false,
                            borderStroke = BorderStroke(3.dp, rainbowColorsBrush),
                            size = 80.dp,
                        )
                    }

                    ShareableProductDetails(
                        modifier = Modifier.padding(top = 40.dp),
                        product = product,
                    )

                    ProductTotalOrdersDetails(details = totalOrder)

                    ShareableOrderDetails(orders = orders, productPrice = product.productPrice)
                }
            }
        }
    }
}

@Composable
private fun ShareableOrderDetails(
    modifier: Modifier = Modifier,
    orders: List<ProductWiseOrder>,
    productPrice: Int,
) = trace("ShareableOrderDetails") {
    val groupedOrders = remember(orders) { orders.groupBy { it.orderedDate.toPrettyDate() } }
    // Track the currently loaded dates
    var loadedDates by remember(groupedOrders) { mutableStateOf(groupedOrders.keys.take(1)) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        loadedDates.forEachIndexed { index, date ->
            key(index) {
                ShareableProductOrderDetailsCard(
                    date = date,
                    orders = groupedOrders[date] ?: emptyList(),
                    productPrice = productPrice,
                )
            }
        }

        PaginationButtons(
            modifier = Modifier.padding(top = SpaceSmall),
            showViewMoreBtn = groupedOrders.keys.size != loadedDates.size,
            showViewLessBtn = loadedDates.size > 1,
            onClickViewLess = {
                loadedDates = loadedDates.dropLast(1)
            },
            onClickViewMore = {
                val remainingDates = groupedOrders.keys - loadedDates.toSet()
                if (remainingDates.isNotEmpty()) {
                    loadedDates = loadedDates + remainingDates.take(1)
                }
            },
        )
    }
}

@Composable
private fun ShareableProductOrderDetailsCard(
    modifier: Modifier = Modifier,
    date: String,
    orders: List<ProductWiseOrder>,
    productPrice: Int,
) = trace("ShareableProductOrderDetailsCard") {
    val grpByOrderType = remember { orders.groupBy { it.orderType } }
    val totalSales = remember { orders.sumOf { it.quantity }.times(productPrice) }.toString()

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        TextWithCount(
            modifier = Modifier
                .background(Color.Transparent),
            text = date,
            trailingText = totalSales.toRupee,
            count = orders.size,
            leadingIcon = PoposIcons.CalenderMonth,
        )

        grpByOrderType.forEach { (orderType, grpOrders) ->
            val totalPrice =
                remember { grpOrders.sumOf { it.quantity }.times(productPrice) }.toString()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Pewter)
                    .align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = "$orderType - ${totalPrice.toRupee}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(SpaceMini)
                        .align(Alignment.Center),
                )
            }

            Spacer(modifier = Modifier.height(SpaceMini))

            grpOrders.forEachIndexed { index, order ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
                ) {
                    IconWithText(
                        text = "${order.orderId}",
                        icon = PoposIcons.Tag,
                        isTitle = true,
                        modifier = Modifier.weight(0.5f),
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        order.customerPhone?.let {
                            Text(text = it)
                        }

                        order.customerAddress?.let {
                            Spacer(modifier = Modifier.height(SpaceMini))
                            Text(text = it)
                        }
                    }

                    Text(
                        text = "${order.quantity} Qty",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(0.7f),
                    )

                    Text(
                        text = order.orderedDate.toTime,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(0.8f),
                    )
                }

                if (index != orders.size - 1 && index != grpOrders.size - 1) {
                    Spacer(modifier = Modifier.height(SpaceMini))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(SpaceMini))
                }
            }
        }
    }
}

@Composable
private fun PaginationButtons(
    modifier: Modifier = Modifier,
    showViewMoreBtn: Boolean,
    showViewLessBtn: Boolean,
    onClickViewLess: () -> Unit,
    onClickViewMore: () -> Unit,
) = trace("PaginationButtons") {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // "View Less" button
        AnimatedVisibility(
            visible = showViewLessBtn,
            enter = fadeIn(),
            exit = fadeOut(tween(500)),
        ) {
            FilledTonalButton(
                onClick = onClickViewLess,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Icon(imageVector = PoposIcons.ArrowDropUp, contentDescription = "View Less")
                Text("View Less")
            }
        }

        // "Load More" button
        FilledTonalButton(
            onClick = onClickViewMore,
            enabled = showViewMoreBtn,
        ) {
            Icon(imageVector = PoposIcons.ArrowDropDown, contentDescription = "View More")
            Text("Load More")
        }
    }
}

@Composable
private fun ShareableProductDetails(
    modifier: Modifier = Modifier,
    product: Product,
) = trace("ShareableProductDetails") {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceMini),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = "Product Details".uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement
                .spacedBy(SpaceSmall, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PoposSuggestionChip(
                icon = PoposIcons.CollectionsBookmark,
                text = product.productName,
            )

            PoposSuggestionChip(
                icon = PoposIcons.Rupee,
                text = product.productPrice.toString(),
            )
        }

        PoposSuggestionChip(
            icon = PoposIcons.CalenderMonth,
            text = (product.updatedAt ?: product.createdAt).toFormattedDateAndTime,
        )

        Spacer(modifier = Modifier.height(SpaceMini))
    }
}

@Composable
private fun DialogButtons(
    modifier: Modifier = Modifier,
    shareButtonColor: Color = MaterialTheme.colorScheme.primary,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onClickPrintOrder: () -> Unit,
) = trace("DialogButtons") {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                    .weight(1f),
                text = "Share",
                onClick = onClickShare,
                icon = PoposIcons.Share,
                colors = ButtonDefaults.buttonColors(
                    containerColor = shareButtonColor,
                ),
            )
        }
    }
}

@DevicePreviews
@Composable
private fun ShareableProductOrderDetailsPreview(
    @PreviewParameter(ProductWiseOrderPreviewParameter::class)
    orderState: UiState<List<ProductWiseOrder>>,
    modifier: Modifier = Modifier,
    product: Product = ProductPreviewData.productList.random(),
    totalOrderDetails: ProductTotalOrderDetails = ProductTotalOrderDetails(
        totalAmount = "1200",
        dineInAmount = "600",
        dineInQty = 6,
        dineOutAmount = "600",
        dineOutQty = 6,
        mostOrderItemDate = "1686854400000",
        mostOrderQtyDate = "1687200000000",
        datePeriod = Pair("1685603200000", "1688195200000"),
    ),
) {
    PoposRoomTheme {
        ShareableProductOrderDetails(
            modifier = modifier,
            productState = UiState.Success(product),
            totalOrderDetails = totalOrderDetails,
            ordersState = orderState,
            onDismiss = {},
            onClickShare = {},
            onCaptured = { _, _ -> },
            onClickPrintOrder = {},
        )
    }
}
