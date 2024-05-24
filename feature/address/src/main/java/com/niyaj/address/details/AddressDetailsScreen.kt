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

package com.niyaj.address.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.address.components.AddressDetailsCard
import com.niyaj.address.components.RecentOrders
import com.niyaj.address.destinations.AddEditAddressScreenDestination
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.components.TotalOrderDetailsCard
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
                .padding(it),
            contentPadding = PaddingValues(SpaceSmall),
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