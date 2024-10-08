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

package com.niyaj.cart.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.components.PoposIconButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.OrderType
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.parameterProvider.OrderTypePreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun CartItemOrderDetailsSection(
    selected: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier,
    orderType: OrderType = OrderType.DineIn,
    orderId: String = "",
    customerPhone: String? = "",
    containerColor: Color = if (orderType == OrderType.DineIn) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    },
    iconColor: Color = if (orderType == OrderType.DineIn) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSecondaryContainer
    },
    height: Dp = if (orderType == OrderType.DineIn) 56.dp else 64.dp,
) = trace("CartItemOrderDetailsSection") {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        color = containerColor,
        contentColor = contentColorFor(containerColor),
        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
            ) {
                IconWithText(
                    text = orderId,
                    icon = PoposIcons.Tag,
                    fontWeight = FontWeight.Bold,
                    textColor = iconColor,
                    tintColor = iconColor,
                )

                customerPhone?.let {
                    Spacer(modifier = Modifier.height(SpaceMini))
                    IconWithText(
                        text = it,
                        icon = PoposIcons.PhoneAndroid,
                        fontWeight = FontWeight.Bold,
                        textColor = iconColor,
                        tintColor = iconColor,
                    )
                }
            }

            CartOrderDetailsButtons(
                selected = selected,
                iconColor = iconColor,
                onClick = onClick,
                onEditClick = onEditClick,
                onViewClick = onViewClick,
            )
        }
    }
}

@Composable
private fun CartOrderDetailsButtons(
    selected: Boolean,
    iconColor: Color,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier,
) = trace("CartOrderDetailsButtons") {
    Row(modifier) {
        PoposIconButton(
            icon = PoposIcons.Edit,
            onClick = onEditClick,
            contentDescription = "Edit Cart",
            contentColor = iconColor,
        )

        PoposIconButton(
            icon = PoposIcons.Visibility,
            onClick = onViewClick,
            contentDescription = "Order Details",
            contentColor = iconColor,
        )

        Crossfade(
            targetState = selected,
            label = "selected",
        ) {
            PoposIconButton(
                icon = if (it) PoposIcons.RadioButtonChecked else PoposIcons.RadioButtonUnchecked,
                onClick = onClick,
                contentDescription = "Select Cart",
                contentColor = iconColor,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun CartItemOrderDetailsSectionPreview(
    @PreviewParameter(OrderTypePreviewParameter::class)
    orderType: OrderType,
    modifier: Modifier = Modifier,
    orderId: String = if (orderType == OrderType.DineIn) "DineIn-1" else "DineOut-2",
    customerPhone: String? = if (orderType == OrderType.DineOut) "9078563421" else null,
) {
    PoposRoomTheme {
        CartItemOrderDetailsSection(
            selected = false,
            onClick = {},
            onEditClick = {},
            onViewClick = {},
            modifier = modifier,
            orderType = orderType,
            orderId = orderId,
            customerPhone = customerPhone,
        )
    }
}

@DevicePreviews
@Composable
private fun CartOrderDetailsButtonsPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CartOrderDetailsButtons(
            selected = false,
            iconColor = MaterialTheme.colorScheme.primary,
            onClick = {},
            onEditClick = {},
            onViewClick = {},
            modifier = modifier,
        )
    }
}
