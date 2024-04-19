package com.niyaj.ui.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun NavController.currentRoute(defaultScreen: String = Screens.HOME_SCREEN) =
    this.currentBackStackEntryAsState().value?.destination?.route
        ?: defaultScreen