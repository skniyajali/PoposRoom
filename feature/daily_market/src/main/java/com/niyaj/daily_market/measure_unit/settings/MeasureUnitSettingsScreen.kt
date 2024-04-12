package com.niyaj.daily_market.measure_unit.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_SETTINGS_TITLE
import com.niyaj.daily_market.destinations.ExportMeasureUnitScreenDestination
import com.niyaj.daily_market.destinations.ImportMeasureUnitScreenDestination
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun MeasureUnitSettingsScreen(
    navController: NavController
) {
    val lazyListState = rememberLazyListState()

    StandardBottomSheet(
        title = UNIT_SETTINGS_TITLE,
        onBackClick = { navController.navigateUp() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ){
            item("Import Measure Unit") {
                SettingsCard(
                    title = "Import Measure Unit",
                    subtitle = "Click here to import data from file.",
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ImportMeasureUnitScreenDestination())
                    }
                )
            }

            item("ExportMarketItem") {
                SettingsCard(
                    title = "Export Measure Unit",
                    subtitle = "Click here to export data to file.",
                    icon = Icons.Default.Upload,
                    onClick = {
                        navController.navigate(ExportMeasureUnitScreenDestination())
                    }
                )
            }
        }
    }
}