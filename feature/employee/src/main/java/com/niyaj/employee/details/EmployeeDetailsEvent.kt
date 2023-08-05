package com.niyaj.employee.details

sealed class EmployeeDetailsEvent {

    data class OnChooseSalaryDate(val date: Pair<String, String>) : EmployeeDetailsEvent()
}
