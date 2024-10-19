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

import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.Navigator.Extras
import com.ramcosta.composedestinations.navigation.DestinationsNavOptionsBuilder
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.spec.Route
import org.junit.Assert.assertEquals

class TestDestinationsNavigator : DestinationsNavigator {

    /**
     * The current route (or `null` if no initial graph has been set and no navigation has been
     * performed).
     *
     * Note that this represents what the route **would be** if actual navigation were allowed to
     * be performed.
     */
    private var currentRoute: String? = null

    /**
     * Represents the parameters of the last known navigation attempt via a call to [navigate].
     */
    private var lastNavigation: Navigation? = null

    override fun navigate(direction: Direction, builder: DestinationsNavOptionsBuilder.() -> Unit) {
        navigate(
            direction = direction,
            navOptions = null,
            navigatorExtras = null,
        )
    }

    override fun clearBackStack(route: Route): Boolean {
        TODO("Not yet implemented")
    }

    override fun navigate(direction: Direction, navOptions: NavOptions?, navigatorExtras: Extras?) {
        lastNavigation = Navigation(
            direction = direction,
            navOptions = navOptions,
            navigatorExtras = navigatorExtras,
        )
        currentRoute = direction.route
    }

    override fun navigateUp(): Boolean {
        TODO("Not yet implemented")
    }

    override fun popBackStack(): Boolean {
        TODO("Not yet implemented")
    }

    override fun popBackStack(route: Route, inclusive: Boolean, saveState: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Asserts the [currentRoute] matches the given [route].
     */
    fun assertCurrentRoute(route: String) {
        assertEquals(currentRoute, route)
    }

    /**
     * Asserts multiple aspects of the last navigation to have occurred.
     */
    fun assertLastNavigation(
        route: String,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null,
    ) {
        assertEquals(currentRoute, route)
        assertEquals(lastNavigation?.navOptions, navOptions)
        assertEquals(lastNavigation?.navigatorExtras, navigatorExtras)
    }

    /**
     * Asserts the [lastNavigation] includes the given [navOptions].
     */
    fun assertLastNavOptions(navOptions: NavOptions?) {
        assertEquals(lastNavigation?.navOptions, navOptions)
    }

    data class Navigation(
        val direction: Direction,
        val navOptions: NavOptions?,
        val navigatorExtras: Navigator.Extras?,
    )
}
