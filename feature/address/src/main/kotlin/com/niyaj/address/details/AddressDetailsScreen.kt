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

package com.niyaj.address.details

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.address.components.AddressDetailsCard
import com.niyaj.address.components.RecentOrders
import com.niyaj.address.destinations.AddEditAddressScreenDestination
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Address
import com.niyaj.model.AddressWiseOrder
import com.niyaj.model.TotalOrderDetails
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.TotalOrderDetailsCard
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddressPreviewData
import com.niyaj.ui.parameterProvider.AddressWiseOrderPreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Destination(route = Screens.ADDRESS_DETAILS_SCREEN)
@Composable
fun AddressDetailsScreen(
    addressId: Int,
    navigator: DestinationsNavigator,
    onClickOrder: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddressDetailsViewModel = hiltViewModel(),
) {
    val addressState by viewModel.addressDetails.collectAsStateWithLifecycle()
    val orderDetailsState by viewModel.orderDetails.collectAsStateWithLifecycle()
    val totalOrdersState by viewModel.totalOrders.collectAsStateWithLifecycle()

    TrackScreenViewEvent(screenName = Screens.ADDRESS_DETAILS_SCREEN + addressId)

    AddressDetailsScreenContent(
        addressState = addressState,
        orderDetailsState = orderDetailsState,
        totalOrdersState = totalOrdersState,
        onBackClick = navigator::navigateUp,
        onClickEdit = {
            navigator.navigate(AddEditAddressScreenDestination(addressId))
        },
        onClickOrder = onClickOrder,
        modifier = modifier,
    )
}

@VisibleForTesting
@Composable
internal fun AddressDetailsScreenContent(
    addressState: UiState<Address>,
    orderDetailsState: UiState<List<AddressWiseOrder>>,
    totalOrdersState: TotalOrderDetails,
    onBackClick: () -> Unit,
    onClickEdit: () -> Unit,
    onClickOrder: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    var detailsExpanded by rememberSaveable {
        mutableStateOf(true)
    }
    var orderExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    PoposSecondaryScaffold(
        title = "Address Details",
        onBackClick = onBackClick,
        modifier = modifier,
        showFab = true,
        fabPosition = FabPosition.End,
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
        floatingActionButton = {
            StandardFAB(
                fabVisible = lazyListState.isScrollingUp(),
                onFabClick = { /*TODO: Add Share functionality*/ },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                showScrollToTop = !lazyListState.isScrollingUp(),
                fabIcon = PoposIcons.Share,
            )
        },
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Address Details::List")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
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
                    expanded = detailsExpanded,
                    onClickEdit = onClickEdit,
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
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddressDetailsScreenContentPreview(
    @PreviewParameter(AddressWiseOrderPreviewParameter::class)
    ordersState: UiState<List<AddressWiseOrder>>,
    addressState: UiState<Address> = UiState.Success(AddressPreviewData.addressList.first()),
    totalOrderDetails: TotalOrderDetails = AddressPreviewData.sampleTotalOrder,
) {
    PoposRoomTheme {
        AddressDetailsScreenContent(
            addressState = addressState,
            orderDetailsState = ordersState,
            totalOrdersState = totalOrderDetails,
            onBackClick = {},
            onClickEdit = {},
            onClickOrder = {},
            modifier = Modifier,
        )
    }
}
