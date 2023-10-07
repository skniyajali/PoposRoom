package com.niyaj.model

import com.squareup.moshi.JsonClass

data class EmployeeWithAbsent(
    val employeeId: Int,

    val absentId: Int
)

@JsonClass(generateAdapter = true)
data class EmployeeWithAbsents(
    val employee: Employee,

    val absents: List<Absent> = emptyList()
)