package com.niyaj.cart

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
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
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.ui.components.StandardScaffoldWithBottomNavigation
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.currentRoute
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@OptIn(ExperimentalFoundationApi::class)
@RootNavGraph(start = true)
@Destination(route = Screens.CART_SCREEN)
@Composable
fun CartScreen(
    navController: NavController,
    onClickEditOrder: (Int) -> Unit,
    onClickOrderDetails: (Int) -> Unit,
    onNavigateToOrderScreen: () -> Unit,
) {

    TrackScreenViewEvent(screenName = Screens.CART_SCREEN)

    val pagerState = rememberPagerState { 2 }

    StandardScaffoldWithBottomNavigation(
        currentRoute = navController.currentRoute(),
        title = "My Cart",
        navActions = {
            IconButton(
                onClick = onNavigateToOrderScreen,
            ) {
                Icon(
                    imageVector = PoposIcons.Order,
                    contentDescription = "go to order screen",
                )
            }
        },
        bottomBar = {},
        showBackButton = true,
        onBackClick = navController::navigateUp,
        onNavigateToScreen = navController::navigate,
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