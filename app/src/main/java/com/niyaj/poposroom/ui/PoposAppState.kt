package com.niyaj.poposroom.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.niyaj.data.utils.NetworkMonitor
import com.niyaj.ui.components.TopLevelDestination
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackDisposableJank
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun rememberPoposAppState(
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator()
): PoposAppState {
    NavigationTrackingSideEffect(navController)
    return remember(
        navController,
        bottomSheetNavigator,
        coroutineScope,
        windowSizeClass,
        networkMonitor,
    ) {
        PoposAppState(
            navController,
            bottomSheetNavigator,
            coroutineScope,
            windowSizeClass,
            networkMonitor,
        )
    }
}

@Stable
class PoposAppState @OptIn(ExperimentalMaterialNavigationApi::class) constructor(
    val navController: NavHostController,
    val bottomSheetNavigator: BottomSheetNavigator,
    coroutineScope: CoroutineScope,
    val windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
) {

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentRouteOrDefault @Composable get() = currentDestination?.route ?: Screens.HOME_SCREEN


    private val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            Screens.HOME_SCREEN -> TopLevelDestination.HOME
            Screens.CART_SCREEN -> TopLevelDestination.CART
            Screens.ORDER_SCREEN -> TopLevelDestination.ORDERS
            Screens.REPORT_SCREEN -> TopLevelDestination.REPORTS
            else -> null
        }

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries


    val shouldShowBottomBar: Boolean
        @Composable get() = currentTopLevelDestination != null && currentTopLevelDestination != TopLevelDestination.CART


    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )


    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

            when (topLevelDestination) {
                TopLevelDestination.HOME -> navController.navigate(
                    Screens.HOME_SCREEN,
                    topLevelNavOptions
                )

                TopLevelDestination.CART -> navController.navigate(
                    Screens.CART_SCREEN,
                    topLevelNavOptions
                )

                TopLevelDestination.ORDERS -> navController.navigate(
                    Screens.ORDER_SCREEN,
                    topLevelNavOptions
                )

                TopLevelDestination.REPORTS -> navController.navigate(
                    Screens.REPORT_SCREEN,
                    topLevelNavOptions
                )
            }
        }
    }


    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelRoute: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelRoute: String) {
        trace("Navigation: $topLevelRoute") {
            val topLevelNavOptions = navOptions {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }

            navController.navigate(topLevelRoute, topLevelNavOptions)
        }
    }

    fun navigateToScreen(route: String) {
        navController.navigate(route)
    }
}

/**
 * Stores information about navigation events to be used with JankStats
 */
@Composable
private fun NavigationTrackingSideEffect(navController: NavHostController) {
    TrackDisposableJank(navController) { metricsHolder ->
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            metricsHolder.state?.putState("Navigation", destination.route.toString())
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
}





