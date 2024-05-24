/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.MeasureUnitTestTags
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_DIGIT_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_EMPTY_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_LENGTH_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_VALUE_EMPTY_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_VALUE_INVALID
import com.niyaj.common.utils.safeDouble
import com.niyaj.common.utils.safeString
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.MeasureUnitRepository
import com.niyaj.data.repository.validation.MeasureUnitValidationRepository
import com.niyaj.database.dao.MeasureUnitDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.MeasureUnit
import com.niyaj.model.searchMeasureUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext


class MeasureUnitRepositoryImpl(
    private val measureUnitDao: MeasureUnitDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : MeasureUnitRepository, MeasureUnitValidationRepository {
    override suspend fun getAllMeasureUnits(searchText: String): Flow<List<MeasureUnit>> {
        return withContext(ioDispatcher) {
            measureUnitDao.getAllMeasureUnits().mapLatest { list ->
                list.map {
                    it.asExternalModel()
                }.searchMeasureUnit(searchText)
            }
        }
    }

    override suspend fun getMeasureUnitById(unitId: Int): Resource<MeasureUnit?> {
        return try {
            val result = withContext(ioDispatcher) {
                measureUnitDao.getMeasureUnitById(unitId)
            }?.asExternalModel()

            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertMeasureUnit(newUnit: MeasureUnit): Resource<Boolean> {
        return try {

            val validateUnitName = validateUnitName(newUnit.unitName, newUnit.unitId)
            val validateUnitValue = validateUnitValue(newUnit.unitValue.safeString)

            if (listOf(validateUnitName, validateUnitValue).all { it.successful }) {
                val result = withContext(ioDispatcher) {
                    measureUnitDao.upsertMeasureUnit(newUnit.toEntity())
                }

                Resource.Success(result > 0)
            } else {
                Resource.Error("Unable to validate items")
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun deleteMeasureUnits(unitIds: List<Int>): Resource<Boolean> {
        return try {
            val result = withContext(ioDispatcher) {
                measureUnitDao.deleteMeasureUnits(unitIds)
            }

            Resource.Success(result > 0)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun importDataFromFilesToDatabase(units: List<MeasureUnit>): Resource<Boolean> {
        return try {
            units.forEach {
                upsertMeasureUnit(it)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun validateUnitName(unitName: String, unitId: Int?): ValidationResult {
        if (unitName.isEmpty()) {
            return ValidationResult(false, UNIT_NAME_EMPTY_ERROR)
        }

        if (unitName.any { it.isDigit() }) {
            return ValidationResult(false, UNIT_NAME_DIGIT_ERROR)
        }

        if (unitName.length < 2) {
            return ValidationResult(false, UNIT_NAME_LENGTH_ERROR)
        }

        val result = withContext(ioDispatcher) {
            measureUnitDao.findMeasureUnitByName(unitName, unitId)
        }

        if (result != null) {
            return ValidationResult(false, UNIT_NAME_ALREADY_EXIST_ERROR)
        }

        return ValidationResult(true)
    }

    override fun validateUnitValue(unitValue: String): ValidationResult {
        if (unitValue.isEmpty()) {
            return ValidationResult(false, UNIT_VALUE_EMPTY_ERROR)
        }

        try {
            if (unitValue.safeDouble() <= 0) {
                return ValidationResult(false, MeasureUnitTestTags.UNIT_VALUE_LESS_THAN_FIVE_ERROR)
            }
        } catch (e: Exception) {
            return ValidationResult(false, UNIT_VALUE_INVALID)
        }

        return ValidationResult(true)
    }
}