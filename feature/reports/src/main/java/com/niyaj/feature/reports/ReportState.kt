package com.niyaj.feature.reports

import com.niyaj.model.Reports

data class ReportState(
    val report: Reports = Reports(),
    val isLoading: Boolean = false,
    val hasError: String? = null,
)