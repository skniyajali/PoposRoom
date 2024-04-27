package com.niyaj.charges.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.charges.destinations.ChargesExportScreenDestination
import com.niyaj.charges.destinations.ChargesImportScreenDestination
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SETTINGS_TITLE
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
fun ChargesSettingsScreen(
    navigator: DestinationsNavigator,
) {
    TrackScreenViewEvent(screenName = "Charges Setting Screen")

    val lazyListState = rememberLazyListState()

    TrackScrollJank(scrollableState = lazyListState, stateName = "Charges Setting::List")

    StandardBottomSheet(
        title = CHARGES_SETTINGS_TITLE,
        onBackClick = navigator::navigateUp,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item("ImportCharges") {
                SettingsCard(
                    title = "Import Charges",
                    subtitle = "Click here to import charges from file.",
                    icon = PoposIcons.Import,
                    onClick = {
                        navigator.navigate(ChargesImportScreenDestination())
                    },
                )
            }

            item("ExportCharges") {
                SettingsCard(
                    title = "Export Charges",
                    subtitle = "Click here to export charges to file.",
                    icon = PoposIcons.Upload,
                    onClick = {
                        navigator.navigate(ChargesExportScreenDestination())
                    },
                )
            }
        }
    }
}