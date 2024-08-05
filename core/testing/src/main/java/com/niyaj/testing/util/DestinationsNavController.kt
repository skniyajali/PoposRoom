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

import androidx.annotation.MainThread
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * Implementation of [DestinationsNavigator] that uses
 * a [NavController] to navigate.
 */
internal class DestinationsNavController(
    private val navController: NavController,
    private val isCurrentBackStackEntryResumed: () -> Boolean,
) : DestinationsNavigator {

    override fun navigate(
        route: String,
        onlyIfResumed: Boolean,
        builder: NavOptionsBuilder.() -> Unit,
    ) {
        if (onlyIfResumed && !isCurrentBackStackEntryResumed()) {
            return
        }

        navController.navigate(route, builder)
    }

    override fun navigate(
        route: String,
        onlyIfResumed: Boolean,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?,
    ) {
        if (onlyIfResumed && !isCurrentBackStackEntryResumed()) {
            return
        }

        navController.navigate(route, navOptions, navigatorExtras)
    }

    @MainThread
    override fun navigateUp(): Boolean {
        return navController.navigateUp()
    }

    @MainThread
    override fun popBackStack(): Boolean {
        return navController.popBackStack()
    }

    @MainThread
    override fun popBackStack(
        route: String,
        inclusive: Boolean,
        saveState: Boolean,
    ): Boolean {
        return navController.popBackStack(route, inclusive, saveState)
    }

    @MainThread
    override fun clearBackStack(route: String): Boolean {
        return navController.clearBackStack(route)
    }
}
