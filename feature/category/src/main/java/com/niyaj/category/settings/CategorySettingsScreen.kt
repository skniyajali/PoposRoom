package com.niyaj.category.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.niyaj.category.destinations.ExportCategoryScreenDestination
import com.niyaj.category.destinations.ImportCategoryScreenDestination
import com.niyaj.common.tags.CategoryConstants.CATEGORY_SETTINGS_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun CategorySettingsScreen(
    navController: NavController,
) {
    TrackScreenViewEvent(screenName = "Category Setting Screen")

    val lazyListState = rememberLazyListState()

    TrackScrollJank(scrollableState = lazyListState, stateName = "Category Setting::Options")

    StandardBottomSheet(
        title = CATEGORY_SETTINGS_TITLE,
        onBackClick = { navController.navigateUp() },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item("ImportCategory") {
                SettingsCard(
                    title = "Import Category",
                    subtitle = "Click here to import data from file.",
                    icon = PoposIcons.Import,
                    onClick = {
                        navController.navigate(ImportCategoryScreenDestination())
                    },
                )
            }

            item("ExportCategory") {
                SettingsCard(
                    title = "Export Category",
                    subtitle = "Click here to export category to file.",
                    icon = PoposIcons.Upload,
                    onClick = {
                        navController.navigate(ExportCategoryScreenDestination())
                    },
                )
            }
        }
    }
}