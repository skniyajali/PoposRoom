package com.niyaj.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.niyaj.designsystem.icon.PoposIcons

sealed class OrderTab(
    val icon: ImageVector,
    val title: String,
    val showBadge: Boolean = false,
    val screen: @Composable () -> Unit,
) {

    data class DineInOrder(
        val shouldShowBadge: Boolean = false,
        val content: @Composable () -> Unit = {},
    ) : OrderTab(
        icon = PoposIcons.DinnerDining,
        title = "DineIn",
        showBadge = shouldShowBadge,
        screen = {
            content()
        },
    )

    data class DineOutOrder(
        val shouldShowBadge: Boolean = false,
        val content: @Composable () -> Unit = {},
    ) : OrderTab(
        icon = PoposIcons.DeliveryDining,
        title = "DineOut",
        showBadge = shouldShowBadge,
        screen = {
            content()
        },
    )
}
