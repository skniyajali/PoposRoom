package com.niyaj.employee_payment.settings

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
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SETTINGS_TITLE
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.employee_payment.destinations.PaymentExportScreenDestination
import com.niyaj.employee_payment.destinations.PaymentImportScreenDestination
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun PaymentSettingsScreen(
    navController: NavController
) {
    TrackScreenViewEvent(screenName = "Payment Setting Screen")

    val lazyListState = rememberLazyListState()

    TrackScrollJank(scrollableState = lazyListState, stateName = "Payment Settings::List")

    StandardBottomSheet(
        title = PAYMENT_SETTINGS_TITLE,
        onBackClick = { navController.navigateUp() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ) {
            item("ImportPayment") {
                SettingsCard(
                    title = "Import Employee Payment",
                    subtitle = "Click here to import data from file.",
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(PaymentImportScreenDestination())
                    }
                )
            }

            item("ExportPayment") {
                SettingsCard(
                    title = "Export Employee Payment",
                    subtitle = "Click here to export data to file.",
                    icon = Icons.Default.Upload,
                    onClick = {
                        navController.navigate(PaymentExportScreenDestination())
                    }
                )
            }
        }
    }
}