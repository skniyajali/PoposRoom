package com.niyaj.poposroom.features.cart.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Dining
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

typealias ComposableFunction = @Composable () -> Unit

sealed class CartTabItem(
    val icon: ImageVector,
    val title: String,
    val screen: ComposableFunction
){

    data class DineInItem(
        val content: @Composable () -> Unit = {},
    ): CartTabItem(
        icon = Icons.Default.Dining,
        title = "DineIn",
        screen = {
            content()
        }
    )

    data class DineOutItem(
        val content: @Composable () -> Unit = {},
    ): CartTabItem(
        icon = Icons.Default.DeliveryDining,
        title = "DineOut",
        screen = {
            content()
        }
    )
}
