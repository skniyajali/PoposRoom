package com.niyaj.poposroom.features.order.presentation.components

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
import androidx.navigation.NavController
import com.niyaj.poposroom.R
import com.niyaj.poposroom.features.common.components.ItemNotAvailable
import com.niyaj.poposroom.features.common.components.LoadingIndicator
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.poposroom.features.destinations.MainFeedScreenDestination
import com.niyaj.poposroom.features.destinations.OrderDetailsScreenDestination
import com.niyaj.poposroom.features.order.domain.model.Order
import com.niyaj.poposroom.features.order.domain.utils.OrderTestTags.ADD_ITEM_TO_CART
import com.niyaj.poposroom.features.order.domain.utils.OrderTestTags.ORDER_NOT_AVAILABLE
import com.niyaj.poposroom.features.order.domain.utils.OrderTestTags.SEARCH_ORDER_NOT_AVAILABLE
import com.ramcosta.composedestinations.navigation.navigate

@Composable
fun OrderedItemLayout(
    navController : NavController,
    orders: List<Order>,
    isLoading: Boolean = false,
    showSearchBar: Boolean = false,
    onClickPrintOrder: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onMarkedAsProcessing: (Int) -> Unit,
) {
    if(orders.isEmpty()){
        ItemNotAvailable(
            text = if(showSearchBar) SEARCH_ORDER_NOT_AVAILABLE else ORDER_NOT_AVAILABLE,
            buttonText = ADD_ITEM_TO_CART,
            image = painterResource(R.drawable.emptycarttwo),
            onClick = {
                navController.navigate(MainFeedScreenDestination())
            }
        )
    } else if(isLoading){
        LoadingIndicator()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalAlignment = Alignment.Start,
        ){
            items(
                items = orders,
                key = {
                    it.orderId
                }
            ){ order ->
                OrderedItem(
                    order = order,
                    onClickPrintOrder = onClickPrintOrder,
                    onMarkedAsProcessing = onMarkedAsProcessing,
                    onClickDelete = onClickDelete,
                    onClickViewDetails = {
                        navController.navigate(OrderDetailsScreenDestination(it))
                    },
                    onClickEdit = {
                        navController.navigate(AddEditCartOrderScreenDestination(it))
                    },
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}