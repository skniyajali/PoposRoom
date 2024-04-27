package com.niyaj.address.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.address.destinations.AddressExportScreenDestination
import com.niyaj.address.destinations.AddressImportScreenDestination
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SETTINGS_TITLE
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddressSettingsScreen(
    navController: DestinationsNavigator,
) {
    TrackScreenViewEvent(screenName = "AddressSettingsScreen")

    val lazyListState = rememberLazyListState()

    StandardBottomSheet(
        title = ADDRESS_SETTINGS_TITLE,
        onBackClick = navController::navigateUp,
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Address Settings")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item("ImportAddress") {
                SettingsCard(
                    title = "Import Address",
                    subtitle = "Click here to import data from file.",
                    icon = PoposIcons.Import,
                    onClick = {
                        navController.navigate(AddressImportScreenDestination())
                    },
                )
            }

            item("ExportAddress") {
                SettingsCard(
                    title = "Export Address",
                    subtitle = "Click here to export address to file.",
                    icon = PoposIcons.Upload,
                    onClick = {
                        navController.navigate(AddressExportScreenDestination())
                    },
                )
            }
        }
    }
}