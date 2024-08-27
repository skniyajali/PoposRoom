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

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_DETAILS_CARD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_DETAILS_TITLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_LIST
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_RECENT_ORDERS
import com.niyaj.common.tags.CustomerTestTags.TOTAL_ORDER_DETAILS_CARD
import com.niyaj.customer.components.CustomerDetailsCard
import com.niyaj.customer.components.CustomerRecentOrders
import com.niyaj.customer.destinations.AddEditCustomerScreenDestination
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Customer
import com.niyaj.model.CustomerWiseOrder
import com.niyaj.model.TotalOrderDetails
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.TotalOrderDetailsCard
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CustomerPreviewData
import com.niyaj.ui.parameterProvider.CustomerWiseOrderPreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Destination(route = Screens.CUSTOMER_DETAILS_SCREEN)
@Composable
fun CustomerDetailsScreen(
    navigator: DestinationsNavigator,
    onClickOrder: (Int) -> Unit,
    modifier: Modifier = Modifier,
    customerId: Int = 0,
    viewModel: CustomerDetailsViewModel = hiltViewModel(),
) {
    val customerState by viewModel.customerDetails.collectAsStateWithLifecycle()
    val customerWiseOrders by viewModel.orderDetails.collectAsStateWithLifecycle()
    val totalOrders by viewModel.totalOrders.collectAsStateWithLifecycle()

    TrackScreenViewEvent(screenName = Screens.CUSTOMER_DETAILS_SCREEN + "/$customerId")

    CustomerDetailsScreenContent(
        modifier = modifier,
        customerState = customerState,
        customerWiseOrders = customerWiseOrders,
        totalOrders = totalOrders,
        onBackClick = navigator::navigateUp,
        onClickEdit = {
            navigator.navigate(AddEditCustomerScreenDestination(customerId))
        },
        onClickOrder = onClickOrder,
    )
}

@VisibleForTesting
@Composable
internal fun CustomerDetailsScreenContent(
    customerState: UiState<Customer>,
    customerWiseOrders: UiState<List<CustomerWiseOrder>>,
    totalOrders: TotalOrderDetails,
    onBackClick: () -> Unit,
    onClickEdit: () -> Unit,
    onClickOrder: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    var detailsExpanded by rememberSaveable { mutableStateOf(true) }
    var orderExpanded by rememberSaveable { mutableStateOf(true) }

    PoposSecondaryScaffold(
        title = CUSTOMER_DETAILS_TITLE,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = false,
        fabPosition = FabPosition.End,
        navActions = {},
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
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Customer Details::List")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(CUSTOMER_LIST)
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(key = TOTAL_ORDER_DETAILS_CARD) {
                TotalOrderDetailsCard(
                    details = totalOrders,
                    modifier = Modifier.testTag(TOTAL_ORDER_DETAILS_CARD),
                )
            }

            item(key = CUSTOMER_DETAILS_CARD) {
                CustomerDetailsCard(
                    modifier = Modifier.testTag(CUSTOMER_DETAILS_CARD),
                    customerState = customerState,
                    onExpanded = {
                        detailsExpanded = !detailsExpanded
                    },
                    doesExpanded = detailsExpanded,
                    onClickEdit = onClickEdit,
                )
            }

            item(key = CUSTOMER_RECENT_ORDERS) {
                CustomerRecentOrders(
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

@DevicePreviews
@Composable
private fun CustomerDetailsScreenContentPreview(
    @PreviewParameter(CustomerWiseOrderPreviewParameter::class)
    customerWiseOrders: UiState<List<CustomerWiseOrder>>,
    modifier: Modifier = Modifier,
    customer: Customer = CustomerPreviewData.customerList.first(),
    totalOrders: TotalOrderDetails = CustomerPreviewData.sampleTotalOrder,
) {
    PoposRoomTheme {
        CustomerDetailsScreenContent(
            modifier = modifier,
            customerState = UiState.Success(customer),
            customerWiseOrders = customerWiseOrders,
            totalOrders = totalOrders,
            onBackClick = {},
            onClickEdit = {},
            onClickOrder = {},
        )
    }
}
