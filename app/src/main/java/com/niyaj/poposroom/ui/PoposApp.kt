package com.niyaj.poposroom.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.niyaj.data.utils.NetworkMonitor
import com.niyaj.designsystem.components.PoposBackground
import com.niyaj.designsystem.components.PoposGradientBackground
import com.niyaj.designsystem.theme.LocalGradientColors
import com.niyaj.poposroom.MainActivityViewModel
import com.niyaj.poposroom.navigation.PoposNavHost
import com.niyaj.poposroom.navigation.RootNavGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalMaterialNavigationApi::class
)
@Composable
fun PoposApp(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel,
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    onCheckForAppUpdate: () -> Unit,
    appState: PoposAppState = rememberPoposAppState(
        networkMonitor = networkMonitor,
        windowSizeClass = windowSizeClass,
    ),
) {
    PoposBackground(modifier) {
        PoposGradientBackground(
            gradientColors = LocalGradientColors.current,
        ) {
            val snackbarHostState = remember { SnackbarHostState() }

            val isOffline by appState.isOffline.collectAsStateWithLifecycle()
            val reportState = viewModel.reportState.collectAsStateWithLifecycle().value
            val deleteState = viewModel.deleteState.collectAsStateWithLifecycle().value

            LaunchedEffect(key1 = deleteState) {
                if (deleteState) {
                    snackbarHostState.showSnackbar(
                        "Data Deletion Running",
                        duration = SnackbarDuration.Long,
                    )
                }
            }

            LaunchedEffect(key1 = reportState) {
                if (reportState) {
                    snackbarHostState.showSnackbar(
                        "Generating Reports",
                        duration = SnackbarDuration.Long,
                    )
                }
            }

            // If user is not connected to the internet show a snack bar to inform them.
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = "You are not connected to the internet",
                        duration = SnackbarDuration.Short,
                    )
                } else {
                    onCheckForAppUpdate()
                }
            }

            Scaffold(
                modifier = Modifier
                    .semantics {
                        testTagsAsResourceId = true
                    }
                    .fillMaxSize(),
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = Color.White,
                contentColor = Color.Transparent
            ) {
                PoposNavHost(
                    modifier = Modifier
                        .consumeWindowInsets(it),
                    appState = appState,
                    startRoute = RootNavGraph.startRoute,
                )
            }
        }
    }
}