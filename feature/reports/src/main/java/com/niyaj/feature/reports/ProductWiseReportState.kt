package com.niyaj.feature.reports

import com.niyaj.feature.chart.horizontalbar.model.HorizontalBarData


data class ProductWiseReportState(
    val data: List<HorizontalBarData> = emptyList(),
    val orderType: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)