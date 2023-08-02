package com.niyaj.ui.components

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Teleport
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.items.dropletbutton.DropletButton
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.Purple
import com.niyaj.ui.utils.Screens


@Stable
internal data class NavigationItem(
    val index: Int,
    val name: String,
    val selected: Boolean,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val onClick: () -> Unit,
)

@Composable
private fun BottomNavigationBar(
    navController: NavController,
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screens.HomeScreen,
            label = {
                val fontWeight = if (currentRoute == Screens.HomeScreen)
                    FontWeight.SemiBold else FontWeight.Normal

                Text(
                    text = "Home",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = fontWeight,
                )
            },
            onClick = {
                navController.navigate(Screens.HomeScreen)
            },
            icon = {
                val icon = if (currentRoute == Screens.HomeScreen) {
                    Icons.Rounded.Home
                } else Icons.Outlined.Home

                Icon(imageVector = icon, contentDescription = "Home")
            }
        )

        NavigationBarItem(
            selected = currentRoute == Screens.CartScreen,
            label = {
                val fontWeight = if (currentRoute == Screens.CartScreen)
                    FontWeight.SemiBold else FontWeight.Normal

                Text(
                    text = "Cart",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = fontWeight,
                )
            },
            onClick = {
                navController.navigate(Screens.CartScreen)
            },
            icon = {
                val icon = if (currentRoute == Screens.CartScreen) {
                    Icons.Rounded.ShoppingCart
                } else Icons.Outlined.ShoppingCart

                Icon(imageVector = icon, contentDescription = "Cart")
            }
        )

        NavigationBarItem(
            selected = currentRoute == Screens.OrderScreen,
            label = {
                val fontWeight = if (currentRoute == Screens.OrderScreen)
                    FontWeight.SemiBold else FontWeight.Normal

                Text(
                    text = "Orders",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = fontWeight,
                )
            },
            onClick = {
                navController.navigate(Screens.OrderScreen)
            },
            icon = {
                val icon = if (currentRoute == Screens.OrderScreen) {
                    Icons.Rounded.Inventory2
                } else Icons.Outlined.Inventory2

                Icon(imageVector = icon, contentDescription = "Orders")
            }
        )

        NavigationBarItem(
            selected = currentRoute == Screens.ReportScreen,
            label = {
                val fontWeight = if (currentRoute == Screens.ReportScreen)
                    FontWeight.SemiBold else FontWeight.Normal

                Text(
                    text = "Reports",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = fontWeight,
                )
            },
            onClick = {
                navController.navigate(Screens.ReportScreen)
            },
            icon = {
                val icon = if (currentRoute == Screens.OrderScreen) {
                    Icons.Rounded.Assessment
                } else Icons.Outlined.Assessment

                Icon(imageVector = icon, contentDescription = "Reports")
            }
        )
    }
}


@Composable
internal fun AnimatedBottomNavigationBar(
    navController: NavController,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route.hashCode()

    val navItems = listOf(
        NavigationItem(
            index = Screens.HomeScreen.hashCode(),
            name = "Home",
            selected = currentRoute == Screens.HomeScreen.hashCode(),
            selectedIcon = R.drawable.round_home,
            unselectedIcon = R.drawable.outline_home,
            onClick = {
                navController.navigate(Screens.HomeScreen)
            }
        ),
        NavigationItem(
            index = Screens.CartScreen.hashCode(),
            name = "Cart",
            selected = currentRoute == Screens.CartScreen.hashCode(),
            selectedIcon = R.drawable.round_cart,
            unselectedIcon = R.drawable.outline_cart,
            onClick = {
                navController.navigate(Screens.CartScreen)
            }
        ),
        NavigationItem(
            index = Screens.OrderScreen.hashCode(),
            name = "Orders",
            selected = currentRoute == Screens.OrderScreen.hashCode(),
            selectedIcon = R.drawable.round_orders,
            unselectedIcon = R.drawable.outline_orders,
            onClick = {
                navController.navigate(Screens.OrderScreen)
            }
        ),
        NavigationItem(
            index = Screens.ReportScreen.hashCode(),
            name = "Reports",
            selected = currentRoute == Screens.ReportScreen.hashCode(),
            selectedIcon = R.drawable.round_reports,
            unselectedIcon = R.drawable.outline_reports,
            onClick = {
                navController.navigate(Screens.ReportScreen)
            }
        )
    )

    val index = navItems.indexOf(navItems.find { it.index == currentRoute })

    AnimatedNavigationBar(
        modifier = Modifier
            .windowInsetsPadding(windowInsets)
            .height(80.dp),
        selectedIndex = index,
        cornerRadius = shapeCornerRadius(0.dp),
        barColor = LightColor8,
        ballColor = MaterialTheme.colorScheme.secondary,
        ballAnimation = Teleport(tween(Duration, easing = LinearOutSlowInEasing)),
        indentAnimation = Height(
            indentWidth = 56.dp,
            indentHeight = 15.dp,
            animationSpec = tween(
                DoubleDuration,
                easing = { OvershootInterpolator().getInterpolation(it) }
            )
        )
    ) {
        navItems.forEach {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DropletButton(
                    modifier = Modifier.fillMaxWidth(),
                    isSelected = it.selected,
                    onClick = it.onClick,
                    icon = if (it.selected) it.selectedIcon else it.unselectedIcon,
                    dropletColor = Purple,
                    iconColor = MaterialTheme.colorScheme.tertiary,
                    size = 24.dp,
                    animationSpec = tween(durationMillis = Duration, easing = LinearEasing)
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = it.name,
                    color = if (it.selected) Purple else MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (it.selected) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }

}