package com.niyaj.cart

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.niyaj.cart.components.CartTabItem
import com.niyaj.cart.components.Tabs
import com.niyaj.cart.components.TabsContent
import com.niyaj.cart.dine_in.DineInScreen
import com.niyaj.cart.dine_out.DineOutScreen
import com.niyaj.ui.components.StandardScaffoldWithBottomNavigation
import com.niyaj.ui.utils.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@OptIn(ExperimentalFoundationApi::class)
@RootNavGraph(start = true)
@Destination(
    route = Screens.CartScreen
)
@Composable
fun CartScreen(
    navController: NavController,
    onClickEditOrder: (Int) -> Unit,
    onClickOrderDetails: (Int) -> Unit,
    onNavigateToOrderScreen: () -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { 2 }
    )

    StandardScaffoldWithBottomNavigation(
        navController = navController,
        title = "My Cart",
        navActions = {
            IconButton(
                onClick = onNavigateToOrderScreen,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = "go to order screen",
                )
            }
        },
        bottomBar = {},
    ) {
        val tabs = listOf(
            CartTabItem.DineOutItem {
                DineOutScreen(
                    navController = navController,
                    onClickEditOrder = onClickEditOrder,
                    onClickOrderDetails = onClickOrderDetails,
                    onNavigateToOrderScreen = onNavigateToOrderScreen,
                )
            },
            CartTabItem.DineInItem {
                DineInScreen(
                    navController = navController,
                    onClickEditOrder = onClickEditOrder,
                    onClickOrderDetails = onClickOrderDetails,
                    onNavigateToOrderScreen = onNavigateToOrderScreen,
                )
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Tabs(tabs = tabs, pagerState = pagerState)

            TabsContent(tabs = tabs, pagerState = pagerState)
        }
    }
}