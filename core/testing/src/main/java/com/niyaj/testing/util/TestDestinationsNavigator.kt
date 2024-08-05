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

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * Creates a [DestinationsNavigator] good for testing.
 *
 * @param testNavController should be `TestNavHostController` from navigation testing artifact
 * @param isCurrentBackStackEntryResumed allows you to control behavior of `onlyIfResumed` parameter
 * on navigate calls. By default, uses the real response from [testNavController]'s current nav back stack entry.
 */
@Suppress("FunctionName")
fun TestDestinationsNavigator(
    testNavController: NavController,
    isCurrentBackStackEntryResumed: () -> Boolean = {
        testNavController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED
    },
): DestinationsNavigator = DestinationsNavController(
    navController = testNavController,
    isCurrentBackStackEntryResumed = isCurrentBackStackEntryResumed,
)
