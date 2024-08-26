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

package com.niyaj.addonitem.settings

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.niyaj.addonitem.destinations.AddOnExportScreenDestination
import com.niyaj.addonitem.destinations.AddOnImportScreenDestination
import com.niyaj.common.tags.AddOnTestTags.ADDON_ITEM_LIST
import com.niyaj.common.tags.AddOnTestTags.ADDON_SETTINGS_TITLE
import com.niyaj.common.tags.AddOnTestTags.EXPORT_ADDON_TITLE
import com.niyaj.common.tags.AddOnTestTags.IMPORT_ADDON_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
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
fun AddOnSettingsScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
) {
    TrackScreenViewEvent(screenName = "AddOnSettingsScreen")

    AddOnSettingsScreenContent(
        modifier = modifier,
        onBackClick = navigator::navigateUp,
        onImportClick = {
            navigator.navigate(AddOnImportScreenDestination())
        },
        onExportClick = {
            navigator.navigate(AddOnExportScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
@DevicePreviews
internal fun AddOnSettingsScreenContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onImportClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    StandardBottomSheet(
        modifier = modifier,
        title = ADDON_SETTINGS_TITLE,
        onBackClick = onBackClick,
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "addon:settings")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(ADDON_ITEM_LIST),
            contentPadding = PaddingValues(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item(IMPORT_ADDON_TITLE) {
                SettingsCard(
                    title = IMPORT_ADDON_TITLE,
                    subtitle = "Click here to import addon from file.",
                    icon = PoposIcons.Import,
                    onClick = onImportClick,
                )
            }

            item(EXPORT_ADDON_TITLE) {
                SettingsCard(
                    title = EXPORT_ADDON_TITLE,
                    subtitle = "Click here to export addon to file.",
                    icon = PoposIcons.Upload,
                    onClick = onExportClick,
                )
            }
        }
    }
}
