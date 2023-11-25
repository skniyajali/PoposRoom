package com.niyaj.product.details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.automirrored.filled.Feed
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.niyaj.common.utils.isSameDay
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toDateString
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.theme.Pewter
import com.niyaj.designsystem.theme.PurpleHaze
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Product
import com.niyaj.model.ProductWiseOrder
import com.niyaj.product.destinations.AddEditProductScreenDestination
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ReportCardBox
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@Destination
@Composable
fun ProductDetailsScreen(
    productId: Int = 0,
    navController: NavController,
    onClickOrder: (Int) -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
) {
    val currentId = navController.currentBackStackEntryAsState()
        .value?.arguments?.getInt("productId") ?: productId

    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val productState = viewModel.product.collectAsStateWithLifecycle().value

    val orderDetailsState = viewModel.orderDetails.collectAsStateWithLifecycle().value

    val totalOrderDetails = viewModel.totalOrders.collectAsStateWithLifecycle().value

    val productPrice = viewModel.productPrice.collectAsStateWithLifecycle().value

    var productDetailsExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var orderDetailsExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    StandardScaffoldNew(
        navController = navController,
        title = "Product Details",
        showBackButton = true,
        showBottomBar = false,
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ) {

            item("TotalOrderDetails") {
                ProductTotalOrdersDetails(details = totalOrderDetails)
            }

            item("ProductDetails") {
                ProductDetails(
                    productState = productState,
                    onExpanded = {
                        productDetailsExpanded = !productDetailsExpanded
                    },
                    doesExpanded = productDetailsExpanded,
                    onClickEdit = {
                        navController.navigate(AddEditProductScreenDestination(currentId))
                    }
                )
            }

            item("OrderDetails") {
                ProductOrderDetails(
                    orderState = orderDetailsState,
                    productPrice = productPrice,
                    onExpanded = {
                        orderDetailsExpanded = !orderDetailsExpanded
                    },
                    doesExpanded = orderDetailsExpanded,
                    onClickOrder = onClickOrder
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductTotalOrdersDetails(
    details: ProductTotalOrderDetails,
) {
    ElevatedCard(
        modifier = Modifier
            .testTag("Calculate TotalOrder")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Total Orders",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(SpaceSmall))
            Spacer(modifier = Modifier.height(SpaceSmall))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = details.totalAmount.toRupee,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("ProductTotalAmount")
                )

                val startDate = details.datePeriod.first
                val endDate = details.datePeriod.second

                if (startDate.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        modifier = Modifier.testTag("DatePeriod")
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(SpaceMini),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = startDate.toBarDate,
                                style = MaterialTheme.typography.labelMedium,
                            )

                            if (endDate.isNotEmpty()) {
                                if (!details.datePeriod.isSameDay()) {
                                    Spacer(modifier = Modifier.width(SpaceMini))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                        contentDescription = "DatePeriod"
                                    )
                                    Spacer(modifier = Modifier.width(SpaceMini))
                                    Text(
                                        text = endDate.toBarDate,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceMedium))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(SpaceMedium),
                maxItemsInEachRow = 2,
            ) {
                ReportCardBox(
                    modifier = Modifier,
                    title = "DineIn Sales",
                    subtitle = details.dineInAmount.toRupee,
                    icon = Icons.Default.RamenDining,
                    minusWidth = 30.dp,
                    containerColor = MaterialTheme.colorScheme.outlineVariant,
                    onClick = {}
                )

                ReportCardBox(
                    modifier = Modifier,
                    title = "DineOut Sales",
                    subtitle = details.dineOutAmount.toRupee,
                    icon = Icons.Default.DeliveryDining,
                    minusWidth = 30.dp,
                    onClick = {}
                )

                if (details.mostOrderQtyDate.isNotEmpty()) {
                    ReportCardBox(
                        modifier = Modifier,
                        title = "Most Sales",
                        subtitle = details.mostOrderQtyDate,
                        icon = Icons.Default.AutoGraph,
                        minusWidth = 30.dp,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = {}
                    )
                }

                if (details.mostOrderItemDate.isNotEmpty()) {
                    ReportCardBox(
                        modifier = Modifier,
                        title = "Most Orders",
                        subtitle = details.mostOrderQtyDate,
                        icon = Icons.Default.Inventory2,
                        minusWidth = 30.dp,
                        containerColor = PurpleHaze,
                        boxColor = Color.White,
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetails(
    productState: UiState<Product>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickEdit: () -> Unit,
) {
    ElevatedCard(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("ProductDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
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
                    text = "Product Details",
                    icon = Icons.AutoMirrored.Filled.Feed,
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Employee",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = productState,
                    label = "Product State"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()
                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Product Details Not Available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall),
                                verticalArrangement = Arrangement.spacedBy(SpaceSmall)
                            ) {
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.productName),
                                    text = "Name - ${state.data.productName}",
                                    icon = Icons.Default.CollectionsBookmark
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.productPrice.toString()),
                                    text = "Price - ${state.data.productPrice.toString().toRupee}",
                                    icon = Icons.Default.CurrencyRupee
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.productAvailability.toString()),
                                    text = "Availability : ${state.data.productAvailability}",
                                    icon = if (state.data.productAvailability)
                                        Icons.Default.RadioButtonChecked
                                    else Icons.Default.RadioButtonUnchecked
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.createdAt.toDateString),
                                    text = "Created At : ${state.data.createdAt.toFormattedDateAndTime}",
                                    icon = Icons.Default.CalendarToday
                                )

                                state.data.updatedAt?.let {
                                    IconWithText(
                                        text = "Updated At : ${it.toFormattedDateAndTime}",
                                        icon = Icons.AutoMirrored.Filled.Login
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductOrderDetails(
    orderState: UiState<List<ProductWiseOrder>>,
    productPrice: Int,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickOrder: (Int) -> Unit,
) {
    ElevatedCard(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
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
                    icon = Icons.Default.AllInbox,
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = orderState,
                    label = "ProductOrderState"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Have not placed any order on this product."
                            )
                        }

                        is UiState.Success -> {
                            val productOrders = state.data
                            val groupedByDate =
                                productOrders.groupBy { it.orderedDate.toPrettyDate() }

                            Column {
                                groupedByDate.forEach { (date, orders) ->
                                    val totalSales = orders
                                        .sumOf { it.quantity }
                                        .times(productPrice).toString()

                                    TextWithCount(
                                        modifier = Modifier
                                            .background(Color.Transparent),
                                        text = date,
                                        trailingText = totalSales.toRupee,
                                        count = orders.size,
                                    )

                                    val grpByOrderType = orders.groupBy { it.orderType }

                                    grpByOrderType.forEach { (orderType, grpOrders) ->
                                        val totalPrice = grpOrders
                                            .sumOf { it.quantity }
                                            .times(productPrice).toString()

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Pewter, RoundedCornerShape(SpaceMini))
                                                .align(Alignment.CenterHorizontally)
                                        ) {
                                            Text(
                                                text = "$orderType - ${totalPrice.toRupee}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .padding(SpaceMini)
                                                    .align(Alignment.Center)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceMini))

                                        grpOrders.forEachIndexed { index, order ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        onClickOrder(order.orderId)
                                                    }
                                                    .padding(SpaceSmall),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                IconWithText(
                                                    text = "${order.orderId}",
                                                    icon = Icons.Default.Tag,
                                                    isTitle = true,
                                                )

                                                Column {
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
                                                    textAlign = TextAlign.Start,
                                                    fontWeight = FontWeight.SemiBold
                                                )

                                                Text(
                                                    text = order.orderedDate.toTime,
                                                    textAlign = TextAlign.End
                                                )
                                            }

                                            if (index != productOrders.size - 1 && index != grpOrders.size - 1) {
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
                }
            }
        )
    }
}