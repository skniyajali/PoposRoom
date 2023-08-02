package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
import java.util.Date

@Entity(tableName = "employee")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val employeeId: Int = 0,

    val employeeName: String = "",

    val employeePhone: String = "",

    val employeeSalary: String = "",

    val employeePosition: String = "",

    val employeeJoinedDate: String = "",

    val employeeEmail: String? = null,

    val employeeSalaryType: EmployeeSalaryType = EmployeeSalaryType.Daily,

    val employeeType: EmployeeType = EmployeeType.FullTime,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun EmployeeEntity.asExternalModel(): Employee {
    return Employee(
        employeeId = this.employeeId,
        employeeName = this.employeeName,
        employeePhone = this.employeePhone,
        employeeSalary = this.employeeSalary,
        employeePosition = this.employeePosition,
        employeeJoinedDate = this.employeeJoinedDate,
        employeeEmail = this.employeeEmail,
        employeeSalaryType = this.employeeSalaryType,
        employeeType = this.employeeType,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}