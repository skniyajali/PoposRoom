package com.niyaj.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Dining
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

typealias ComposableFunction = @Composable () -> Unit

sealed class OrderTab(
    val icon: ImageVector,
    val title: String,
    val screen: ComposableFunction,
) {

    data class DineInOrder(
        val content: @Composable () -> Unit = {},
    ) : OrderTab(
        icon = Icons.Default.Dining,
        title = "DineIn",
        screen = {
            content()
        }
    )

    data class DineOutOrder(
        val content: @Composable () -> Unit = {},
    ) : OrderTab(
        icon = Icons.Default.DeliveryDining,
        title = "DineOut",
        screen = {
            content()
        }
    )
}
