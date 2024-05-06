/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.market.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SETTINGS_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.Pewter
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.market.destinations.ExportMarketItemScreenDestination
import com.niyaj.market.destinations.ExportMeasureUnitScreenDestination
import com.niyaj.market.destinations.ImportMarketItemScreenDestination
import com.niyaj.market.destinations.ImportMeasureUnitScreenDestination
import com.niyaj.market.destinations.MeasureUnitScreenDestination
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination
@Composable
fun MarketItemSettingsScreen(
    navigator: DestinationsNavigator,
    exportRecipient: ResultRecipient<ExportMarketItemScreenDestination, String>,
    importRecipient: ResultRecipient<ImportMarketItemScreenDestination, String>,
    exportMeasureRecipient: ResultRecipient<ExportMeasureUnitScreenDestination, String>,
    importMeasureRecipient: ResultRecipient<ImportMeasureUnitScreenDestination, String>,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    exportRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    exportMeasureRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    importMeasureRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = MARKET_ITEM_SETTINGS_TITLE)

    StandardScaffoldRouteNew(
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
                },
            )
        },
        onBackClick = navigator::navigateUp,
    ) {
        TrackScrollJank(
            scrollableState = lazyListState,
            stateName = "MarketItemSettingsScreen::Columns",
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(SpaceSmall),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item("Measure Unit") {
                SettingsCard(
                    title = "Measure Units",
                    subtitle = "Click here manage measure units.",
                    icon = PoposIcons.MonitorWeight,
                    onClick = {
                        navigator.navigate(MeasureUnitScreenDestination())
                    },
                    containerColor = Pewter
                )
            }

            item("Import Market Item") {
                SettingsCard(
                    title = "Import Market Item",
                    subtitle = "Click here to import data from file.",
                    icon = PoposIcons.Import,
                    onClick = {
                        navigator.navigate(ImportMarketItemScreenDestination())
                    },
                    containerColor = Pewter
                )
            }

            item("Export Market Item") {
                SettingsCard(
                    title = "Export Market Item",
                    subtitle = "Click here to export data to file.",
                    icon = PoposIcons.Upload,
                    onClick = {
                        navigator.navigate(ExportMarketItemScreenDestination())
                    },
                    containerColor = Pewter
                )
            }

            item("Import Measure Unit") {
                SettingsCard(
                    title = "Import Measure Unit",
                    subtitle = "Click here to import data from file.",
                    icon = PoposIcons.Import,
                    onClick = {
                        navigator.navigate(ImportMeasureUnitScreenDestination())
                    },
                    containerColor = Pewter
                )
            }

            item("Export Measure Unit") {
                SettingsCard(
                    title = "Export Measure Unit",
                    subtitle = "Click here to export data to file.",
                    icon = PoposIcons.Upload,
                    onClick = {
                        navigator.navigate(ExportMeasureUnitScreenDestination())
                    },
                    containerColor = Pewter
                )
            }
        }
    }
}