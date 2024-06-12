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

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.MeasureUnitTestTags.EXPORT_UNIT_SUB_TITLE
import com.niyaj.common.tags.MeasureUnitTestTags.EXPORT_UNIT_TITLE
import com.niyaj.common.tags.MeasureUnitTestTags.IMPORT_UNIT_SUB_TITLE
import com.niyaj.common.tags.MeasureUnitTestTags.IMPORT_UNIT_TITLE
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_SETTINGS_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.market.destinations.ExportMeasureUnitScreenDestination
import com.niyaj.market.destinations.ImportMeasureUnitScreenDestination
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
fun MeasureUnitSettingsScreen(
    navigator: DestinationsNavigator,
) {
    MeasureUnitSettingsScreenContent(
        modifier = Modifier,
        onBackClick = navigator::navigateUp,
        onImportClick = {
            navigator.navigate(ImportMeasureUnitScreenDestination())
        },
        onExportClick = {
            navigator.navigate(ExportMeasureUnitScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
internal fun MeasureUnitSettingsScreenContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = UNIT_SETTINGS_TITLE)

    TrackScrollJank(scrollableState = lazyListState, stateName = "MeasureUnitSettingsScreen::List")

    StandardBottomSheet(
        modifier = modifier,
        title = UNIT_SETTINGS_TITLE,
        onBackClick = onBackClick,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item(IMPORT_UNIT_TITLE) {
                SettingsCard(
                    title = IMPORT_UNIT_TITLE,
                    subtitle = IMPORT_UNIT_SUB_TITLE,
                    icon = PoposIcons.Import,
                    onClick = onImportClick,
                )
            }

            item(EXPORT_UNIT_TITLE) {
                SettingsCard(
                    title = EXPORT_UNIT_TITLE,
                    subtitle = EXPORT_UNIT_SUB_TITLE,
                    icon = PoposIcons.Upload,
                    onClick = onExportClick,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun MeasureUnitSettingsScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        MeasureUnitSettingsScreenContent(
            modifier = modifier,
            onBackClick = {},
            onImportClick = {},
            onExportClick = {},
        )
    }
}