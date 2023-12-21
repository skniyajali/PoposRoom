package com.niyaj.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class EmployeeAbsentDates(
    val startDate: String = "",
    val endDate: String = "",
    val absentDates: ImmutableList<String> = persistentListOf(),
)
