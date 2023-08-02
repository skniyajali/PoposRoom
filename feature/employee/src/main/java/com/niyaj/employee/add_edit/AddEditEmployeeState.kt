package com.niyaj.employee.add_edit

import com.niyaj.common.utils.toMilliSecond
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
import java.time.LocalDate

data class AddEditEmployeeState(
    val employeePhone: String = "",
    val employeeName: String = "",
    val employeeSalary: String = "",
    val employeePosition: String = "",
    val employeeEmail: String? = null,
    val employeeSalaryType: EmployeeSalaryType = EmployeeSalaryType.Monthly,
    val employeeType: EmployeeType = EmployeeType.FullTime,
    val employeeJoinedDate: String = LocalDate.now().toMilliSecond,
)


val positions = listOf(
    "Master",
    "Assistant",
    "Captain",
    "Manager",
    "Cleaner",
    "Senior Cook",
    "Junior Cook",
    "Chef"
)