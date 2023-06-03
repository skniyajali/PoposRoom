package com.niyaj.poposroom.features.employee.domain.use_cases

import com.niyaj.poposroom.features.employee.dao.EmployeeDao
import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee.domain.model.searchEmployee
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class GetAllEmployee(
    private val employeeDao: EmployeeDao
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(searchText: String): Flow<List<Employee>> {
        return employeeDao.getAllEmployee().mapLatest { it.searchEmployee(searchText) }
    }
}