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

package com.niyaj.customer.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_RECENT_ORDERS
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CustomerWiseOrder
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CustomerWiseOrderPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun CustomerRecentOrders(
    customerWiseOrders: UiState<List<CustomerWiseOrder>>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickOrder: (Int) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("CustomerRecentOrders") {
    ElevatedCard(
        onClick = onExpanded,
        modifier = modifier
            .testTag(CUSTOMER_RECENT_ORDERS)
            .fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = containerColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        StandardExpandable(
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            content = {
                Crossfade(
                    targetState = customerWiseOrders,
                    label = "Recent Orders",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicatorHalf()

                        is UiState.Empty -> {
                            ItemNotAvailableHalf(text = "No orders made using this customer.")
                        }

                        is UiState.Success -> {
                            Column {
                                val groupedByDate = remember(state.data) {
                                    state.data.groupBy { it.updatedAt.toPrettyDate() }
                                }

                                groupedByDate.forEach { (date, orders) ->
                                    TextWithCount(
                                        text = date,
                                        count = orders.size,
                                        modifier = Modifier
                                            .background(Color.Transparent),
                                    )

                                    orders.forEachIndexed { index, order ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onClickOrder(order.orderId)
                                                }
                                                .padding(SpaceSmall),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(SpaceMini),
                                        ) {
                                            IconWithText(
                                                text = "${order.orderId}",
                                                modifier = Modifier
                                                    .testTag("Order-${order.orderId}")
                                                    .weight(0.5f),
                                                icon = PoposIcons.Tag,
                                                isTitle = true,
                                            )

                                            Text(
                                                modifier = Modifier.weight(1f),
                                                text = order.customerAddress,
                                                textAlign = TextAlign.Start,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )

                                            Text(
                                                modifier = Modifier.weight(0.8f),
                                                text = order.totalPrice.toRupee,
                                                textAlign = TextAlign.End,
                                                fontWeight = FontWeight.SemiBold,
                                            )

                                            Text(
                                                modifier = Modifier.weight(0.7f),
                                                text = order.updatedAt.toTime,
                                                textAlign = TextAlign.End,
                                            )
                                        }

                                        if (index != orders.size - 1) {
                                            Spacer(modifier = Modifier.height(SpaceMini))
                                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                            Spacer(modifier = Modifier.height(SpaceMini))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            rowClickable = true,
            title = {
                IconWithText(
                    text = "Recent Orders",
                    icon = PoposIcons.AllInbox,
                )
            },
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
        )
    }
}

@DevicePreviews
@Composable
private fun CustomerRecentOrdersPreview(
    @PreviewParameter(CustomerWiseOrderPreviewParameter::class)
    customerWiseOrders: UiState<List<CustomerWiseOrder>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CustomerRecentOrders(
            modifier = modifier,
            customerWiseOrders = customerWiseOrders,
            onExpanded = {},
            doesExpanded = true,
            onClickOrder = {},
        )
    }
}
