package com.niyaj.feature.reports

import com.niyaj.model.CategoryWiseReport

data class CategoryWiseReportState(
    val categoryWiseReport: List<CategoryWiseReport> = emptyList(),
    val orderType: String = "",
    val isLoading: Boolean = false,
    val hasError: String? = null
)
