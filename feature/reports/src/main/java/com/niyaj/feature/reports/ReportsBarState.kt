package com.niyaj.feature.reports

import com.niyaj.feature.chart.horizontalbar.model.HorizontalBarData

data class ReportsBarState(
    val reportBarData: List<HorizontalBarData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)