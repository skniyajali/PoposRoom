package com.niyaj.model

import androidx.compose.runtime.Stable
import com.squareup.moshi.JsonClass
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class EmployeeWithAbsent(
    val employeeId: Int,

    val absentId: Int
)

@JsonClass(generateAdapter = true)
@Stable
data class EmployeeWithAbsents(
    val employee: Employee,

    val absents: ImmutableList<Absent> = persistentListOf()
)