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

package com.niyaj.expenses.settings

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SETTINGS_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPORT_EXPENSE_SUB_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPORT_EXPENSE_TITLE
import com.niyaj.common.tags.ExpenseTestTags.IMPORT_EXPENSE_SUB_TITLE
import com.niyaj.common.tags.ExpenseTestTags.IMPORT_EXPENSE_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.expenses.destinations.ExpensesExportScreenDestination
import com.niyaj.expenses.destinations.ExpensesImportScreenDestination
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
fun ExpensesSettingsScreen(
    navigator: DestinationsNavigator,
) {
    ExpensesSettingsScreenContent(
        modifier = Modifier,
        onBackClick = navigator::navigateUp,
        onImportClick = {
            navigator.navigate(ExpensesImportScreenDestination())
        },
        onExportClick = {
            navigator.navigate(ExpensesExportScreenDestination())
        },
    )
}

@VisibleForTesting
@Composable
internal fun ExpensesSettingsScreenContent(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = EXPENSE_SETTINGS_TITLE)

    TrackScrollJank(scrollableState = lazyListState, stateName = "Expenses Settings::List")

    StandardBottomSheet(
        modifier = modifier,
        title = EXPENSE_SETTINGS_TITLE,
        onBackClick = onBackClick,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item(IMPORT_EXPENSE_TITLE) {
                SettingsCard(
                    title = IMPORT_EXPENSE_TITLE,
                    subtitle = IMPORT_EXPENSE_SUB_TITLE,
                    icon = PoposIcons.Import,
                    onClick = onImportClick,
                )
            }

            item(EXPORT_EXPENSE_TITLE) {
                SettingsCard(
                    title = EXPORT_EXPENSE_TITLE,
                    subtitle = EXPORT_EXPENSE_SUB_TITLE,
                    icon = PoposIcons.Upload,
                    onClick = onExportClick,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ExpensesSettingsScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ExpensesSettingsScreenContent(
            modifier = modifier,
            onBackClick = {},
            onImportClick = {},
            onExportClick = {},
        )
    }
}
