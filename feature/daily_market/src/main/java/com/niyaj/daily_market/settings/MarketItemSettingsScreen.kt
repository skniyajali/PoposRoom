package com.niyaj.daily_market.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SETTINGS_TITLE
import com.niyaj.daily_market.destinations.ExportMarketItemScreenDestination
import com.niyaj.daily_market.destinations.ExportMeasureUnitScreenDestination
import com.niyaj.daily_market.destinations.ImportMarketItemScreenDestination
import com.niyaj.daily_market.destinations.ImportMeasureUnitScreenDestination
import com.niyaj.daily_market.destinations.MeasureUnitScreenDestination
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination
@Composable
fun MarketItemSettingsScreen(
    navController: NavController,
    exportRecipient: ResultRecipient<ExportMarketItemScreenDestination, String>,
    importRecipient: ResultRecipient<ImportMarketItemScreenDestination, String>,
    exportMeasureRecipient: ResultRecipient<ExportMeasureUnitScreenDestination, String>,
    importMeasureRecipient: ResultRecipient<ImportMeasureUnitScreenDestination, String>,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    exportRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    exportMeasureRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    importMeasureRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffoldNew(
        navController = navController,
        title = MARKET_ITEM_SETTINGS_TITLE,
        snackbarHostState = snackbarState,
        showBackButton = true,
        showBottomBar = false,
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ){
            item("Measure Unit") {
                SettingsCard(
                    title = "Measure Units",
                    subtitle = "Click here manage measure units.",
                    icon = Icons.Default.MonitorWeight,
                    onClick = {
                        navController.navigate(MeasureUnitScreenDestination())
                    }
                )
            }

            item("Import Market Item") {
                SettingsCard(
                    title = "Import Market Item",
                    subtitle = "Click here to import data from file.",
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ImportMarketItemScreenDestination())
                    }
                )
            }

            item("Export Market Item") {
                SettingsCard(
                    title = "Export Market Item",
                    subtitle = "Click here to export data to file.",
                    icon = Icons.Default.Upload,
                    onClick = {
                        navController.navigate(ExportMarketItemScreenDestination())
                    }
                )
            }

            item("Import Measure Unit") {
                SettingsCard(
                    title = "Import Measure Unit",
                    subtitle = "Click here to import data from file.",
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ImportMeasureUnitScreenDestination())
                    }
                )
            }

            item("Export Measure Unit") {
                SettingsCard(
                    title = "Export Measure Unit",
                    subtitle = "Click here to export data to file.",
                    icon = Icons.Default.Upload,
                    onClick = {
                        navController.navigate(ExportMeasureUnitScreenDestination())
                    }
                )
            }
        }
    }
}