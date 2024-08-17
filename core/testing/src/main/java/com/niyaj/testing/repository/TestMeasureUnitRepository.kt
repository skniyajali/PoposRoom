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

package com.niyaj.testing.repository

import com.niyaj.common.result.Resource
import com.niyaj.data.repository.MeasureUnitRepository
import com.niyaj.model.MeasureUnit
import com.niyaj.model.searchMeasureUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestMeasureUnitRepository : MeasureUnitRepository {
    /**
     * The backing measure unit list for testing
     */
    private val items = MutableStateFlow(mutableListOf<MeasureUnit>())

    override suspend fun getAllMeasureUnits(searchText: String): Flow<List<MeasureUnit>> {
        return items.mapLatest { it.searchMeasureUnit(searchText) }
    }

    override suspend fun getMeasureUnitById(unitId: Int): Resource<MeasureUnit?> {
        val item = items.value.find { it.unitId == unitId }
        return if (item != null) {
            Resource.Success(item)
        } else {
            Resource.Error("Item not found")
        }
    }

    override suspend fun upsertMeasureUnit(newUnit: MeasureUnit): Resource<Boolean> {
        val index = items.value.indexOfFirst { it.unitId == newUnit.unitId }

        if (index != -1) {
            items.value[index] = newUnit
        } else {
            items.value.add(newUnit)
        }

        return Resource.Success(true)
    }

    override suspend fun deleteMeasureUnits(unitIds: List<Int>): Resource<Boolean> {
        return Resource.Success(items.value.removeAll { it.unitId in unitIds })
    }

    override suspend fun findMeasureUnitByName(unitName: String, unitId: Int?): Boolean {
        return items.value.any {
            if (unitId != null) {
                it.unitName == unitName && it.unitId != unitId
            } else {
                it.unitName == unitName
            }
        }
    }

    override suspend fun importDataFromFilesToDatabase(units: List<MeasureUnit>): Resource<Boolean> {
        units.forEach {
            upsertMeasureUnit(it)
        }

        return Resource.Success(true)
    }

    @TestOnly
    fun updateMeasureUnitData(units: List<MeasureUnit>) {
        items.update { units.toMutableList() }
    }

    @TestOnly
    fun createTestItem(): MeasureUnit {
        val item = MeasureUnit(
            unitId = 1,
            unitName = "Test Unit",
            unitValue = 0.5,
        )

        items.value.add(item)
        return item
    }
}
