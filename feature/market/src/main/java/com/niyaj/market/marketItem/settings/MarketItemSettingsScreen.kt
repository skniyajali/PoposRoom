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

package com.niyaj.market.marketItem.settings

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SETTINGS_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.market.destinations.ExportMarketItemScreenDestination
import com.niyaj.market.destinations.ExportMarketTypeScreenDestination
import com.niyaj.market.destinations.ExportMeasureUnitScreenDestination
import com.niyaj.market.destinations.ImportMarketItemScreenDestination
import com.niyaj.market.destinations.ImportMarketTypeScreenDestination
import com.niyaj.market.destinations.ImportMeasureUnitScreenDestination
import com.niyaj.market.destinations.MarketTypeScreenDestination
import com.niyaj.market.destinations.MeasureUnitScreenDestination
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Destination
@Composable
fun MarketItemSettingsScreen(
    navigator: DestinationsNavigator,
    exportRecipient: ResultRecipient<ExportMarketItemScreenDestination, String>,
    importRecipient: ResultRecipient<ImportMarketItemScreenDestination, String>,
    exportMeasureRecipient: ResultRecipient<ExportMeasureUnitScreenDestination, String>,
    importMeasureRecipient: ResultRecipient<ImportMeasureUnitScreenDestination, String>,
    modifier: Modifier = Modifier,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    MarketItemSettingsScreenContent(
        modifier = modifier,
        onNavigateToMarketTypes = {
            navigator.navigate(MarketTypeScreenDestination)
        },
        onNavigateToMeasureUnit = {
            navigator.navigate(MeasureUnitScreenDestination)
        },
        onImportMarketType = {
            navigator.navigate(ImportMarketTypeScreenDestination)
        },
        onImportMeasureUnit = {
            navigator.navigate(ImportMeasureUnitScreenDestination)
        },
        onImportMarketItem = {
            navigator.navigate(ImportMarketItemScreenDestination)
        },
        onExportMarketItem = {
            navigator.navigate(ExportMarketItemScreenDestination)
        },
        onExportMarketType = {
            navigator.navigate(ExportMarketTypeScreenDestination)
        },
        onExportMeasureUnit = {
            navigator.navigate(ExportMeasureUnitScreenDestination)
        },
        onBackClick = navigator::navigateUp,
        snackbarState = snackbarState,
    )

    HandleResultRecipients(
        exportRecipient = exportRecipient,
        importRecipient = importRecipient,
        exportMeasureRecipient = exportMeasureRecipient,
        importMeasureRecipient = importMeasureRecipient,
        scope = scope,
        snackbarState = snackbarState,
    )
}

@VisibleForTesting
@Composable
internal fun MarketItemSettingsScreenContent(
    onNavigateToMarketTypes: () -> Unit,
    onNavigateToMeasureUnit: () -> Unit,
    onImportMarketType: () -> Unit,
    onImportMeasureUnit: () -> Unit,
    onImportMarketItem: () -> Unit,
    onExportMarketItem: () -> Unit,
    onExportMarketType: () -> Unit,
    onExportMeasureUnit: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    lazyListState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    containerColor: Color = MaterialTheme.colorScheme.background,
) {
    TrackScreenViewEvent(screenName = MARKET_ITEM_SETTINGS_TITLE)

    PoposSecondaryScaffold(
        title = MARKET_ITEM_SETTINGS_TITLE,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = false,
        fabPosition = FabPosition.End,
        snackbarHostState = snackbarState,
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
            item("Market Type") {
                SettingsCard(
                    title = "Market Types",
                    subtitle = "Click here manage market types.",
                    icon = PoposIcons.Category,
                    onClick = onNavigateToMarketTypes,
                    containerColor = containerColor,
                )
            }

            item("Measure Unit") {
                SettingsCard(
                    title = "Measure Units",
                    subtitle = "Click here manage measure units.",
                    icon = PoposIcons.MonitorWeight,
                    onClick = onNavigateToMeasureUnit,
                    containerColor = containerColor,
                )
            }

            item("Import Market Type") {
                SettingsCard(
                    title = "Import Market Type",
                    subtitle = "Click here to import data from file.",
                    icon = PoposIcons.Import,
                    onClick = onImportMarketType,
                    containerColor = containerColor,
                )
            }

            item("Export Market Type") {
                SettingsCard(
                    title = "Export Market Type",
                    subtitle = "Click here to export data to file.",
                    icon = PoposIcons.Upload,
                    onClick = onExportMarketType,
                    containerColor = containerColor,
                )
            }

            item("Import Measure Unit") {
                SettingsCard(
                    title = "Import Measure Unit",
                    subtitle = "Click here to import data from file.",
                    icon = PoposIcons.Import,
                    onClick = onImportMeasureUnit,
                    containerColor = containerColor,
                )
            }

            item("Export Measure Unit") {
                SettingsCard(
                    title = "Export Measure Unit",
                    subtitle = "Click here to export data to file.",
                    icon = PoposIcons.Upload,
                    onClick = onExportMeasureUnit,
                    containerColor = containerColor,
                )
            }

            item("Import Market Item") {
                SettingsCard(
                    title = "Import Market Item",
                    subtitle = "Click here to import data from file.",
                    icon = PoposIcons.Import,
                    onClick = onImportMarketItem,
                    containerColor = containerColor,
                )
            }

            item("Export Market Item") {
                SettingsCard(
                    title = "Export Market Item",
                    subtitle = "Click here to export data to file.",
                    icon = PoposIcons.Upload,
                    onClick = onExportMarketItem,
                    containerColor = containerColor,
                )
            }
        }
    }
}

@Composable
private fun HandleResultRecipients(
    exportRecipient: ResultRecipient<ExportMarketItemScreenDestination, String>,
    importRecipient: ResultRecipient<ImportMarketItemScreenDestination, String>,
    exportMeasureRecipient: ResultRecipient<ExportMeasureUnitScreenDestination, String>,
    importMeasureRecipient: ResultRecipient<ImportMeasureUnitScreenDestination, String>,
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
) {
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
}

@DevicePreviews
@Composable
private fun MarketItemSettingsScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        MarketItemSettingsScreenContent(
            modifier = modifier,
            onNavigateToMarketTypes = {},
            onNavigateToMeasureUnit = {},
            onImportMarketType = {},
            onImportMeasureUnit = {},
            onImportMarketItem = {},
            onExportMarketItem = {},
            onExportMarketType = {},
            onExportMeasureUnit = {},
            onBackClick = {},
        )
    }
}
