package com.niyaj.data.mapper

import com.niyaj.common.utils.toDate
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.model.Employee

fun Employee.toEntity(): EmployeeEntity {
    return EmployeeEntity(
        employeeId = this.employeeId,
        employeeName = this.employeeName,
        employeePhone = this.employeePhone,
        employeeSalary = this.employeeSalary,
        employeePosition = this.employeePosition,
        employeeJoinedDate = this.employeeJoinedDate,
        employeeEmail = this.employeeEmail,
        employeeSalaryType = this.employeeSalaryType,
        employeeType = this.employeeType,
        createdAt = this.createdAt.toDate,
        updatedAt = this.updatedAt?.toDate
    )
}