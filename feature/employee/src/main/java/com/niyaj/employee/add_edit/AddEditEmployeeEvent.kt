package com.niyaj.employee.add_edit

import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType


sealed interface AddEditEmployeeEvent {
    /**
     * Employee Name Changed Event Class
     * @param employeeName [String]
     */
    data class EmployeeNameChanged(val employeeName: String) : AddEditEmployeeEvent

    /**
     * Employee Email Changed Event Class
     * @param employeeSalary [String]
     */
    data class EmployeeSalaryChanged(val employeeSalary: String) : AddEditEmployeeEvent

    /**
     * Employee Phone Changed Event Class
     * @param employeePhone [String]
     */
    data class EmployeePhoneChanged(val employeePhone: String) : AddEditEmployeeEvent

    data class EmployeeEmailChanged(val employeeEmail: String) : AddEditEmployeeEvent

    data class EmployeeSalaryTypeChanged(val employeeSalaryType: EmployeeSalaryType) :
        AddEditEmployeeEvent

    data class EmployeePositionChanged(val employeePosition: String) : AddEditEmployeeEvent

    data class EmployeeTypeChanged(val employeeType: EmployeeType) : AddEditEmployeeEvent

    data class EmployeeJoinedDateChanged(val employeeJoinedDate: String) : AddEditEmployeeEvent

    data class CreateOrUpdateEmployee(val employeeId: Int = 0) : AddEditEmployeeEvent
}