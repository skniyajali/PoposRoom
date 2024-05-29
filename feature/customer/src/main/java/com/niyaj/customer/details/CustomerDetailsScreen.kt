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

package com.niyaj.customer.details

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.customer.destinations.AddEditCustomerScreenDestination
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Customer
import com.niyaj.model.CustomerWiseOrder
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardExpandable
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

@Destination(route = Screens.CUSTOMER_DETAILS_SCREEN)
@Composable
fun CustomerDetailsScreen(
    customerId: Int = 0,
    navController: DestinationsNavigator,
    onClickOrder: (Int) -> Unit,
    viewModel: CustomerDetailsViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val customerState = viewModel.customerDetails.collectAsStateWithLifecycle().value

    val customerWiseOrders = viewModel.orderDetails.collectAsStateWithLifecycle().value

    val totalOrders = viewModel.totalOrders.collectAsStateWithLifecycle().value

    var detailsExpanded by remember {
        mutableStateOf(true)
    }
    var orderExpanded by remember {
        mutableStateOf(true)
    }

    TrackScreenViewEvent(screenName = Screens.CUSTOMER_DETAILS_SCREEN + "/$customerId")

    PoposSecondaryScaffold(
        title = "Customer Details",
        showBackButton = true,
        showBottomBar = false,
        navActions = {},
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
        onBackClick = navController::navigateUp,
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Customer Details::List")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item(key = "TotalOrder Details") {
                TotalOrderDetailsCard(details = totalOrders)
            }

            item(key = "Address Details") {
                CustomerDetailsCard(
                    customerState = customerState,
                    onExpanded = {
                        detailsExpanded = !detailsExpanded
                    },
                    doesExpanded = detailsExpanded,
                    onClickEdit = {
                        navController.navigate(AddEditCustomerScreenDestination(customerId))
                    },
                )
            }

            item(key = "OrderDetails") {
                RecentOrders(
                    customerWiseOrders = customerWiseOrders,
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
fun CustomerDetailsCard(
    customerState: UiState<Customer>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickEdit: () -> Unit,
) = trace("CustomerDetailsCard") {
    ElevatedCard(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("CustomerDetails")
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
                    text = "Customer Details",
                    icon = PoposIcons.Address,
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit,
                ) {
                    Icon(
                        imageVector = PoposIcons.Edit,
                        contentDescription = "Edit Employee",
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
                    targetState = customerState,
                    label = "Customer State",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()
                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Unable to get customer details",
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
                                    modifier = Modifier.testTag(state.data.customerPhone),
                                    text = "Phone - ${state.data.customerPhone}",
                                    icon = PoposIcons.PhoneAndroid,
                                )
                                state.data.customerName?.let { name ->
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    IconWithText(
                                        modifier = Modifier.testTag(name),
                                        text = "Name - $name",
                                        icon = PoposIcons.Person4,
                                    )
                                }

                                state.data.customerEmail?.let { email ->
                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                    IconWithText(
                                        modifier = Modifier.testTag(email),
                                        text = "Email : $email",
                                        icon = PoposIcons.Email,
                                    )
                                }

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
    customerWiseOrders: UiState<List<CustomerWiseOrder>>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickOrder: (Int) -> Unit,
) = trace("RecentOrders") {
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
                    icon = PoposIcons.AllInbox,
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
                    targetState = customerWiseOrders,
                    label = "Recent Orders",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(text = "No orders made using this customer.")
                        }

                        is UiState.Success -> {
                            Column {
                                val groupedByDate = remember(state.data) {
                                    state.data.groupBy { it.updatedAt.toPrettyDate() }
                                }

                                groupedByDate.forEach { (date, orders) ->
                                    TextWithCount(
                                        modifier = Modifier
                                            .background(Color.Transparent),
                                        text = date,
                                        count = orders.size,
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
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                        ) {
                                            IconWithText(
                                                text = "${order.orderId}",
                                                icon = PoposIcons.Tag,
                                                isTitle = true,
                                            )

                                            Text(
                                                text = order.customerAddress,
                                                textAlign = TextAlign.Start,
                                            )

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
        )
    }
}
