package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_EMPTY
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_EXIST
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_EMPLOYEE_NAME_EMPTY
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.AbsentRepository
import com.niyaj.data.repository.validation.AbsentValidationRepository
import com.niyaj.database.dao.AbsentDao
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.EmployeeWithAbsentCrossRef
import com.niyaj.database.model.EmployeeWithAbsentsDto
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Absent
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeWithAbsents
import com.niyaj.model.filterAbsent
import com.niyaj.model.filterEmployee
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext


@OptIn(ExperimentalCoroutinesApi::class)
class AbsentRepositoryImpl(
    private val absentDao: AbsentDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : AbsentRepository, AbsentValidationRepository {
    override fun getAllEmployee(): Flow<List<Employee>> =
        absentDao.getAllEmployee().mapLatest { list ->
            list.map(EmployeeEntity::asExternalModel)
        }

    override suspend fun getEmployeeById(employeeId: Int): Employee? {
        return withContext(ioDispatcher) {
            absentDao.getEmployeeById(employeeId)?.asExternalModel()
        }
    }

    override suspend fun getAllEmployeeAbsents(searchText: String): Flow<List<EmployeeWithAbsents>> {
        return withContext(ioDispatcher) {
            absentDao.getAllAbsentEmployee().mapLatest { list ->
                list.filter { it.absents.isNotEmpty() }
                    .map(EmployeeWithAbsentsDto::asExternalModel)
                    .filter {
                        it.employee.filterEmployee(searchText)
                    }
            }
        }
    }

    override suspend fun getAllAbsent(searchText: String): Flow<List<Absent>> {
        return withContext(ioDispatcher) {
            absentDao.getAllAbsent().mapLatest { list ->
                list
                    .map { it.asExternalModel() }
                    .filter { it.filterAbsent(searchText) }
            }
        }
    }

    override suspend fun getAbsentById(absentId: Int): Resource<Absent?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(absentDao.getAbsentById(absentId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertAbsent(newAbsent: Absent): Resource<Boolean> {
        return try {
            val validateAbsentEmployee = validateAbsentEmployee(newAbsent.employeeId)
            val validateAbsentDate = validateAbsentDate(
                absentDate = newAbsent.absentDate,
                employeeId = newAbsent.employeeId,
                absentId = newAbsent.absentId,
            )

            val hasError = listOf(validateAbsentEmployee, validateAbsentDate).any { !it.successful }

            if (!hasError) {
                val result = absentDao.upsertAbsent(newAbsent.toEntity())

                if (result > 0) {
                    absentDao.upsertEmployeeWithAbsentCrossReference(
                        EmployeeWithAbsentCrossRef(newAbsent.employeeId, result.toInt()),
                    )
                }

                Resource.Success(result > 0)
            } else {
                Resource.Error("Unable to validate attendance")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add or update absent entry.")
        }
    }

    override suspend fun deleteAbsents(absentIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = absentDao.deleteAbsents(absentIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error("Unable to delete absent entries")
        }
    }

    override suspend fun validateAbsentDate(
        absentDate: String,
        employeeId: Int?,
        absentId: Int?,
    ): ValidationResult {
        if (absentDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ABSENT_DATE_EMPTY,
            )
        }

        if (employeeId != null) {
            val serverResult = withContext(ioDispatcher) {
                absentDao.findEmployeeByDate(absentDate, employeeId, absentId) != null
            }

            if (serverResult) {
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

    override suspend fun importAbsentDataToDatabase(absentees: List<EmployeeWithAbsents>): Resource<Boolean> {
        try {
            absentees.forEach { empWithAbsents ->
                val findEmployee = withContext(ioDispatcher) {
                    absentDao.findEmployeeByName(
                        empWithAbsents.employee.employeeName,
                        empWithAbsents.employee.employeeId,
                    )
                }

                if (findEmployee != null) {
                    empWithAbsents.absents.forEach { newAbsent ->
                        upsertAbsent(newAbsent)
                    }
                } else {
                    val result = withContext(ioDispatcher) {
                        absentDao.upsertEmployee(empWithAbsents.employee.toEntity())
                    }

                    if (result > 0) {
                        empWithAbsents.absents.forEach { newAbsent ->
                            upsertAbsent(newAbsent)
                        }
                    } else {
                        return Resource.Error("Something went wrong inserting employee!")
                    }
                }
            }

            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unable to add or update absent entry.")
        }
    }
}