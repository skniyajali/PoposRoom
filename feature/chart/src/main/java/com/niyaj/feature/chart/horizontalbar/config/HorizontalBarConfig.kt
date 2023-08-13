package com.niyaj.feature.chart.horizontalbar.config

data class HorizontalBarConfig(
    val showLabels: Boolean = true,
    val startDirection: StartDirection,
    val productReport: Boolean = true,
)

internal object HorizontalBarConfigDefaults {

    fun horizontalBarConfig() = HorizontalBarConfig(
        showLabels = true,
        startDirection = StartDirection.Right,
        productReport = true,
    )
}

sealed interface StartDirection {
    data object Left : StartDirection
    data object Right : StartDirection
}
