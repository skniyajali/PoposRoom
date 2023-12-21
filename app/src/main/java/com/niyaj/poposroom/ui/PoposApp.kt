package com.niyaj.poposroom.ui

import android.annotation.SuppressLint
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.niyaj.common.utils.showToast
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
            val context = LocalContext.current

            val isOffline by appState.isOffline.collectAsStateWithLifecycle()
            val reportState = viewModel.reportState.collectAsStateWithLifecycle().value
            val deleteState = viewModel.deleteState.collectAsStateWithLifecycle().value

            LaunchedEffect(key1 = deleteState, key2 = reportState) {
                if (deleteState) {
                    context.showToast("Data Deletion Running")
                }

                if (reportState) {
                    context.showToast("Generating Reports")
                }
            }

            // If user is not connected to the internet show a snack bar to inform them.
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    context.showToast(message = "You are not connected to the internet")
                } else {
                    onCheckForAppUpdate()
                }
            }

            PoposNavHost(
                modifier = Modifier,
                appState = appState,
                startRoute = RootNavGraph.startRoute,
            )
        }
    }
}