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

import androidx.compose.material.navigation.BottomSheetNavigator
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberPoposTestAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
): PoposTestAppState {
    return remember(
        navController,
        bottomSheetNavigator,
        coroutineScope,
    ) {
        PoposTestAppState(
            navController,
            bottomSheetNavigator,
            coroutineScope,
        )
    }
}

@Stable
class PoposTestAppState(
    val navController: NavHostController,
    val bottomSheetNavigator: BottomSheetNavigator,
    coroutineScope: CoroutineScope,
) {
    val currentRoute: String? = navController.currentDestination?.route
}
