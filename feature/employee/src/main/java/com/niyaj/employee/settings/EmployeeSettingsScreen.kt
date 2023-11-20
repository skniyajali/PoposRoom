package com.niyaj.employee.settings

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
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SETTINGS_TITLE
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.employee.destinations.EmployeeExportScreenDestination
import com.niyaj.employee.destinations.EmployeeImportScreenDestination
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun EmployeeSettingsScreen(
    navController: NavController
) {
    val lazyListState = rememberLazyListState()

    StandardBottomSheet(
        title = EMPLOYEE_SETTINGS_TITLE,
        onBackClick = { navController.navigateUp() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ){
            item("ImportEmployee") {
                SettingsCard(
                    title = "Import Employees",
                    subtitle = "Click here to import data from file.",
                    icon = Icons.Default.SaveAlt,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = {
                        navController.navigate(EmployeeImportScreenDestination())
                    }
                )
            }

            item("ExportExpense") {
                SettingsCard(
                    title = "Export Employees",
                    subtitle = "Click here to export data to file.",
                    containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    icon = Icons.Default.Upload,
                    onClick = {
                        navController.navigate(EmployeeExportScreenDestination())
                    }
                )
            }
        }
    }
}