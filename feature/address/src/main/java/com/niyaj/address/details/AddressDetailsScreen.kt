package com.niyaj.address.details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.address.destinations.AddEditAddressScreenDestination
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Address
import com.niyaj.model.AddressWiseOrder
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardRoundedFilterChip
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.components.TotalOrderDetailsCard
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination(route = Screens.ADDRESS_DETAILS_SCREEN)
@Composable
fun AddressDetailsScreen(
    addressId: Int = 0,
    navigator: DestinationsNavigator,
    onClickOrder: (Int) -> Unit,
    viewModel: AddressDetailsViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val addressState = viewModel.addressDetails.collectAsStateWithLifecycle().value

    val orderDetailsState = viewModel.orderDetails.collectAsStateWithLifecycle().value

    val totalOrdersState = viewModel.totalOrders.collectAsStateWithLifecycle().value

    var detailsExpanded by remember {
        mutableStateOf(true)
    }
    var orderExpanded by remember {
        mutableStateOf(true)
    }

    TrackScreenViewEvent(screenName = Screens.ADDRESS_DETAILS_SCREEN + addressId)

    StandardScaffoldRouteNew(
        title = "Address Details",
        onBackClick = navigator::navigateUp,
        showFab = true,
        fabPosition = FabPosition.End,
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = !lazyListState.isScrollingUp(),
                fabVisible = lazyListState.isScrollingUp(),
                fabIcon = PoposIcons.Share,
                onFabClick = { /*TODO: Add Share functionality*/ },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
        navActions = {
            IconButton(
                onClick = { /*TODO*/ },
            ) {
                Icon(
                    imageVector = PoposIcons.Share,
                    contentDescription = "Share Address Details",
                )
            }

            IconButton(
                onClick = { /*TODO: Add Print functionality*/ },
            ) {
                Icon(
                    imageVector = PoposIcons.Print,
                    contentDescription = "Print Address Details",
                )
            }
        },
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Address Details::List")

        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
        ) {
            item(key = "TotalOrder Details") {
                TotalOrderDetailsCard(details = totalOrdersState)
            }

            item(key = "Address Details") {
                AddressDetailsCard(
                    addressState = addressState,
                    onExpanded = {
                        detailsExpanded = !detailsExpanded
                    },
                    doesExpanded = detailsExpanded,
                    onClickEdit = {
                        navigator.navigate(AddEditAddressScreenDestination(addressId))
                    },
                )
            }

            item(key = "OrderDetails") {
                RecentOrders(
                    orderDetailsState = orderDetailsState,
                    doesExpanded = orderExpanded,
                    onExpanded = {
                        orderExpanded = !orderExpanded
                    },
                    onClickOrder = onClickOrder,
                )

                Spacer(modifier = Modifier.height(SpaceMedium))
            }
        }
    }
}

@Composable
fun AddressDetailsCard(
    addressState: UiState<Address>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickEdit: () -> Unit,
) = trace("AddressDetailsCard") {
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
                    text = "Address Details",
                    icon = PoposIcons.Address,
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit,
                ) {
                    Icon(
                        imageVector = PoposIcons.Edit,
                        contentDescription = "Edit Address",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            rowClickable = true,
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
                    targetState = addressState,
                    label = "Address State",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Address Details Not Available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall),
                            ) {
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.addressName),
                                    text = "Name - ${state.data.addressName}",
                                    icon = PoposIcons.Address,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.shortName),
                                    text = "Short Name - ${state.data.shortName}",
                                    icon = PoposIcons.Home,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.createdAt.toFormattedDateAndTime),
                                    text = "Created At : ${state.data.createdAt.toPrettyDate()}",
                                    icon = PoposIcons.CalenderToday,
                                )

                                state.data.updatedAt?.let {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    IconWithText(
                                        text = "Updated At : ${it.toFormattedDateAndTime}",
                                        icon = PoposIcons.Login,
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
            rowClickable = true,
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
                                        leadingIcon = PoposIcons.CalenderMonth,
                                        count = orders.size,
                                    )

                                    val groupByCustomer = orders.groupBy { it.customerPhone }

                                    groupByCustomer.forEach { (customerPhone, orderDetails) ->
                                        if (orderDetails.size > 1) {
                                            GroupedOrders(
                                                customerPhone = customerPhone,
                                                orderDetails = orderDetails,
                                                onClickOrder = onClickOrder,
                                            )
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupedOrders(
    customerPhone: String,
    orderDetails: List<AddressWiseOrder>,
    onClickOrder: (Int) -> Unit,
) = trace("Address::GroupedOrders") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconWithText(
                text = customerPhone,
                icon = PoposIcons.PhoneAndroid,
            )

            val startDate = orderDetails.first().updatedAt
            val endDate = orderDetails.last().updatedAt

            Row(
                modifier = Modifier
                    .padding(SpaceMini),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = endDate.toTime,
                    style = MaterialTheme.typography.labelMedium,
                )

                Spacer(modifier = Modifier.width(SpaceMini))
                Icon(
                    imageVector = PoposIcons.ArrowRightAlt,
                    contentDescription = "DatePeriod",
                )
                Spacer(modifier = Modifier.width(SpaceMini))
                Text(
                    text = startDate.toTime,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceMini))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center,
            maxItemsInEachRow = 2,
        ) {
            orderDetails.forEach { order ->
                StandardRoundedFilterChip(
                    text = order.totalPrice.toRupee,
                    icon = PoposIcons.Tag,
                    onClick = {
                        onClickOrder(order.orderId)
                    },
                )

                Spacer(modifier = Modifier.width(SpaceSmall))
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ListOfOrders(
    orderSize: Int,
    orderDetails: List<AddressWiseOrder>,
    onClickOrder: (Int) -> Unit,
) = trace("Address::ListOfOrder") {
    orderDetails.forEachIndexed { index, order ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClickOrder(order.orderId)
                }
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconWithText(
                text = "${order.orderId}",
                icon = PoposIcons.Tag,
                tintColor = MaterialTheme.colorScheme.tertiary,
                isTitle = true,
            )

            Column {
                Text(
                    text = order.customerPhone,
                    textAlign = TextAlign.Start,
                )

                order.customerName?.let {
                    Spacer(
                        modifier = Modifier.height(
                            SpaceMini,
                        ),
                    )
                    Text(text = it)
                }
            }

            Text(
                text = order.totalPrice.toRupee,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = order.updatedAt.toTime,
                textAlign = TextAlign.End,
            )
        }

        if (index != orderSize - 1) {
            Spacer(modifier = Modifier.height(SpaceMini))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceMini))
        }
    }
}