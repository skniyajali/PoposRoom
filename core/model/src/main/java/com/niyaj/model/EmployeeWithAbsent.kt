package com.niyaj.model

data class EmployeeWithAbsent(
    val employeeId: Int,

    val absentId: Int
)


data class EmployeeWithAbsents(
    val employee: Employee,

    val absents: List<Absent> = emptyList()
)