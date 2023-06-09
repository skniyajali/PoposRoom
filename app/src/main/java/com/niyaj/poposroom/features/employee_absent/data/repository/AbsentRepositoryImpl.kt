package com.niyaj.poposroom.features.employee_absent.data.repository

import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.ValidationResult
import com.niyaj.poposroom.features.employee.domain.model.Employee
import com.niyaj.poposroom.features.employee.domain.model.filterEmployee
import com.niyaj.poposroom.features.employee_absent.data.dao.AbsentDao
import com.niyaj.poposroom.features.employee_absent.domain.model.Absent
import com.niyaj.poposroom.features.employee_absent.domain.model.EmployeeWithAbsent
import com.niyaj.poposroom.features.employee_absent.domain.model.EmployeeWithAbsentCrossRef
import com.niyaj.poposroom.features.employee_absent.domain.model.filterAbsent
import com.niyaj.poposroom.features.employee_absent.domain.repository.AbsentRepository
import com.niyaj.poposroom.features.employee_absent.domain.repository.AbsentValidationRepository
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_DATE_EMPTY
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_DATE_EXIST
import com.niyaj.poposroom.features.employee_absent.domain.utils.AbsentScreenTags.ABSENT_EMPLOYEE_NAME_EMPTY
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class AbsentRepositoryImpl(
    private val absentDao: AbsentDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : AbsentRepository, AbsentValidationRepository {
    override fun getAllEmployee(): Flow<List<Employee>> = absentDao.getAllEmployee()

    override suspend fun getEmployeeById(employeeId: Int): Employee? {
        return withContext(ioDispatcher) {
            absentDao.getEmployeeById(employeeId)
        }
    }

    override suspend fun getAllEmployeeAbsents(searchText: String): Flow<List<EmployeeWithAbsent>> {
        return withContext(ioDispatcher) {
            absentDao.getAllAbsentEmployee().mapLatest { list ->
                list.filter { it.employee.filterEmployee(searchText) }
            }
        }
    }

    override suspend fun getAllAbsent(searchText: String): Flow<List<Absent>> {
        return withContext(ioDispatcher) {
            absentDao.getAllAbsent().mapLatest { list ->
                list.filter { it.filterAbsent(searchText) }
            }
        }
    }

    override suspend fun getAbsentById(absentId: Int): Resource<Absent?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(absentDao.getAbsentById(absentId))
            }
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrIgnoreAbsent(newAbsent: Absent): Resource<Boolean> {
        return try {
            val validateAbsentEmployee = validateAbsentEmployee(newAbsent.employeeId)
            val validateAbsentDate = validateAbsentDate(
                absentDate = newAbsent.absentDate,
                employeeId = newAbsent.employeeId
            )

            val hasError = listOf(validateAbsentEmployee, validateAbsentDate).any { !it.successful }

            if (!hasError) {
                val result = absentDao.insertOrIgnoreAbsent(newAbsent)

                if (result > 0) {
                    absentDao.upsertEmployeeWithAbsentCrossReference(
                        EmployeeWithAbsentCrossRef(newAbsent.employeeId, result.toInt())
                    )
                }

                Resource.Success(result > 0)
            }else {
                Resource.Error("Unable to validate attendance")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add absent entry.")
        }
    }

    override suspend fun updateAbsent(newAbsent: Absent): Resource<Boolean> {
        return try {
            val validateAbsentEmployee = validateAbsentEmployee(newAbsent.employeeId)
            val validateAbsentDate = validateAbsentDate(
                absentDate = newAbsent.absentDate,
                employeeId = newAbsent.employeeId,
                absentId = newAbsent.absentId
            )

            val hasError = listOf(validateAbsentEmployee, validateAbsentDate).any { !it.successful }

            if (!hasError) {
                val result = absentDao.updateAbsent(newAbsent)

                Resource.Success(result > 0)
            }else {
                Resource.Error("Unable to validate attendance")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update absent entry.")
        }
    }

    override suspend fun upsertAbsent(newAbsent: Absent): Resource<Boolean> {
        return try {
            val validateAbsentEmployee = validateAbsentEmployee(newAbsent.employeeId)
            val validateAbsentDate = validateAbsentDate(
                absentDate = newAbsent.absentDate,
                employeeId = newAbsent.employeeId,
                absentId = newAbsent.absentId
            )

            val hasError = listOf(validateAbsentEmployee, validateAbsentDate).any { !it.successful }

            if (!hasError) {
                val result = absentDao.upsertAbsent(newAbsent)

                if (result > 0) {
                    absentDao.upsertEmployeeWithAbsentCrossReference(
                        EmployeeWithAbsentCrossRef(newAbsent.employeeId, result.toInt())
                    )
                }

                Resource.Success(result > 0)
            }else {
                Resource.Error("Unable to validate attendance")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add or update absent entry.")
        }
    }

    override suspend fun deleteAbsent(absentId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = absentDao.deleteAbsent(absentId)

                Resource.Success(result > 0)
            }
        }catch (e: Exception) {
            Resource.Error("Unable to delete absent entry")
        }
    }

    override suspend fun deleteAbsents(absentIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = absentDao.deleteAbsents(absentIds)

                Resource.Success(result > 0)
            }
        }catch (e: Exception) {
            Resource.Error("Unable to delete absent entries")
        }
    }

    override suspend fun validateAbsentDate(
        absentDate: String,
        employeeId: Int?,
        absentId: Int?
    ): ValidationResult {
        if (absentDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ABSENT_DATE_EMPTY
            )
        }

        if (employeeId != null) {
            val serverResult = withContext(ioDispatcher) {
                absentDao.findEmployeeByDate(absentDate, employeeId, absentId) != null
            }

            if(serverResult){
                return ValidationResult(
                    successful = false,
                    errorMessage = ABSENT_DATE_EXIST,
                )
            }
        }

        return ValidationResult(true)
    }

    override fun validateAbsentEmployee(employeeId: Int): ValidationResult {
        if (employeeId == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = ABSENT_EMPLOYEE_NAME_EMPTY,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}