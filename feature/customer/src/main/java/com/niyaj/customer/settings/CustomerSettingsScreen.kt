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

package com.niyaj.customer.settings

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SETTINGS_TITLE
import com.niyaj.common.tags.CustomerTestTags.EXPORT_CUSTOMER_BTN_TEXT
import com.niyaj.common.tags.CustomerTestTags.EXPORT_CUSTOMER_SUB_TITLE
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_BTN_TEXT
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_SUB_TITLE
import com.niyaj.customer.destinations.CustomerExportScreenDestination
import com.niyaj.customer.destinations.CustomerImportScreenDestination
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
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
fun CustomerSettingsScreen(
    navigator: DestinationsNavigator,
) {
    CustomerSettingsScreenContent(
        modifier = Modifier,
        onBackClick = navigator::navigateUp,
        onImportClick = {
            navigator.navigate(CustomerImportScreenDestination())
        },
        onExportClick = {
            navigator.navigate(CustomerExportScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
internal fun CustomerSettingsScreenContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = CUSTOMER_SETTINGS_TITLE)

    TrackScrollJank(scrollableState = lazyListState, stateName = "Customer::SettingList")

    StandardBottomSheet(
        modifier = modifier,
        title = CUSTOMER_SETTINGS_TITLE,
        onBackClick = onBackClick,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item(IMPORT_CUSTOMER_BTN_TEXT) {
                SettingsCard(
                    title = IMPORT_CUSTOMER_BTN_TEXT,
                    subtitle = IMPORT_CUSTOMER_SUB_TITLE,
                    icon = PoposIcons.Import,
                    onClick = onImportClick,
                )
            }

            item(EXPORT_CUSTOMER_BTN_TEXT) {
                SettingsCard(
                    title = EXPORT_CUSTOMER_BTN_TEXT,
                    subtitle = EXPORT_CUSTOMER_SUB_TITLE,
                    icon = PoposIcons.Upload,
                    onClick = onExportClick,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun CustomerSettingsScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CustomerSettingsScreenContent(
            modifier = modifier,
            onBackClick = {},
            onImportClick = {},
            onExportClick = {},
        )
    }
}
