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

@file:Suppress("InvalidPackageDeclaration")

package com.niyaj.poposroom.core.testing.util

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import com.niyaj.designsystem.theme.PoposRoomTheme
import org.junit.Rule

/**
 * A base class that can be used for performing Compose-layer testing using Robolectric, Compose
 * Testing, and JUnit 4.
 */
abstract class BaseComposeTest : BaseRobolectricTest() {
    @get:Rule
    open val composeTestRule = createComposeRule()

    /**
     * instance of [OnBackPressedDispatcher] made available if testing using
     *
     * [setContentWithBackDispatcher] or [runTestWithTheme]
     */
    var backDispatcher: OnBackPressedDispatcher? = null
        private set

    /**
     * Helper for testing a basic Composable function that only requires a Composable environment
     * with the [PoposRoomTheme].
     */
    protected fun runTestWithTheme(
        darkTheme: Boolean = false,
        androidTheme: Boolean = false,
        disableDynamicTheming: Boolean = true,
        test: @Composable () -> Unit,
    ) {
        composeTestRule.setContent {
            PoposRoomTheme(
                darkTheme = darkTheme,
                androidTheme = androidTheme,
                disableDynamicTheming = disableDynamicTheming,
            ) {
                backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                test()
            }
        }
    }

    /**
     * Helper for testing a basic Composable function that provides access to a
     * [OnBackPressedDispatcher].
     *
     * Use if the [Composable] function being tested uses a [BackHandler]
     */
    protected fun setContentWithBackDispatcher(test: @Composable () -> Unit) {
        composeTestRule.setContent {
            backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            test()
        }
    }
}
