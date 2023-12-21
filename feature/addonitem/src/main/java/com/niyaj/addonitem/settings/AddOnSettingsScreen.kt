package com.niyaj.addonitem.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.niyaj.addonitem.destinations.AddOnExportScreenDestination
import com.niyaj.addonitem.destinations.AddOnImportScreenDestination
import com.niyaj.common.tags.AddOnTestTags.ADDON_SETTINGS_TITLE
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
fun AddOnSettingsScreen(
    navController: NavController,
) {
    TrackScreenViewEvent(screenName = "AddOnSettingsScreen")

    val lazyListState = rememberLazyListState()

    StandardBottomSheet(
        title = ADDON_SETTINGS_TITLE,
        onBackClick = {
            navController.navigateUp()
        }
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "addon:settings")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ){
            item("ImportAddOn") {
                SettingsCard(
                    title = "Import AddOn",
                    subtitle = "Click here to import addon from file.",
                    icon = Icons.Default.SaveAlt,
                    containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    onClick = {
                        navController.navigate(AddOnImportScreenDestination())
                    }
                )
            }

            item("ExportAddOn") {
                SettingsCard(
                    title = "Export AddOn",
                    subtitle = "Click here to export addon to file.",
                    icon = Icons.Default.Upload,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = {
                        navController.navigate(AddOnExportScreenDestination())
                    }
                )
            }
        }
    }
}