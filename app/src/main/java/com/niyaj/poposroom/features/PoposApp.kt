package com.niyaj.poposroom.features

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.niyaj.poposroom.features.common.navigation.PoposNavigation
import com.niyaj.poposroom.features.common.ui.theme.PoposRoomTheme
import com.niyaj.poposroom.features.common.utils.SheetLayout
import com.niyaj.poposroom.features.common.utils.SheetScreen
import com.niyaj.poposroom.features.destinations.MainFeedScreenDestination
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoposApp(
    sizeClass: WindowSizeClass
) {
    // A surface container using the 'background' color from the theme
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val snackbarState = remember { SnackbarHostState() }
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(bottomSheetState, snackbarState)

    val currentBottomSheet = remember {
        mutableStateOf<SheetScreen?>(null)
    }

    val closeBottomSheet: () -> Unit = {
        scope.launch {
            bottomSheetScaffoldState.bottomSheetState.hide()
            currentBottomSheet.value = null
        }
    }

    val openBottomSheet: (SheetScreen) -> Unit = {
        scope.launch {
            currentBottomSheet.value = it
            bottomSheetScaffoldState.bottomSheetState.expand()
        }
    }

    PoposRoomTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomSheetScaffold(
                modifier = Modifier,
                scaffoldState = bottomSheetScaffoldState,
                sheetContent = {
                    currentBottomSheet.value?.let {
                        SheetLayout(
                            current = it,
                            onCloseBottomSheet = closeBottomSheet,
                        )
                    }
                },
                sheetSwipeEnabled = true,
                sheetPeekHeight = 0.dp,
            ) {
                PoposNavigation(
                    bottomSheetScaffoldState = bottomSheetScaffoldState,
                    scaffoldState = scaffoldState,
                    snackbarState = snackbarState,
                    navController = navController,
                    startRoute = MainFeedScreenDestination,
                    closeSheet = closeBottomSheet,
                    onOpenSheet = openBottomSheet
                )
            }
        }
    }
}



