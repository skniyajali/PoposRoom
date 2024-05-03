package com.niyaj.product.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.product.destinations.DecreaseProductPriceScreenDestination
import com.niyaj.product.destinations.ExportProductScreenDestination
import com.niyaj.product.destinations.ImportProductScreenDestination
import com.niyaj.product.destinations.IncreaseProductPriceScreenDestination
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardBottomSheet
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ProductSettingScreen(
    navigator: DestinationsNavigator,
) {
    TrackScreenViewEvent(screenName = "Product Setting Screen")

    val lazyListState = rememberLazyListState()

    TrackScrollJank(scrollableState = lazyListState, stateName = "Product Settings::List")

    StandardBottomSheet(
        title = "Product Settings",
        onBackClick = navigator::navigateUp,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item("IncreaseProductPrice") {
                SettingsCard(
                    title = "Increase Product Price",
                    subtitle = "Click here to increase product price.",
                    icon = PoposIcons.ControlPoint,
                    onClick = {
                        navigator.navigate(IncreaseProductPriceScreenDestination)
                    },
                )
            }

            item("decreaseProductPrice") {
                SettingsCard(
                    title = "Decrease Product Price",
                    subtitle = "Click here to decrease product price.",
                    icon = PoposIcons.RemoveCircleOutline,
                    onClick = {
                        navigator.navigate(DecreaseProductPriceScreenDestination)
                    },
                )
            }

            item("ImportProduct") {
                SettingsCard(
                    title = "Import Product",
                    subtitle = "Click here to import product from file.",
                    icon = PoposIcons.Import,
                    onClick = {
                        navigator.navigate(ImportProductScreenDestination())
                    },
                )
            }

            item("ExportProduct") {
                SettingsCard(
                    title = "Export Product",
                    subtitle = "Click here to export products to file.",
                    icon = PoposIcons.Upload,
                    onClick = {
                        navigator.navigate(ExportProductScreenDestination())
                    },
                )
            }
        }
    }
}