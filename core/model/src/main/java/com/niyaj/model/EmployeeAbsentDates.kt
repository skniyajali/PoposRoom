package com.niyaj.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class EmployeeAbsentDates(
    val startDate: String = "",
    val endDate: String = "",
    val absentDates: ImmutableList<String> = persistentListOf(),
)
