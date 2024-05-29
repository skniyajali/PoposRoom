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

package com.niyaj.market.measureUnit.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_SETTINGS_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.market.destinations.ExportMeasureUnitScreenDestination
import com.niyaj.market.destinations.ImportMeasureUnitScreenDestination
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun MeasureUnitSettingsScreen(
    navController: DestinationsNavigator,
) {
    val lazyListState = rememberLazyListState()
    TrackScreenViewEvent(screenName = UNIT_SETTINGS_TITLE)
    TrackScrollJank(scrollableState = lazyListState, stateName = "MeasureUnitSettingsScreen::Columns")

    StandardBottomSheet(
        title = UNIT_SETTINGS_TITLE,
        onBackClick = navController::navigateUp,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item("Import Measure Unit") {
                SettingsCard(
                    title = "Import Measure Unit",
                    subtitle = "Click here to import data from file.",
                    icon = PoposIcons.Import,
                    onClick = {
                        navController.navigate(ImportMeasureUnitScreenDestination())
                    },
                )
            }

            item("ExportMarketItem") {
                SettingsCard(
                    title = "Export Measure Unit",
                    subtitle = "Click here to export data to file.",
                    icon = PoposIcons.Upload,
                    onClick = {
                        navController.navigate(ExportMeasureUnitScreenDestination())
                    },
                )
            }
        }
    }
}
