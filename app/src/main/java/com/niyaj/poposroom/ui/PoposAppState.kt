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

package com.niyaj.poposroom.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.niyaj.data.repository.UserDataRepository
import com.niyaj.data.utils.NetworkMonitor
import com.niyaj.data.utils.WorkMonitor
import com.niyaj.ui.utils.TrackDisposableJank
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun rememberPoposAppState(
    networkMonitor: NetworkMonitor,
    userDataRepository: UserDataRepository,
    workMonitor: WorkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
): PoposAppState {
    NavigationTrackingSideEffect(navController)
    return remember(
        navController,
        bottomSheetNavigator,
        coroutineScope,
        networkMonitor,
        workMonitor,
        userDataRepository,
    ) {
        PoposAppState(
            navController,
            bottomSheetNavigator,
            coroutineScope,
            networkMonitor,
            workMonitor,
            userDataRepository,
        )
    }
}

@Stable
class PoposAppState
@OptIn(ExperimentalMaterialNavigationApi::class)
constructor(
    val navController: NavHostController,
    val bottomSheetNavigator: BottomSheetNavigator,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    workMonitor: WorkMonitor,
    userDataRepository: UserDataRepository,
) {
    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val isLoggedIn = userDataRepository
        .isUserLoggedIn
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val reportState = workMonitor.isGeneratingReport.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5_000),
        false,
    )

    val deleteState = workMonitor.isDeletingData.stateIn(
        coroutineScope,
        SharingStarted.WhileSubscribed(5_000),
        false,
    )

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
