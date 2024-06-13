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

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.designsystem.components.PoposBackground
import com.niyaj.poposroom.navigation.PoposNavHost
import com.ramcosta.composedestinations.spec.Route

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PoposApp(
    modifier: Modifier = Modifier,
    appState: PoposAppState,
    startRoute: Route,
) {
    PoposBackground(modifier) {
        val snackbarHostState = remember { SnackbarHostState() }
        // TODO:: Since we're not dealing with network
//        val isOffline by appState.isOffline.collectAsStateWithLifecycle()
        val reportState = appState.reportState.collectAsStateWithLifecycle().value
        val deleteState = appState.deleteState.collectAsStateWithLifecycle().value

        LaunchedEffect(key1 = deleteState, key2 = reportState) {
            if (deleteState) {
                snackbarHostState.showSnackbar("Data Deletion Running")
            }

            if (reportState) {
                snackbarHostState.showSnackbar("Generating Reports")
            }
        }

        // TODO:: Since we're not dealing with network
        // If user is not connected to the internet show a snack bar to inform them.
//        LaunchedEffect(isOffline) {
//            if (isOffline) {
//                snackbarHostState.showSnackbar(message = "You are not connected to the internet")
//            }
//        }

        Scaffold(
            modifier = Modifier.semantics {
                testTagsAsResourceId = true
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    ),
            ) {
                PoposNavHost(
                    modifier = Modifier
                        .fillMaxSize(),
                    appState = appState,
                    startRoute = startRoute,
                )
            }
        }
    }
}
