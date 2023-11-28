package com.niyaj.order.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.niyaj.common.tags.OrderTestTags.ADD_ITEM_TO_CART
import com.niyaj.common.tags.OrderTestTags.ORDER_NOT_AVAILABLE
import com.niyaj.common.tags.OrderTestTags.SEARCH_ORDER_NOT_AVAILABLE
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Order
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator

@Composable
fun OrderedItemLayout(
    modifier: Modifier = Modifier,
    orders: List<Order>,
    isLoading: Boolean = false,
    showSearchBar: Boolean = false,
    onClickPrintOrder: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onMarkedAsProcessing: (Int) -> Unit,
    onNavigateToHomeScreen: () -> Unit,
    onClickOrderDetails: (Int) -> Unit,
    onClickEditOrder: (Int) -> Unit,
    onClickShareOrder: (Int) -> Unit,
) {
    if (orders.isEmpty()) {
        ItemNotAvailable(
            text = if (showSearchBar) SEARCH_ORDER_NOT_AVAILABLE else ORDER_NOT_AVAILABLE,
            buttonText = ADD_ITEM_TO_CART,
            image = painterResource(R.drawable.emptycarttwo),
            onClick = {
                onNavigateToHomeScreen()
            }
        )
    } else if (isLoading) {
        LoadingIndicator()
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalAlignment = Alignment.Start,
        ) {
            items(
                items = orders,
                key = {
                    it.orderId
                }
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