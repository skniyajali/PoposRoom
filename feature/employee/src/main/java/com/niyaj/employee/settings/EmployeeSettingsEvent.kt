package com.niyaj.employee.settings

import com.niyaj.model.Employee

sealed class EmployeeSettingsEvent {

    data object GetExportedItems: EmployeeSettingsEvent()

    data class OnImportEmployeeItemsFromFile(val data: List<Employee>): EmployeeSettingsEvent()

    data object ImportEmployeeItemsToDatabase: EmployeeSettingsEvent()
}