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

package com.niyaj.cart

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.cart.components.CartTabItem
import com.niyaj.cart.components.Tabs
import com.niyaj.cart.components.TabsContent
import com.niyaj.cart.dine_in.DineInScreen
import com.niyaj.cart.dine_out.DineOutScreen
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.ui.components.StandardScaffoldWithBottomNavigation
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalFoundationApi::class)
@RootNavGraph(start = true)
@Destination(route = Screens.CART_SCREEN)
@Composable
fun CartScreen(
    navigator: DestinationsNavigator,
    onClickEditOrder: (Int) -> Unit,
    onClickOrderDetails: (Int) -> Unit,
) {
    TrackScreenViewEvent(screenName = Screens.CART_SCREEN)

    val pagerState = rememberPagerState { 2 }

    BackHandler {
        navigator.navigate(Screens.HOME_SCREEN)
    }

    StandardScaffoldWithBottomNavigation(
        currentRoute = Screens.CART_SCREEN,
        title = "My Cart",
        navActions = {
            IconButton(
                onClick = { navigator.navigate(Screens.ORDER_SCREEN) }
            ) {
                Icon(
                    imageVector = PoposIcons.Order,
                    contentDescription = "go to order screen",
                )
            }
        },
        bottomBar = {},
        showBackButton = true,
        onBackClick = {
            navigator.navigate(Screens.HOME_SCREEN)
        },
        onNavigateToScreen = navigator::navigate,
    ) {
        val tabs = listOf(
            CartTabItem.DineOutItem {
                DineOutScreen(
                    onClickEditOrder = onClickEditOrder,
                    onClickOrderDetails = onClickOrderDetails,
                    onNavigateToOrderScreen = { navigator.navigate(Screens.ORDER_SCREEN) },
                    onClickCreateOrder = { navigator.navigate(Screens.HOME_SCREEN) },
                )
            },
            CartTabItem.DineInItem {
                DineInScreen(
                    onClickEditOrder = onClickEditOrder,
                    onClickOrderDetails = onClickOrderDetails,
                    onNavigateToOrderScreen = { navigator.navigate(Screens.ORDER_SCREEN) },
                    onClickCreateOrder = { navigator.navigate(Screens.HOME_SCREEN) },
                )
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Tabs(
                tabs = listOf(CartTabItem.DineOutItem(), CartTabItem.DineInItem()),
                pagerState = pagerState,
            )

            TabsContent(tabs = tabs, pagerState = pagerState)
        }
    }
}