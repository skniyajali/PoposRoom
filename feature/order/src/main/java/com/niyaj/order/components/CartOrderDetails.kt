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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.trace
import androidx.compose.ui.window.PopupProperties
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.components.PoposOutlinedDropdownButton
import com.niyaj.designsystem.components.PoposSuggestionChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.Customer
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.utils.DevicePreviews

/**
 * This composable displays the cart order details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CartOrderDetails(
    modifier: Modifier = Modifier,
    cartOrder: CartOrder,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
    deliveryPartner: EmployeeNameAndId? = null,
    partners: List<EmployeeNameAndId>,
    onChangePartner: (Int) -> Unit,
    containerColor: Color = LightColor6,
) = trace("CartOrderDetails") {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val text = if (deliveryPartner == null) "Add" else "Update"
    val icon = if (deliveryPartner == null) PoposIcons.Add else PoposIcons.Edit

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
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

                    Spacer(modifier = Modifier.height(SpaceMini))

                    if (partners.isNotEmpty() && cartOrder.orderType == OrderType.DineOut) {
                        ExposedDropdownMenuBox(
                            modifier = Modifier.fillMaxWidth(),
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            },
                        ) {
                            PoposOutlinedDropdownButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { coordinates ->
                                        // This is used to assign to the DropDown the same width
                                        textFieldSize = coordinates.size.toSize()
                                    }
                                    .menuAnchor(),
                                text = "$text Delivery Partner",
                                leadingIcon = icon,
                                trailingIcon = if (expanded) PoposIcons.KeyboardArrowUp else PoposIcons.ArrowDown,
                                onClick = {
                                    expanded = true
                                },
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {
                                    expanded = false
                                },
                                properties = PopupProperties(
                                    focusable = false,
                                    dismissOnBackPress = true,
                                    dismissOnClickOutside = true,
                                    excludeFromSystemGesture = true,
                                    clippingEnabled = true,
                                ),
                                modifier = Modifier
                                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                            ) {
                                partners.forEachIndexed { i, item ->
                                    DropdownMenuItem(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = {
                                            Text(text = item.employeeName)
                                        },
                                        onClick = {
                                            onChangePartner(item.employeeId)
                                        },
                                    )

                                    if (i != partners.lastIndex) {
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}

@DevicePreviews
@Composable
private fun CartOrderDetailsPreview(
    modifier: Modifier = Modifier,
) {
    CartOrderDetails(
        modifier = modifier,
        cartOrder = CartOrder(
            orderId = 4866,
            orderType = OrderType.DineOut,
            orderStatus = OrderStatus.PROCESSING,
            doesChargesIncluded = false,
            customer = Customer(),
            address = Address(),
            deliveryPartnerId = 4978,
        ),
        doesExpanded = true,
        onExpandChanged = {},
        deliveryPartner = EmployeeNameAndId(
            employeeId = 4978,
            employeeName = "Niyaj",
        ),
        partners = listOf(
            EmployeeNameAndId(
                employeeId = 4978,
                employeeName = "Niyaj",
            ),
            EmployeeNameAndId(
                employeeId = 497,
                employeeName = "Baby",
            ),
        ),
        onChangePartner = {},
    )
}
