/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.MeasureUnitRepository
import com.niyaj.database.dao.MeasureUnitDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.MeasureUnit
import com.niyaj.model.searchMeasureUnit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MeasureUnitRepositoryImpl @Inject constructor(
    private val measureUnitDao: MeasureUnitDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : MeasureUnitRepository {
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
            val result = withContext(ioDispatcher) {
                measureUnitDao.upsertMeasureUnit(newUnit.toEntity())
            }

            Resource.Success(result > 0)
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

    override suspend fun findMeasureUnitByName(unitName: String, unitId: Int?): Boolean {
        return withContext(ioDispatcher) {
            measureUnitDao.findMeasureUnitByName(unitName, unitId) != null
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
}
