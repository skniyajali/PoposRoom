package com.niyaj.employee_absent.settings

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
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_SETTINGS_TITLE
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.employee_absent.destinations.AbsentExportScreenDestination
import com.niyaj.employee_absent.destinations.AbsentImportScreenDestination
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AbsentSettingsScreen(
    navController: NavController
) {
    TrackScreenViewEvent(screenName = "Absent Settings Screen")

    val lazyListState = rememberLazyListState()

    TrackScrollJank(scrollableState = lazyListState, stateName = "Absent Settings::List")

    StandardBottomSheet(
        title = ABSENT_SETTINGS_TITLE,
        onBackClick = { navController.navigateUp() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ){
            item("ImportAbsent") {
                SettingsCard(
                    title = "Import Absent Employee",
                    subtitle = "Click here to import data from file.",
                    icon = Icons.Default.SaveAlt,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = {
                        navController.navigate(AbsentImportScreenDestination())
                    }
                )
            }

            item("ExportAbsent") {
                SettingsCard(
                    title = "Export Absent Employee",
                    subtitle = "Click here to export data to file.",
                    icon = Icons.Default.Upload,
                    containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    onClick = {
                        navController.navigate(AbsentExportScreenDestination())
                    }
                )
            }
        }
    }
}