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
import androidx.navigation.compose.rememberNavController
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Teleport
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.items.dropletbutton.DropletButton
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.Purple
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens

@Composable
fun AnimatedBottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier,
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
            },
        ),
        NavigationItem(
            index = Screens.CART_SCREEN.hashCode(),
            name = "Cart",
            selected = currentRoute == Screens.CART_SCREEN.hashCode(),
            selectedIcon = R.drawable.round_cart,
            unselectedIcon = R.drawable.outline_cart,
            onClick = {
                navController.navigate(Screens.CART_SCREEN)
            },
        ),
        NavigationItem(
            index = Screens.ORDER_SCREEN.hashCode(),
            name = "Orders",
            selected = currentRoute == Screens.ORDER_SCREEN.hashCode(),
            selectedIcon = R.drawable.round_orders,
            unselectedIcon = R.drawable.outline_orders,
            onClick = {
                navController.navigate(Screens.ORDER_SCREEN)
            },
        ),
        NavigationItem(
            index = Screens.REPORT_SCREEN.hashCode(),
            name = "Reports",
            selected = currentRoute == Screens.REPORT_SCREEN.hashCode(),
            selectedIcon = R.drawable.round_reports,
            unselectedIcon = R.drawable.outline_reports,
            onClick = {
                navController.navigate(Screens.REPORT_SCREEN)
            },
        ),
    )

    val index = navItems.indexOf(navItems.find { it.index == currentRoute })
    val currentIndex = if (index < 0) 0 else index

    AnimatedNavigationBar(
        modifier = modifier
            .windowInsetsPadding(windowInsets)
            .height(80.dp),
        selectedIndex = currentIndex,
        cornerRadius = shapeCornerRadius(0.dp),
        barColor = LightColor8,
        ballColor = MaterialTheme.colorScheme.secondary,
        ballAnimation = Teleport(tween(DURATION, easing = LinearOutSlowInEasing)),
        indentAnimation = Height(
            indentWidth = 56.dp,
            indentHeight = 15.dp,
            animationSpec = tween(
                DOUBLE_DURATION,
                easing = { OvershootInterpolator().getInterpolation(it) },
            ),
        ),
    ) {
        navItems.forEachIndexed { index, item ->
            key(index) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DropletButton(
                        modifier = Modifier.fillMaxWidth(),
                        isSelected = item.selected,
                        onClick = item.onClick,
                        icon = if (item.selected) item.selectedIcon else item.unselectedIcon,
                        dropletColor = Purple,
                        iconColor = MaterialTheme.colorScheme.tertiary,
                        size = 24.dp,
                        animationSpec = tween(durationMillis = DURATION, easing = LinearEasing),
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = item.name,
                        color = if (item.selected) Purple else MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (item.selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedBottomNavigationBar(
    currentRoute: String,
    onNavigateToDestination: (String) -> Unit,
    modifier: Modifier = Modifier,
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
        ballAnimation = Teleport(tween(DURATION, easing = LinearOutSlowInEasing)),
        indentAnimation = Height(
            indentWidth = 56.dp,
            indentHeight = 15.dp,
            animationSpec = tween(
                DOUBLE_DURATION,
                easing = { OvershootInterpolator().getInterpolation(it) },
            ),
        ),
    ) {
        destinations.forEachIndexed { index, destination ->
            val selected = remember(currentRoute, destination) {
                currentRoute == destination.route
            }

            key(index, destination) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DropletButton(
                        modifier = Modifier.fillMaxWidth(),
                        isSelected = selected,
                        onClick = { onNavigateToDestination(destination.route) },
                        icon = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        dropletColor = Purple,
                        iconColor = MaterialTheme.colorScheme.tertiary,
                        size = 24.dp,
                        animationSpec = tween(durationMillis = DURATION, easing = LinearEasing),
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = destination.label,
                        color = if (selected) Purple else MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Stable
internal data class NavigationItem(
    val index: Int,
    val name: String,
    val selected: Boolean,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val onClick: () -> Unit,
)

internal fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false

@DevicePreviews
@Composable
private fun AnimatedBottomNavigationBarPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AnimatedBottomNavigationBar(
            navController = rememberNavController(),
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun AnimatedBottomNavigationBarRoutePreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AnimatedBottomNavigationBar(
            currentRoute = Screens.HOME_SCREEN,
            onNavigateToDestination = {},
            modifier = modifier,
        )
    }
}
