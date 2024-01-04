package com.niyaj.product.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.OrderType
import com.niyaj.model.ProductWiseOrder
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.OrderTab
import com.niyaj.ui.components.OrderTabs
import com.niyaj.ui.components.OrderTabsContent
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.event.UiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductOrderDetails(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    orderState: UiState<List<ProductWiseOrder>>,
    productPrice: Int,
    onClickOrder: (Int) -> Unit,
) = trace("ProductOrderDetails") {
    ElevatedCard(
        modifier = modifier
            .testTag("RecentOrders")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Crossfade(
            targetState = orderState,
            label = "ProductOrderState",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start),
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = "Have not placed any order on this product."
                    )
                }

                is UiState.Success -> {
                    val dineInOrders = remember {
                        derivedStateOf {
                            state.data.filter { it.orderType == OrderType.DineIn }
                        }
                    }
                    val dineOutOrders = remember {
                        derivedStateOf {
                            state.data.filter { it.orderType == OrderType.DineOut }
                        }
                    }

                    RecentOrdersTabbed(
                        pagerState = pagerState,
                        dineInOrders = dineInOrders.value,
                        dineOutOrders = dineOutOrders.value,
                        productPrice = productPrice,
                        onClickOrder = onClickOrder
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecentOrdersTabbed(
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState{ 2 },
    dineInOrders: List<ProductWiseOrder>,
    dineOutOrders: List<ProductWiseOrder>,
    productPrice: Int,
    onClickOrder: (Int) -> Unit,
) = trace("RecentOrdersTabbed") {
    val tabs = listOf(
        OrderTab.DineOutOrder {
            DineOutProductOrders(
                orderList = dineOutOrders,
                productPrice = productPrice,
                onClickOrder = onClickOrder
            )
        },
        OrderTab.DineInOrder {
            DineInProductOrders(
                orderList = dineInOrders,
                productPrice = productPrice,
                onClickOrder = onClickOrder
            )
        },
    )

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        OrderTabs(
            tabs = tabs,
            pagerState = pagerState,
            containerColor = MaterialTheme.colorScheme.background
        )
        OrderTabsContent(tabs = tabs, pagerState = pagerState)
    }
}

@Composable
fun DineInProductOrders(
    modifier: Modifier = Modifier,
    orderList: List<ProductWiseOrder>,
    productPrice: Int,
    onClickOrder: (Int) -> Unit,
) = trace("DineInProductOrders") {
    val groupedByDate = orderList.groupBy { it.orderedDate.toPrettyDate() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = SpaceSmall),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        groupedByDate.forEach { (date, groupedOrders) ->
            val totalSales = groupedOrders
                .sumOf { it.quantity }
                .times(productPrice).toString()

            TextWithCount(
                modifier = Modifier
                    .background(Color.Transparent),
                text = date,
                trailingText = totalSales.toRupee,
                count = groupedOrders.size,
                leadingIcon = Icons.Default.CalendarMonth
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            groupedOrders.forEachIndexed { index, order ->
                key(order.orderId) {
                    OrderDetailsCard(
                        order = order,
                        onClickOrder = onClickOrder
                    )
                }

                if (index != orderList.size - 1 && index != groupedOrders.size - 1) {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
    }
}

@Composable
fun DineOutProductOrders(
    modifier: Modifier = Modifier,
    orderList: List<ProductWiseOrder>,
    productPrice: Int,
    onClickOrder: (Int) -> Unit,
) = trace("DineOutProductOrders") {
    val groupedByDate = orderList.groupBy { it.orderedDate.toPrettyDate() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = SpaceSmall),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        groupedByDate.forEach { (date, groupedOrders) ->
            val totalSales = groupedOrders
                .sumOf { it.quantity }
                .times(productPrice).toString()

            TextWithCount(
                modifier = Modifier
                    .background(Color.Transparent),
                text = date,
                trailingText = totalSales.toRupee,
                count = groupedOrders.size,
                leadingIcon = Icons.Default.CalendarMonth
            )

            groupedOrders.forEachIndexed { index, order ->
                key(order.orderId) {
                    OrderDetailsCard(
                        order = order,
                        onClickOrder = onClickOrder
                    )
                }

                if (index != orderList.size - 1 && index != groupedOrders.size - 1) {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
    }
}


@Composable
fun OrderDetailsCard(
    modifier: Modifier = Modifier,
    order: ProductWiseOrder,
    onClickOrder: (Int) -> Unit,
) = trace("OrderDetailsCard") {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable {
                onClickOrder(order.orderId)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(2.5f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconWithText(
                    text = "${order.orderId}",
                    icon = Icons.Default.Tag,
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
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
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
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(imageVector = Icons.Default.ArrowOutward, contentDescription =  "View Details")
        }
    }
}