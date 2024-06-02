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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.components.PoposSuggestionChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor1
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CartOrder
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderType
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardExpandable

/**
 * This composable displays the cart order details
 */
@Composable
fun CartOrderDetails(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
    deliveryPartner: EmployeeNameAndId? = null,
) = trace("CartOrderDetails") {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = LightColor1,
        ),
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Order Details",
                    icon = PoposIcons.Order,
                    isTitle = true,
                )
            },
            trailing = {
                PoposSuggestionChip(
                    text = cartOrder.orderStatus.name,
                    icon = PoposIcons.StarHalf,
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    },
                ) {
                    Icon(
                        imageVector = PoposIcons.ArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                ) {
                    IconWithText(
                        text = cartOrder.orderId.toString(),
                        icon = PoposIcons.Tag,
                    )

                    IconWithText(
                        text = "Order Type : ${cartOrder.orderType}",
                        icon = if (cartOrder.orderType == OrderType.DineIn) {
                            PoposIcons.RoomService
                        } else {
                            PoposIcons.DeliveryDining
                        },
                    )

                    IconWithText(
                        text = "Order Status: ${cartOrder.orderStatus.name}",
                        icon = PoposIcons.StarHalf,
                    )

                    deliveryPartner?.let {
                        IconWithText(
                            text = "Delivery Partner: ${it.employeeName}",
                            icon = PoposIcons.Person4,
                        )
                    }

                    IconWithText(
                        text = "Created At : ${cartOrder.createdAt.toPrettyDate()}",
                        icon = PoposIcons.AccessTime,
                    )

                    cartOrder.updatedAt?.let {
                        IconWithText(
                            text = "Updated At : ${it.toPrettyDate()}",
                            icon = PoposIcons.Update,
                        )
                    }
                }
            },
        )
    }
}
