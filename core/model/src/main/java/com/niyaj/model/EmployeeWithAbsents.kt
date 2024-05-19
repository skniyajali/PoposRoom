package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmployeeWithAbsents(
    val employee: Employee,

    val absents: List<Absent> = emptyList(),
)