package com.niyaj.model

import com.squareup.moshi.JsonClass
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class EmployeeWithAbsent(
    val employeeId: Int,

    val absentId: Int,
)

@JsonClass(generateAdapter = true)
data class EmployeeWithAbsents(
    val employee: Employee,

    val absents: ImmutableList<Absent> = persistentListOf(),
)