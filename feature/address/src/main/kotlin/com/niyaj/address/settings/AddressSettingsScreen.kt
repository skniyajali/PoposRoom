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

package com.niyaj.address.settings

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.address.destinations.AddressExportScreenDestination
import com.niyaj.address.destinations.AddressImportScreenDestination
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SETTINGS_TITLE
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_SUB_TITLE
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_TITLE
import com.niyaj.common.tags.AddressTestTags.IMPORT_ADDRESS_SUB_TITLE
import com.niyaj.common.tags.AddressTestTags.IMPORT_ADDRESS_TITLE
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
fun AddressSettingsScreen(
    navigator: DestinationsNavigator,
) {
    TrackScreenViewEvent(screenName = "AddressSettingsScreen")

    AddressSettingsScreenContent(
        modifier = Modifier,
        onBackClick = navigator::navigateUp,
        onImportClick = {
            navigator.navigate(AddressImportScreenDestination())
        },
        onExportClick = {
            navigator.navigate(AddressExportScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
internal fun AddressSettingsScreenContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    StandardBottomSheet(
        modifier = modifier,
        title = ADDRESS_SETTINGS_TITLE,
        onBackClick = onBackClick,
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Address Settings")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item(IMPORT_ADDRESS_TITLE) {
                SettingsCard(
                    title = IMPORT_ADDRESS_TITLE,
                    subtitle = IMPORT_ADDRESS_SUB_TITLE,
                    icon = PoposIcons.Import,
                    onClick = onImportClick,
                )
            }

            item(EXPORT_ADDRESS_TITLE) {
                SettingsCard(
                    title = EXPORT_ADDRESS_TITLE,
                    subtitle = EXPORT_ADDRESS_SUB_TITLE,
                    icon = PoposIcons.Upload,
                    onClick = onExportClick,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddressSettingsScreenContentPreview() {
    AddressSettingsScreenContent(
        modifier = Modifier,
        onBackClick = {},
        onImportClick = {},
        onExportClick = {},
    )
}
