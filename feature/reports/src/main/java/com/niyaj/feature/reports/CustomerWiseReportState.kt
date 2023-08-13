package com.niyaj.feature.reports

import com.niyaj.model.CustomerWiseReport

data class CustomerWiseReportState(
    val reports: List<CustomerWiseReport> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
