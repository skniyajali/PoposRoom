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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.OrderType
import com.niyaj.model.ProductWiseOrder
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.OrderTab
import com.niyaj.ui.components.OrderTabs
import com.niyaj.ui.components.OrderTabsContent
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ProductWiseOrderPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ProductOrderDetails(
    orderState: UiState<List<ProductWiseOrder>>,
    productPrice: Int,
    onClickOrder: (Int) -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { 2 },
) = trace("ProductOrderDetails") {
    Surface(
        modifier = modifier
            .testTag("RecentOrders"),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 2.dp,
        tonalElevation = 2.dp,
    ) {
        Crossfade(
            targetState = orderState,
            label = "ProductOrderState",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailableHalf(
                        text = "Have not placed any order on this product.",
                        modifier = Modifier.height(IntrinsicSize.Min),
                    )
                }

                is UiState.Success -> {
                    val dineInOrders by remember(state.data) {
                        derivedStateOf {
                            state.data
                                .filter { it.orderType == OrderType.DineIn }
                                .groupBy { it.orderedDate.toPrettyDate() }
                        }
                    }
                    val dineOutOrders by remember(state.data) {
                        derivedStateOf {
                            state.data
                                .filter { it.orderType == OrderType.DineOut }
                                .groupBy { it.orderedDate.toPrettyDate() }
                        }
                    }

                    val tabs = listOf(
                        OrderTab.DineOutOrder {
                            GroupedProductOrders(
                                groupedByDate = dineOutOrders,
                                productPrice = productPrice,
                                onClickOrder = onClickOrder,
                            )
                        },
                        OrderTab.DineInOrder {
                            GroupedProductOrders(
                                groupedByDate = dineInOrders,
                                productPrice = productPrice,
                                onClickOrder = onClickOrder,
                            )
                        },
                    )

                    Column(
                        modifier = modifier,
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                    ) {
                        OrderTabs(
                            tabs = listOf(
                                OrderTab.DineOutOrder(dineOutOrders.isNotEmpty()),
                                OrderTab.DineInOrder(dineInOrders.isNotEmpty()),
                            ),
                            pagerState = pagerState,
                            containerColor = MaterialTheme.colorScheme.background,
                        )

                        OrderTabsContent(
                            tabs = tabs,
                            pagerState = pagerState,
                            modifier = Modifier
                                .fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupedProductOrders(
    groupedByDate: Map<String, List<ProductWiseOrder>>,
    productPrice: Int,
    onClickOrder: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart),
        ) {
            groupedByDate.forEach { (date, groupedOrders) ->
                val totalSales = groupedOrders
                    .sumOf { it.quantity }
                    .times(productPrice).toString()

                TextWithCount(
                    text = date,
                    count = groupedOrders.size,
                    modifier = Modifier
                        .background(Color.Transparent),
                    trailingText = totalSales.toRupee,
                    leadingIcon = PoposIcons.CalenderMonth,
                )

                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                groupedOrders.forEachIndexed { index, order ->
                    key(order.orderId) {
                        OrderDetailsCard(
                            order = order,
                            onClickOrder = onClickOrder,
                        )
                    }

                    if (index != groupedOrders.size - 1) {
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderDetailsCard(
    order: ProductWiseOrder,
    onClickOrder: (Int) -> Unit,
    modifier: Modifier = Modifier,
) = trace("OrderDetailsCard") {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        onClick = { onClickOrder(order.orderId) },
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(2.5f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconWithText(
                        text = "${order.orderId}",
                        icon = PoposIcons.Tag,
                        isTitle = true,
                    )

                    Text(
                        text = "${order.quantity} Qty",
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Text(
                        text = order.orderedDate.toTime,
                        textAlign = TextAlign.End,
                    )
                }

                order.customerAddress?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = SpaceSmall))
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                SpaceSmall,
                                Alignment.CenterHorizontally,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = it,
                                textAlign = TextAlign.End,
                            )

                            order.customerPhone?.let {
                                Text(
                                    text = it,
                                    textAlign = TextAlign.End,
                                )
                            }
                        }
                    }
                }
            }

            FilledTonalIconButton(
                onClick = { onClickOrder(order.orderId) },
                shape = RoundedCornerShape(SpaceMini),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Icon(imageVector = PoposIcons.ArrowRightAlt, contentDescription = "View Details")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@DevicePreviews
@Composable
private fun ProductOrderDetailsPreview(
    @PreviewParameter(ProductWiseOrderPreviewParameter::class)
    orderState: UiState<List<ProductWiseOrder>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ProductOrderDetails(
            modifier = modifier,
            orderState = orderState,
            productPrice = 120,
            onClickOrder = {},
        )
    }
}
