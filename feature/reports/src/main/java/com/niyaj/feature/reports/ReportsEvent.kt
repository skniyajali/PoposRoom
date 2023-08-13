package com.niyaj.feature.reports

sealed class ReportsEvent {

    data class SelectDate(val date: String) : ReportsEvent()

    data class OnChangeOrderType(val orderType: String = "") : ReportsEvent()

    data class OnChangeCategoryOrderType(val orderType: String = "") : ReportsEvent()

    data class OnSelectCategory(val categoryName: String) : ReportsEvent()

    data object PrintReport : ReportsEvent()

    data object RefreshReport : ReportsEvent()

    data object GenerateReport : ReportsEvent()

}
