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

package com.niyaj.testing.util

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCallsBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.OpenResultRecipient
import com.ramcosta.composedestinations.spec.NavGraphSpec

/**
 *  Navigation controller
 *  @author Sk Niyaj Ali
 *  @param modifier
 *  @param navGraphSpec
 */
@OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class,
)
@Composable
fun PoposTestNavHost(
    appState: PoposTestAppState,
    navGraphSpec: NavGraphSpec,
    modifier: Modifier = Modifier,
    manualComposableCallsBuilder: ManualComposableCallsBuilder.() -> Unit = {},
) {
    val bottomSheetNavigator = appState.bottomSheetNavigator
    appState.navController.navigatorProvider += bottomSheetNavigator

    val navHostEngine = rememberAnimatedNavHostEngine(
        navHostContentAlignment = Alignment.TopCenter,
        // default `rootDefaultAnimations` means no animations
        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
    )

    ModalBottomSheetLayout(
        modifier = modifier,
        bottomSheetNavigator = bottomSheetNavigator,
    ) {
        DestinationsNavHost(
            engine = navHostEngine,
            modifier = Modifier,
            navController = appState.navController,
            startRoute = navGraphSpec.startRoute,
            navGraph = navGraphSpec,
            dependenciesContainerBuilder = {
                dependency(navController)
            },
            manualComposableCallsBuilder = manualComposableCallsBuilder,
        )
    }
}


class EmptyOpenResultRecipient<R> : OpenResultRecipient<R> {
    @Composable
    override fun onNavResult(listener: (NavResult<R>) -> Unit) = Unit
}