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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.designsystem.components.PoposCenterAlignedTopAppBar

@Stable
@Composable
@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreenScaffold(
    currentRoute: String,
    onNavigateToScreen: (String) -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    navActions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    fabPosition: FabPosition = FabPosition.End,
    drawerState: DrawerState = rememberDrawerState(initialValue = Closed),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    content: @Composable (padding: PaddingValues) -> Unit = {},
) {
    val systemUiController = rememberSystemUiController()

    val navColor = MaterialTheme.colorScheme.surface

    SideEffect {
        systemUiController.setStatusBarColor(color = navColor, darkIcons = true)

        systemUiController.setNavigationBarColor(color = navColor)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            PoposDrawer(
                modifier = Modifier.testTag("homeAppDrawer"),
                drawerState = drawerState,
                currentRoute = currentRoute,
                onNavigateToScreen = onNavigateToScreen,
            )
        },
        gesturesEnabled = true,
    ) {
        Scaffold(
            topBar = {
                PoposCenterAlignedTopAppBar(
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = navActions,
                )
            },
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = fabPosition,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier
                .fillMaxSize(),
        ) { padding ->
            content(padding)
        }
    }
}
