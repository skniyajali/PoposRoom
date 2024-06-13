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

package com.niyaj.employeePayment.settings

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.PaymentScreenTags.EXPORT_PAYMENT_SUB_TITLE
import com.niyaj.common.tags.PaymentScreenTags.EXPORT_PAYMENT_TITLE
import com.niyaj.common.tags.PaymentScreenTags.IMPORT_PAYMENT_SUB_TITLE
import com.niyaj.common.tags.PaymentScreenTags.IMPORT_PAYMENT_TITLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SETTINGS_NOTE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SETTINGS_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.employeePayment.destinations.PaymentExportScreenDestination
import com.niyaj.employeePayment.destinations.PaymentImportScreenDestination
import com.niyaj.ui.components.NoteCard
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
fun PaymentSettingsScreen(
    navigator: DestinationsNavigator,
) {
    PaymentSettingsScreenContent(
        modifier = Modifier,
        onBackClick = navigator::navigateUp,
        onImportClick = {
            navigator.navigate(PaymentImportScreenDestination)
        },
        onExportClick = {
            navigator.navigate(PaymentExportScreenDestination)
        },
    )
}

@VisibleForTesting
@Composable
internal fun PaymentSettingsScreenContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = "Payment Setting Screen")

    TrackScrollJank(scrollableState = lazyListState, stateName = "Payment Settings::List")

    StandardBottomSheet(
        modifier = modifier,
        title = PAYMENT_SETTINGS_TITLE,
        onBackClick = onBackClick,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item("Note") {
                NoteCard(text = PAYMENT_SETTINGS_NOTE)
            }

            item(IMPORT_PAYMENT_TITLE) {
                SettingsCard(
                    title = IMPORT_PAYMENT_TITLE,
                    subtitle = IMPORT_PAYMENT_SUB_TITLE,
                    icon = PoposIcons.Import,
                    onClick = onImportClick,
                )
            }

            item(EXPORT_PAYMENT_TITLE) {
                SettingsCard(
                    title = EXPORT_PAYMENT_TITLE,
                    subtitle = EXPORT_PAYMENT_SUB_TITLE,
                    icon = PoposIcons.Upload,
                    onClick = onExportClick,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun PaymentSettingsScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        PaymentSettingsScreenContent(
            modifier = modifier,
            onBackClick = {},
            onImportClick = {},
            onExportClick = {},
        )
    }
}
