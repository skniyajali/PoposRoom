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

package com.niyaj.product.settings

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.CategoryConstants.EXPORT_CATEGORY_TITLE
import com.niyaj.common.tags.CategoryConstants.EXPORT_CATEGORY_TITLE_NOTE
import com.niyaj.common.tags.CategoryConstants.IMPORT_CATEGORY_NOTE
import com.niyaj.common.tags.CategoryConstants.IMPORT_CATEGORY_TITLE
import com.niyaj.common.tags.ProductTestTags.DECREASE_PRODUCTS_SUB_TITLE
import com.niyaj.common.tags.ProductTestTags.DECREASE_PRODUCTS_TITLE
import com.niyaj.common.tags.ProductTestTags.EXPORT_PRODUCTS_SUB_TITLE
import com.niyaj.common.tags.ProductTestTags.EXPORT_PRODUCTS_TITLE
import com.niyaj.common.tags.ProductTestTags.IMPORT_PRODUCTS_SUB_TITLE
import com.niyaj.common.tags.ProductTestTags.IMPORT_PRODUCTS_TITLE
import com.niyaj.common.tags.ProductTestTags.INCREASE_PRODUCTS_SUB_TITLE
import com.niyaj.common.tags.ProductTestTags.INCREASE_PRODUCTS_TITLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SETTINGS_TITLE
import com.niyaj.common.tags.ProductTestTags.VIEW_CATEGORY_SUB_TITLE
import com.niyaj.common.tags.ProductTestTags.VIEW_CATEGORY_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.product.destinations.DecreaseProductPriceScreenDestination
import com.niyaj.product.destinations.ExportProductScreenDestination
import com.niyaj.product.destinations.ImportProductScreenDestination
import com.niyaj.product.destinations.IncreaseProductPriceScreenDestination
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.CATEGORY_EXPORT_SCREEN
import com.niyaj.ui.utils.Screens.CATEGORY_IMPORT_SCREEN
import com.niyaj.ui.utils.Screens.CATEGORY_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ProductSettingScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
) {
    ProductSettingScreenContent(
        modifier = modifier,
        onBackClick = navigator::navigateUp,
        onImportClick = {
            navigator.navigate(ImportProductScreenDestination())
        },
        onExportClick = {
            navigator.navigate(ExportProductScreenDestination())
        },
        onClickViewCategory = {
            navigator.navigate(CATEGORY_SCREEN)
        },
        onCategoryImportClick = {
            navigator.navigate(CATEGORY_IMPORT_SCREEN)
        },
        onCategoryExportClick = {
            navigator.navigate(CATEGORY_EXPORT_SCREEN)
        },
        onIncreaseClick = {
            navigator.navigate(IncreaseProductPriceScreenDestination)
        },
        onDecreaseClick = {
            navigator.navigate(DecreaseProductPriceScreenDestination)
        },
    )
}

@VisibleForTesting
@Composable
internal fun ProductSettingScreenContent(
    onBackClick: () -> Unit,
    onClickViewCategory: () -> Unit,
    onCategoryImportClick: () -> Unit,
    onCategoryExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = PRODUCT_SETTINGS_TITLE)

    TrackScrollJank(scrollableState = lazyListState, stateName = "Product Settings::List")

    StandardBottomSheet(
        modifier = modifier,
        title = PRODUCT_SETTINGS_TITLE,
        onBackClick = onBackClick,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item(VIEW_CATEGORY_TITLE) {
                SettingsCard(
                    title = VIEW_CATEGORY_TITLE,
                    subtitle = VIEW_CATEGORY_SUB_TITLE,
                    icon = PoposIcons.OpenInNew,
                    onClick = onClickViewCategory,
                )
            }

            item(IMPORT_CATEGORY_TITLE) {
                SettingsCard(
                    title = IMPORT_CATEGORY_TITLE,
                    subtitle = IMPORT_CATEGORY_NOTE,
                    icon = PoposIcons.Import,
                    onClick = onCategoryImportClick,
                )
            }

            item(EXPORT_CATEGORY_TITLE) {
                SettingsCard(
                    title = EXPORT_CATEGORY_TITLE,
                    subtitle = EXPORT_CATEGORY_TITLE_NOTE,
                    icon = PoposIcons.Upload,
                    onClick = onCategoryExportClick,
                )
            }

            item(INCREASE_PRODUCTS_TITLE) {
                SettingsCard(
                    title = INCREASE_PRODUCTS_TITLE,
                    subtitle = INCREASE_PRODUCTS_SUB_TITLE,
                    icon = PoposIcons.Import,
                    onClick = onIncreaseClick,
                )
            }

            item(DECREASE_PRODUCTS_TITLE) {
                SettingsCard(
                    title = DECREASE_PRODUCTS_TITLE,
                    subtitle = DECREASE_PRODUCTS_SUB_TITLE,
                    icon = PoposIcons.Upload,
                    onClick = onDecreaseClick,
                )
            }

            item(IMPORT_PRODUCTS_TITLE) {
                SettingsCard(
                    title = IMPORT_PRODUCTS_TITLE,
                    subtitle = IMPORT_PRODUCTS_SUB_TITLE,
                    icon = PoposIcons.Import,
                    onClick = onImportClick,
                )
            }

            item(EXPORT_PRODUCTS_TITLE) {
                SettingsCard(
                    title = EXPORT_PRODUCTS_TITLE,
                    subtitle = EXPORT_PRODUCTS_SUB_TITLE,
                    icon = PoposIcons.Upload,
                    onClick = onExportClick,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ProductSettingScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ProductSettingScreenContent(
            modifier = modifier,
            onBackClick = {},
            onImportClick = {},
            onExportClick = {},
            onCategoryImportClick = {},
            onCategoryExportClick = {},
            onIncreaseClick = {},
            onDecreaseClick = {},
            onClickViewCategory = {},
        )
    }
}
