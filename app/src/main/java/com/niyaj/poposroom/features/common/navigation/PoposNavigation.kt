package com.niyaj.poposroom.features.common.navigation

import androidx.compose.material.ScaffoldState
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.niyaj.poposroom.features.addon_item.presentation.AddOnItemScreen
import com.niyaj.poposroom.features.addon_item.presentation.NavGraphs
import com.niyaj.poposroom.features.addon_item.presentation.destinations.AddOnItemScreenDestination
import com.niyaj.poposroom.features.common.utils.SheetScreen
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.spec.Route

/**
 *  Navigation controller
 *  @author Sk Niyaj Ali
 *  @param modifier
 *  @param scaffoldState
 *  @param navController
 *  @param startRoute
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoposNavigation(
    modifier: Modifier = Modifier,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    scaffoldState: ScaffoldState,
    snackbarState: SnackbarHostState,
    navController: NavHostController,
    startRoute: Route,
    closeSheet: () -> Unit,
    onOpenSheet: (SheetScreen) -> Unit,
) {

    val navHostEngine = rememberNavHostEngine()

    DestinationsNavHost(
        modifier = modifier,
        navGraph = NavGraphs.root,
        navController = navController,
        startRoute = startRoute,
        engine = navHostEngine,
        dependenciesContainerBuilder = {
            dependency(scaffoldState)
            dependency(bottomSheetScaffoldState)
            dependency(snackbarState)
        },
    ) {
        composable(AddOnItemScreenDestination) {
            AddOnItemScreen(
                bottomSheetScaffoldState = bottomSheetScaffoldState,
                navController = navController,
                onCloseSheet = closeSheet,
                onOpenSheet = onOpenSheet,
            )
        }
    }
}