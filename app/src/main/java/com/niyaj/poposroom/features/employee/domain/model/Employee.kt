package com.niyaj.poposroom.features.employee.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeSalaryType
import com.niyaj.poposroom.features.employee.domain.utils.EmployeeType
import java.util.Date

@Entity(tableName = "employee")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val employeeId: Int,

    val employeeName: String = "",

    val employeePhone: String = "",

    val employeeSalary: String = "",

    val employeePosition: String = "",

    val employeeJoinedDate: String = "",

    val employeeEmail: String? = null,

    val employeeSalaryType: EmployeeSalaryType = EmployeeSalaryType.Daily,

    val employeeType: EmployeeType = EmployeeType.FullTime,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun List<Employee>.searchEmployee(searchText: String): List<Employee> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.employeeName.contains(searchText, true) ||
            it.employeePosition.contains(searchText, true) ||
            it.employeePhone.contains(searchText, true) ||
            it.employeeSalaryType.name.contains(searchText, true) ||
            it.employeeSalary.contains(searchText, true) ||
            it.employeeJoinedDate.contains(searchText, true)
        }
    }else this
}