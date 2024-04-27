package com.niyaj.customer.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SETTINGS_TITLE
import com.niyaj.customer.destinations.CustomerExportScreenDestination
import com.niyaj.customer.destinations.CustomerImportScreenDestination
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
fun CustomerSettingsScreen(
    navigator: DestinationsNavigator,
) {
    TrackScreenViewEvent(screenName = "Customer Settings Screen")

    val lazyListState = rememberLazyListState()

    TrackScrollJank(scrollableState = lazyListState, stateName = "Customer Settings::List")

    StandardBottomSheet(
        title = CUSTOMER_SETTINGS_TITLE,
        onBackClick = navigator::navigateUp,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item("ImportCustomer") {
                SettingsCard(
                    title = "Import Customer",
                    subtitle = "Click here to import data from file.",
                    icon = PoposIcons.Import,
                    onClick = {
                        navigator.navigate(CustomerImportScreenDestination())
                    },
                )
            }

            item("ExportCustomer") {
                SettingsCard(
                    title = "Export Customer",
                    subtitle = "Click here to export data to file.",
                    icon = PoposIcons.Upload,
                    onClick = {
                        navigator.navigate(CustomerExportScreenDestination())
                    },
                )
            }
        }
    }
}