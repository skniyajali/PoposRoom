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

package com.niyaj.address.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddressWiseOrder
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.event.UiState

@Composable
fun RecentOrders(
    orderDetailsState: UiState<List<AddressWiseOrder>>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickOrder: (Int) -> Unit,
) = trace("Address::RecentOrders") {
    ElevatedCard(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                IconWithText(
                    text = "Recent Orders",
                    icon = PoposIcons.Order,
                )
            },
            rowClickable = false,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded,
                ) {
                    Icon(
                        imageVector = PoposIcons.ArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = orderDetailsState,
                    label = "Recent Orders State",
                ) { orders ->
                    when (orders) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(text = "No orders made using this address.")
                        }

                        is UiState.Success -> {
                            val groupedByDate = remember(orders.data) {
                                orders.data.groupBy { it.updatedAt.toPrettyDate() }
                            }

                            Column {
                                groupedByDate.forEach { (date, orders) ->
                                    TextWithCount(
                                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow),
                                        text = date,
                                        count = orders.size,
                                        leadingIcon = PoposIcons.CalenderMonth,
                                    )

                                    val groupByCustomer = remember(orders) {
                                        orders.groupBy { it.customerPhone }
                                    }

                                    groupByCustomer.forEach { (customerPhone, orderDetails) ->
                                        if (orderDetails.size > 1) {
                                            GroupedOrders(
                                                customerPhone = customerPhone,
                                                orderDetails = orderDetails,
                                                onClickOrder = onClickOrder,
                                            )

                                            HorizontalDivider()
                                        } else {
                                            ListOfOrders(
                                                orderSize = orders.size,
                                                orderDetails = orderDetails,
                                                onClickOrder = onClickOrder,
                                            )
                                        }
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