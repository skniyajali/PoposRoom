package com.niyaj.ui.components

import androidx.compose.runtime.Stable
import com.niyaj.core.ui.R
import com.niyaj.ui.utils.Screens

@Stable
enum class TopLevelDestination(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val label: String,
    val route: String,
) {
    HOME(
        selectedIcon = R.drawable.round_home,
        unselectedIcon = R.drawable.outline_home,
        label = "Home",
        route = Screens.HOME_SCREEN,
    ),
    CART(
        selectedIcon = R.drawable.round_cart,
        unselectedIcon = R.drawable.outline_cart,
        label = "Cart",
        route = Screens.CART_SCREEN,
    ),
    ORDERS(
        selectedIcon = R.drawable.round_orders,
        unselectedIcon = R.drawable.outline_orders,
        label = "Orders",
        route = Screens.ORDER_SCREEN
    ),
    REPORTS(
        selectedIcon = R.drawable.round_reports,
        unselectedIcon = R.drawable.outline_reports,
        label = "Reports",
        route = Screens.REPORT_SCREEN
    )
}