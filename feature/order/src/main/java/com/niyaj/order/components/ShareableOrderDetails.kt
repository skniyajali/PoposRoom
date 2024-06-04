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

package com.niyaj.order.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.compose.ui.window.DialogProperties
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposOutlinedIconButton
import com.niyaj.designsystem.components.PoposSuggestionChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.LightColor10
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.LightColor9
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.gradient6
import com.niyaj.designsystem.theme.gradient7
import com.niyaj.designsystem.theme.rainbowColorsBrush
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartOrder
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.OrderDetails
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType
import com.niyaj.ui.components.AnimatedTextDividerDashed
import com.niyaj.ui.components.CartAddOnItems
import com.niyaj.ui.components.CartChargesItem
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.drawRainbowBorder
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.CaptureController
import com.niyaj.ui.utils.ScrollableCapturable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareableOrderDetails(
    modifier: Modifier = Modifier,
    captureController: CaptureController,
    orderDetails: OrderDetails,
    charges: List<Charges>,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
    onClickPrintOrder: () -> Unit,
) = trace("ShareableOrderDetails") {
    var changeLayout by remember { mutableStateOf(false) }

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
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                val icon = if (orderDetails.cartOrder.orderType == OrderType.DineIn) {
                    PoposIcons.DinnerDining
                } else {
                    PoposIcons.DeliveryDining
                }
                val containerColor = if (orderDetails.cartOrder.orderType == OrderType.DineIn) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.primary
                }

                val backgroundColor = if (orderDetails.cartOrder.orderType == OrderType.DineIn) {
                    gradient6
                } else {
                    gradient7
                }

                CapturableCard(
                    modifier = Modifier.weight(2.5f),
                    captureController = captureController,
                    orderDetails = orderDetails,
                    charges = charges,
                    containerColor = containerColor,
                    backgroundColor = backgroundColor,
                    icon = icon,
                    onCaptured = onCaptured,
                    layoutChanged = changeLayout,
                )

                DialogButtons(
                    shareButtonColor = containerColor,
                    layoutChanged = changeLayout,
                    onDismiss = onDismiss,
                    onClickShare = onClickShare,
                    onClickChangeLayout = {
                        changeLayout = !changeLayout
                    },
                    onClickPrintOrder = onClickPrintOrder,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareableOrderDetails(
    modifier: Modifier = Modifier,
    captureController: CaptureController,
    orderDetails: UiState<OrderDetails>,
    charges: List<Charges>,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
    onClickPrintOrder: (orderId: Int) -> Unit,
) = trace("ShareableOrderDetails") {
    var changeLayout by remember { mutableStateOf(false) }

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
                targetState = orderDetails,
                label = "OrderDetails",
            ) { state ->
                when (state) {
                    UiState.Empty -> LoadingIndicator()
                    UiState.Loading -> LoadingIndicator()
                    is UiState.Success -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            val icon = if (state.data.cartOrder.orderType == OrderType.DineIn) {
                                PoposIcons.DinnerDining
                            } else {
                                PoposIcons.DeliveryDining
                            }
                            val containerColor =
                                if (state.data.cartOrder.orderType == OrderType.DineIn) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }

                            val backgroundColor =
                                if (state.data.cartOrder.orderType == OrderType.DineIn) {
                                    gradient6
                                } else {
                                    gradient7
                                }

                            CapturableCard(
                                modifier = Modifier.weight(2.5f),
                                captureController = captureController,
                                orderDetails = state.data,
                                charges = charges,
                                containerColor = containerColor,
                                backgroundColor = backgroundColor,
                                icon = icon,
                                onCaptured = onCaptured,
                                layoutChanged = changeLayout,
                            )

                            DialogButtons(
                                shareButtonColor = containerColor,
                                layoutChanged = changeLayout,
                                onDismiss = onDismiss,
                                onClickShare = onClickShare,
                                onClickChangeLayout = {
                                    changeLayout = !changeLayout
                                },
                                onClickPrintOrder = {
                                    onClickPrintOrder(state.data.cartOrder.orderId)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CapturableCard(
    modifier: Modifier,
    captureController: CaptureController,
    orderDetails: OrderDetails,
    charges: List<Charges>,
    containerColor: Color,
    backgroundColor: Brush,
    icon: ImageVector,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
    layoutChanged: Boolean,
) = trace("CapturableCard") {
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
            Crossfade(
                targetState = layoutChanged,
                label = "LayoutChanged",
            ) {
                if (it) {
                    CartItemOrderDetails(
                        orderDetails = orderDetails,
                        charges = charges,
                        icon = icon,
                        containerColor = containerColor,
                    )
                } else {
                    CartItemOrderDetailsCard(
                        orderDetails = orderDetails,
                        charges = charges,
                        icon = icon,
                        containerColor = containerColor,
                        backgroundColor = backgroundColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemOrderDetails(
    modifier: Modifier = Modifier,
    orderDetails: OrderDetails,
    charges: List<Charges>,
    icon: ImageVector,
    containerColor: Color,
) = trace("CartItemOrderDetails") {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            ShareableCartOrderDetails(
                cartOrder = orderDetails.cartOrder,
                icon = icon,
                color = containerColor,
            )

            if (orderDetails.cartProducts.isNotEmpty()) {
                CartItemOrderProductDetails(
                    orderType = orderDetails.cartOrder.orderType,
                    doesChargesIncluded = orderDetails.cartOrder.doesChargesIncluded,
                    cartProducts = orderDetails.cartProducts,
                    addOnItems = orderDetails.addOnItems,
                    charges = charges,
                    additionalCharges = orderDetails.charges,
                    orderPrice = orderDetails.orderPrice,
                )
            } else {
                Text(text = "Looks, like you have not added any product!")
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CartItemOrderDetailsCard(
    modifier: Modifier = Modifier,
    orderDetails: OrderDetails,
    charges: List<Charges>,
    icon: ImageVector,
    containerColor: Color,
    backgroundColor: Brush,
) = trace("CartItemOrderDetailsCard") {
    ElevatedCard(
        modifier = modifier
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
                        .background(backgroundColor),
                )

                CircularBox(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 40.dp),
                    icon = icon,
                    showBorder = true,
                    doesSelected = false,
                    borderStroke = BorderStroke(3.dp, rainbowColorsBrush),
                    unselectedTint = containerColor,
                    size = 80.dp,
                )
            }

            ShareableCartOrderDetailsCard(
                modifier = Modifier.padding(top = 40.dp),
                cartOrder = orderDetails.cartOrder,
                icon = icon,
                color = containerColor,
            )

            ShareableCartProductsDetails(
                cartProducts = orderDetails.cartProducts,
                charges = charges,
                additionalCharges = orderDetails.charges,
                addOnItems = orderDetails.addOnItems,
                orderPrice = orderDetails.orderPrice,
                doesChargesIncluded = orderDetails.cartOrder.doesChargesIncluded,
                orderType = orderDetails.cartOrder.orderType,
            )
        }
    }
}

@Composable
private fun ShareableCartOrderDetailsCard(
    modifier: Modifier,
    cartOrder: CartOrder,
    icon: ImageVector,
    color: Color,
) = trace("ShareableCartOrderDetailsCard") {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceMini),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = "Order Details".uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement
                .spacedBy(SpaceSmall, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PoposSuggestionChip(
                icon = PoposIcons.Tag,
                text = cartOrder.orderId.toString(),
                borderColor = color,
            )

            PoposSuggestionChip(
                icon = icon,
                text = cartOrder.orderType.name,
                borderColor = color,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement
                .spacedBy(SpaceSmall, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PoposSuggestionChip(
                icon = PoposIcons.ModeStandby,
                text = cartOrder.orderStatus.name,
                borderColor = color,
            )

            PoposSuggestionChip(
                icon = PoposIcons.CalenderMonth,
                text = (
                    cartOrder.updatedAt
                        ?: cartOrder.createdAt
                    ).toFormattedDateAndTime,
                borderColor = color,
            )
        }

        if (cartOrder.address.addressName.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement
                    .spacedBy(SpaceSmall, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PoposSuggestionChip(
                    text = cartOrder.customer.customerPhone,
                    icon = PoposIcons.Phone,
                    borderColor = color,
                )

                PoposSuggestionChip(
                    text = cartOrder.address.addressName,
                    icon = PoposIcons.LocationOn,
                    borderColor = color,
                )
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))
    }
}

@Composable
private fun ShareableCartOrderDetails(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder,
    icon: ImageVector,
    color: Color,
) = trace("ShareableCartOrderDetails") {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceMini),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularBox(
            icon = icon,
            showBorder = true,
            doesSelected = false,
            borderStroke = BorderStroke(3.dp, color),
            unselectedTint = color,
            size = 80.dp,
        )

        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = "Order Details".uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement
                .spacedBy(SpaceSmall, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PoposSuggestionChip(
                icon = PoposIcons.Tag,
                text = cartOrder.orderId.toString(),
                borderColor = color,
            )

            PoposSuggestionChip(
                icon = icon,
                text = cartOrder.orderType.name,
                borderColor = color,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement
                .spacedBy(SpaceSmall, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PoposSuggestionChip(
                icon = PoposIcons.ModeStandby,
                text = cartOrder.orderStatus.name,
                borderColor = color,
            )

            PoposSuggestionChip(
                icon = PoposIcons.CalenderMonth,
                text = (
                    cartOrder.updatedAt
                        ?: cartOrder.createdAt
                    ).toFormattedDateAndTime,
                borderColor = color,
            )
        }

        if (cartOrder.address.addressName.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement
                    .spacedBy(SpaceSmall, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PoposSuggestionChip(
                    text = cartOrder.customer.customerPhone,
                    icon = PoposIcons.Phone,
                    borderColor = color,
                )

                PoposSuggestionChip(
                    text = cartOrder.address.addressName,
                    icon = PoposIcons.LocationOn,
                    borderColor = color,
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = SpaceLarge, horizontal = SpaceMini)
                .drawRainbowBorder(2.dp, durationMillis = 5000),
            thickness = SpaceMini,
        )
    }
}

@Composable
private fun ShareableCartProductsDetails(
    modifier: Modifier = Modifier,
    cartProducts: List<CartProductItem>,
    charges: List<Charges>,
    additionalCharges: List<Charges>,
    addOnItems: List<AddOnItem>,
    orderPrice: OrderPrice,
    doesChargesIncluded: Boolean,
    orderType: OrderType,
) = trace("ShareableCartProductsDetails") {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        if (cartProducts.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = SpaceSmall, topEnd = SpaceSmall))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(SpaceSmall),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        SpaceMini,
                        Alignment.CenterHorizontally,
                    ),
                ) {
                    Text(
                        modifier = Modifier
                            .weight(2f, true),
                        text = "Name",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Text(
                        modifier = Modifier.weight(0.5f, true),
                        text = "Price",
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelMedium,
                    )

                    Text(
                        modifier = Modifier.weight(0.5f, true),
                        text = "Qty",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            cartProducts.forEach { cartProduct ->
                CartProduct(cartProduct = cartProduct)
            }

            if (addOnItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                AnimatedTextDividerDashed(text = "AddOn Items")

                CartAddOnItems(addOnItems = addOnItems)
            }

            if (charges.isNotEmpty()) {
                if (doesChargesIncluded && orderType == OrderType.DineOut) {
                    AnimatedTextDividerDashed(text = "Charges")

                    CartChargesItem(chargesList = charges)
                }
            }

            if (additionalCharges.isNotEmpty()) {
                AnimatedTextDividerDashed(text = "Additional Charges")

                CartChargesItem(
                    chargesList = additionalCharges,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.inverseOnSurface)
                    .padding(SpaceSmall),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Sub Total",
                            style = MaterialTheme.typography.bodySmall,
                        )

                        Text(
                            text = orderPrice.basePrice.toRupee,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Discount",
                            style = MaterialTheme.typography.bodySmall,
                        )

                        Text(
                            text = orderPrice.discountPrice.toRupee,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp)

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            modifier = Modifier,
                            text = "Total",
                            fontWeight = FontWeight.SemiBold,
                        )

                        Text(
                            modifier = Modifier,
                            text = "${cartProducts.size} Items",
                            textAlign = TextAlign.Center,
                        )

                        Text(
                            modifier = Modifier,
                            text = orderPrice.totalPrice.toRupee,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        } else {
            Text(text = "Looks, like you have not added any product!")
        }
    }
}

@Composable
private fun CartProduct(
    modifier: Modifier = Modifier,
    cartProduct: CartProductItem,
) = trace("CartProduct") {
    key(cartProduct.productId) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(SpaceMini),
            horizontalArrangement = Arrangement.spacedBy(SpaceMini, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(LightColor9, RoundedCornerShape(4.dp))
                    .weight(1.9f, true),
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = SpaceMini)
                        .align(Alignment.CenterStart),
                    text = cartProduct.productName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(LightColor8, RoundedCornerShape(4.dp))
                    .weight(0.5f, true),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = cartProduct.productPrice.toString().toRupee,
                    textAlign = TextAlign.End,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(LightColor10, RoundedCornerShape(4.dp))
                    .weight(0.3f, true),
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = cartProduct.productQuantity.toString(),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun DialogButtons(
    modifier: Modifier = Modifier,
    layoutChanged: Boolean,
    shareButtonColor: Color,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onClickChangeLayout: () -> Unit,
    onClickPrintOrder: () -> Unit,
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

        PoposOutlinedIconButton(
            modifier = Modifier
                .size(ButtonSize),
            icon = if (layoutChanged) PoposIcons.ViewAgenda else PoposIcons.CalendarViewDay,
            onClick = onClickChangeLayout,
            borderColor = MaterialTheme.colorScheme.primary,
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
