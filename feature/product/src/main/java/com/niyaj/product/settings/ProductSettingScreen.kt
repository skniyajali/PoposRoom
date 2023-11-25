package com.niyaj.product.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ControlPoint
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.product.destinations.DecreaseProductPriceScreenDestination
import com.niyaj.product.destinations.ExportProductScreenDestination
import com.niyaj.product.destinations.ImportProductScreenDestination
import com.niyaj.product.destinations.IncreaseProductPriceScreenDestination
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ProductSettingScreen(
    navController: NavController
) {
    val lazyListState = rememberLazyListState()

    StandardBottomSheet(
        title = "Product Settings",
        onBackClick = { navController.navigateUp() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ){
            item("IncreaseProductPrice") {
                SettingsCard(
                    title = "Increase Product Price",
                    subtitle = "Click here to increase product price.",
                    icon = Icons.Default.ControlPoint,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = {
                        navController.navigate(IncreaseProductPriceScreenDestination)
                    }
                )
            }

            item("decreaseProductPrice") {
                SettingsCard(
                    title = "Decrease Product Price",
                    subtitle = "Click here to decrease product price.",
                    icon = Icons.Default.RemoveCircleOutline,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    onClick = {
                        navController.navigate(DecreaseProductPriceScreenDestination)
                    }
                )
            }

            item("ImportProduct") {
                SettingsCard(
                    title = "Import Product",
                    subtitle = "Click here to import product from file.",
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ImportProductScreenDestination())
                    }
                )
            }

            item("ExportProduct") {
                SettingsCard(
                    title = "Export Product",
                    subtitle = "Click here to export products to file.",
                    icon = Icons.Default.Upload,
                    onClick = {
                        navController.navigate(ExportProductScreenDestination())
                    }
                )
            }
        }
    }
}