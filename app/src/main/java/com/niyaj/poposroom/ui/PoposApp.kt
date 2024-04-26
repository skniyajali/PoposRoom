package com.niyaj.poposroom.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun PoposApp(
    modifier: Modifier = Modifier,
    viewModel: MainActivityViewModel,
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    appState: PoposAppState = rememberPoposAppState(
        networkMonitor = networkMonitor,
        windowSizeClass = windowSizeClass,
    ),
) {
    PoposBackground(modifier) {
        PoposGradientBackground(
            gradientColors = LocalGradientColors.current,
        ) {
//            val scope = rememberCoroutineScope()
//            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val snackbarHostState = remember { SnackbarHostState() }
//            val layoutDirection = LocalLayoutDirection.current

            val isOffline by appState.isOffline.collectAsStateWithLifecycle()
            val reportState = viewModel.reportState.collectAsStateWithLifecycle().value
            val deleteState = viewModel.deleteState.collectAsStateWithLifecycle().value

            LaunchedEffect(key1 = deleteState, key2 = reportState) {
                if (deleteState) {
                    snackbarHostState.showSnackbar("Data Deletion Running")
                }

                if (reportState) {
                    snackbarHostState.showSnackbar("Generating Reports")
                }
            }

            // If user is not connected to the internet show a snack bar to inform them.
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(message = "You are not connected to the internet")
                }
            }

            /*
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    StandardDrawer(
                        currentRoute = appState.currentRouteOrDefault,
                        onNavigateToScreen = {
                            appState.navigateToScreen(it)

                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                },
                gesturesEnabled = true
            ) {
                Scaffold(
                    modifier = Modifier.semantics {
                        testTagsAsResourceId = true
                    },
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    bottomBar = {
                        if (appState.shouldShowBottomBar) {
                            StandardBottomNavigation(
                                destinations = appState.topLevelDestinations,
                                onNavigateToDestination = appState::navigateToTopLevelDestination,
                                currentDestination = appState.currentDestination,
                                modifier = Modifier.testTag("PoposBottomBar"),
                            )
                        }
                    },
                ) {
                    PoposNavHost(
                        modifier = Modifier
                            .fillMaxSize(),
                        appState = appState,
                        startRoute = RootNavGraph.startRoute,
                    )
                }
            }
            */

            PoposNavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                appState = appState,
                startRoute = RootNavGraph.startRoute,
            )
        }
    }
}