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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
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
internal fun AnimatedBottomNavigationBar(
    navController: NavController,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
) {
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route.hashCode()

    val navItems = listOf(
        NavigationItem(
            index = Screens.HOME_SCREEN.hashCode(),
            name = "Home",
            selected = currentRoute == Screens.HOME_SCREEN.hashCode(),
            selectedIcon = R.drawable.round_home,
            unselectedIcon = R.drawable.outline_home,
            onClick = {
                navController.navigate(Screens.HOME_SCREEN)
            }
        ),
        NavigationItem(
            index = Screens.CART_SCREEN.hashCode(),
            name = "Cart",
            selected = currentRoute == Screens.CART_SCREEN.hashCode(),
            selectedIcon = R.drawable.round_cart,
            unselectedIcon = R.drawable.outline_cart,
            onClick = {
                navController.navigate(Screens.CART_SCREEN)
            }
        ),
        NavigationItem(
            index = Screens.ORDER_SCREEN.hashCode(),
            name = "Orders",
            selected = currentRoute == Screens.ORDER_SCREEN.hashCode(),
            selectedIcon = R.drawable.round_orders,
            unselectedIcon = R.drawable.outline_orders,
            onClick = {
                navController.navigate(Screens.ORDER_SCREEN)
            }
        ),
        NavigationItem(
            index = Screens.REPORT_SCREEN.hashCode(),
            name = "Reports",
            selected = currentRoute == Screens.REPORT_SCREEN.hashCode(),
            selectedIcon = R.drawable.round_reports,
            unselectedIcon = R.drawable.outline_reports,
            onClick = {
                navController.navigate(Screens.REPORT_SCREEN)
            }
        )
    )

    val index = navItems.indexOf(navItems.find { it.index == currentRoute })
    val currentIndex = if (index < 0) 0 else index

    AnimatedNavigationBar(
        modifier = Modifier
            .windowInsetsPadding(windowInsets)
            .height(80.dp),
        selectedIndex = currentIndex,
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
        navItems.forEachIndexed { index, it ->
            key(index) {
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
}


@Composable
internal fun AnimatedBottomNavigationBar(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigateToDestination: (String) -> Unit,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
) {
    val destinations: List<TopLevelDestination> = remember(currentRoute) {
        TopLevelDestination.entries
    }

    val index = remember(currentRoute) {
        destinations.indexOf(destinations.find { it.route == currentRoute })
    }
    val currentIndex = remember(index) { if (index < 0) 0 else index }

    AnimatedNavigationBar(
        modifier = modifier
            .windowInsetsPadding(windowInsets)
            .height(80.dp),
        selectedIndex = currentIndex,
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
        destinations.forEachIndexed { index, it ->
            val selected = remember(currentRoute, it) {
                currentRoute == it.route
            }

            key(index, it) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DropletButton(
                        modifier = Modifier.fillMaxWidth(),
                        isSelected = selected,
                        onClick = { onNavigateToDestination(it.route) },
                        icon = if (selected) it.selectedIcon else it.unselectedIcon,
                        dropletColor = Purple,
                        iconColor = MaterialTheme.colorScheme.tertiary,
                        size = 24.dp,
                        animationSpec = tween(durationMillis = Duration, easing = LinearEasing)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = it.label,
                        color = if (selected) Purple else MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}


internal fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false
