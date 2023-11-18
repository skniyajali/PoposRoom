package com.niyaj.charges.settings

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
import com.niyaj.charges.destinations.ChargesExportScreenDestination
import com.niyaj.charges.destinations.ChargesImportScreenDestination
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SETTINGS_TITLE
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ChargesSettingsScreen(
    navController: NavController
) {
    val lazyListState = rememberLazyListState()

    StandardBottomSheet(
        title = CHARGES_SETTINGS_TITLE,
        onBackClick = { navController.navigateUp() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ){
            item("ImportCharges") {
                SettingsCard(
                    title = "Import Charges",
                    subtitle = "Click here to import charges from file.",
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ChargesImportScreenDestination())
                    }
                )
            }

            item("ExportCharges") {
                SettingsCard(
                    title = "Export Charges",
                    subtitle = "Click here to export charges to file.",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    icon = Icons.Default.Upload,
                    onClick = {
                        navController.navigate(ChargesExportScreenDestination())
                    }
                )
            }
        }
    }
}