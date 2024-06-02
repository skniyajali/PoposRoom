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

package com.niyaj.order.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.niyaj.common.tags.OrderTestTags.ADD_ITEM_TO_CART
import com.niyaj.common.tags.OrderTestTags.ORDER_NOT_AVAILABLE
import com.niyaj.common.tags.OrderTestTags.SEARCH_ORDER_NOT_AVAILABLE
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.order.OrderState
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.utils.TrackScrollJank

@Composable
fun OrderedItemLayout(
    modifier: Modifier = Modifier,
    orderState: OrderState,
    showSearchBar: Boolean = false,
    onClickPrintOrder: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onMarkedAsProcessing: (Int) -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    onClickOrderDetails: (Int) -> Unit,
    onClickEditOrder: (Int) -> Unit,
    onClickShareOrder: (Int) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    Crossfade(
        targetState = orderState,
        label = "Order State",
    ) { state ->
        when (state) {
            is OrderState.Loading -> LoadingIndicator()

            is OrderState.Empty -> {
                ItemNotAvailable(
                    text = if (showSearchBar) SEARCH_ORDER_NOT_AVAILABLE else ORDER_NOT_AVAILABLE,
                    buttonText = ADD_ITEM_TO_CART,
                    image = painterResource(R.drawable.emptycarttwo),
                    onClick = {
                        onNavigateToHomeScreen()
                    },
                )
            }

            is OrderState.Success -> {
                TrackScrollJank(scrollableState = lazyListState, stateName = "Order::List")

                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(SpaceSmall),
                    horizontalAlignment = Alignment.Start,
                    state = lazyListState,
                ) {
                    items(
                        items = state.data,
                        key = {
                            it.orderId
                        },
                    ) { order ->
                        OrderedItem(
                            order = order,
                            onClickPrintOrder = onClickPrintOrder,
                            onMarkedAsProcessing = onMarkedAsProcessing,
                            onClickDelete = onClickDelete,
                            onClickViewDetails = onClickOrderDetails,
                            onClickEdit = onClickEditOrder,
                            onClickShareOrder = onClickShareOrder,
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }
                }
            }
        }
    }
}
