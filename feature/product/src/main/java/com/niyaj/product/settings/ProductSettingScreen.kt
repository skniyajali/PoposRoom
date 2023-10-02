package com.niyaj.product.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ControlPoint
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.product.destinations.DecreaseProductPriceScreenDestination
import com.niyaj.product.destinations.ExportProductScreenDestination
import com.niyaj.product.destinations.ImportProductScreenDestination
import com.niyaj.product.destinations.IncreaseProductPriceScreenDestination
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination
@Composable
fun ProductSettingScreen(
    navController: NavController,
    exportRecipient: ResultRecipient<ExportProductScreenDestination, String>,
    importRecipient: ResultRecipient<ImportProductScreenDestination, String>,
    increaseRecipient: ResultRecipient<IncreaseProductPriceScreenDestination, String>,
    decreaseRecipient: ResultRecipient<DecreaseProductPriceScreenDestination, String>,
) {
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    exportRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }
    importRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }
    increaseRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }
    decreaseRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffoldNew(
        navController = navController,
        title = "Product Settings",
        snackbarHostState = snackbarState,
        showBackButton = true,
        showBottomBar = false,
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ){
            item("IncreaseProductPrice") {
                ProductSettingCard(
                    title = "Increase Product Price",
                    subtitle = "Click here to increase product price.",
                    icon = Icons.Default.ControlPoint,
                    onClick = {
                        navController.navigate(IncreaseProductPriceScreenDestination)
                    }
                )
            }

            item("decreaseProductPrice") {
                ProductSettingCard(
                    title = "Decrease Product Price",
                    subtitle = "Click here to decrease product price.",
                    icon = Icons.Default.RemoveCircleOutline,
                    onClick = {
                        navController.navigate(DecreaseProductPriceScreenDestination)
                    }
                )
            }

            item("ImportProduct") {
                ProductSettingCard(
                    title = "Import Product",
                    subtitle = "Click here to import product from file.",
                    icon = Icons.Default.SaveAlt,
                    onClick = {
                        navController.navigate(ImportProductScreenDestination())
                    }
                )
            }

            item("ExportProduct") {
                ProductSettingCard(
                    title = "Export Product",
                    subtitle = "Click here to import product from file.",
                    icon = Icons.Default.Upload,
                    onClick = {
                        navController.navigate(ExportProductScreenDestination())
                    }
                )
            }
        }
    }
}


@Composable
fun ProductSettingCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SpaceMini))
            .clickable {
                onClick()
            },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
        },
        supportingContent = {
            Text(text = subtitle)
        },
        leadingContent = {
            CircularBox(
                icon = icon,
                doesSelected = false,
                showBorder = false,
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Default.ArrowRight,
                contentDescription = "Arrow right icon"
            )
        },
        tonalElevation = 1.dp,
        shadowElevation = 4.dp,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}