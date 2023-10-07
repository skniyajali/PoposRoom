package com.niyaj.employee_absent.settings

import com.niyaj.model.Absent
import com.niyaj.model.EmployeeWithAbsents

sealed class AbsentSettingsEvent {

    data object GetExportedItems: AbsentSettingsEvent()

    data class OnImportAbsentItemsFromFile(val data: List<EmployeeWithAbsents>): AbsentSettingsEvent()

    data object ImportAbsentItemsToDatabase: AbsentSettingsEvent()
}