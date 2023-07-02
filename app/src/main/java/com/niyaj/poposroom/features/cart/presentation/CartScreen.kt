package com.niyaj.poposroom.features.cart.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.niyaj.poposroom.features.cart.presentation.components.CartTabItem
import com.niyaj.poposroom.features.cart.presentation.dine_in.DineInScreen
import com.niyaj.poposroom.features.cart.presentation.dine_out.DineOutScreen
import com.niyaj.poposroom.features.common.components.StandardScaffoldWithBottomNavigation
import com.niyaj.poposroom.features.common.components.Tabs
import com.niyaj.poposroom.features.common.components.TabsContent
import com.niyaj.poposroom.features.destinations.OrderScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun CartScreen(
    navController: NavController,
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
                onClick = {
                    navController.navigate(OrderScreenDestination())
                },
            ){
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = "go to order screen",
                )
            }
        },
        bottomBar = {},
    ) {
        val tabs = listOf(
            CartTabItem.DineOutItem {
                DineOutScreen(navController = navController)
            },
            CartTabItem.DineInItem {
                DineInScreen(navController = navController)
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