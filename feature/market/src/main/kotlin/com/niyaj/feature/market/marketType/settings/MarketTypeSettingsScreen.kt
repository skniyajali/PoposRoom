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

package com.niyaj.feature.market.marketType.settings

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.MarketTypeTags.MARKET_SETTINGS_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.feature.market.destinations.ExportMarketTypeScreenDestination
import com.niyaj.feature.market.destinations.ImportMarketTypeScreenDestination
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun MarketTypeSettingsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
) {
    MarketTypeSettingsScreenContent(
        onBackClick = navigator::navigateUp,
        onImportClick = {
            navigator.navigate(ImportMarketTypeScreenDestination())
        },
        onExportClick = {
            navigator.navigate(ExportMarketTypeScreenDestination())
        },
        modifier = modifier,
    )
}

@VisibleForTesting
@Composable
internal fun MarketTypeSettingsScreenContent(
    onBackClick: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = MARKET_SETTINGS_TITLE)

    TrackScrollJank(scrollableState = lazyListState, stateName = "MarketTypeSettingsScreen::Columns")

    StandardBottomSheet(
        modifier = modifier,
        title = MARKET_SETTINGS_TITLE,
        onBackClick = onBackClick,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item("ImportMarketType") {
                SettingsCard(
                    title = "Import Market Type",
                    subtitle = "Click here to import data from file.",
                    icon = PoposIcons.Import,
                    onClick = onImportClick,
                )
            }

            item("ExportMarketType") {
                SettingsCard(
                    title = "Export Market Type",
                    subtitle = "Click here to export data to file.",
                    icon = PoposIcons.Upload,
                    onClick = onExportClick,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun MarketTypeSettingsScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        MarketTypeSettingsScreenContent(
            modifier = modifier,
            onBackClick = {},
            onImportClick = {},
            onExportClick = {},
        )
    }
}
