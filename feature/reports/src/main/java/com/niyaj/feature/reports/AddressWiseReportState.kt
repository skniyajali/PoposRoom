package com.niyaj.feature.reports

import com.niyaj.model.AddressWiseReport

data class AddressWiseReportState(
    val reports: List<AddressWiseReport> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
