package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.MeasureUnit
import kotlinx.coroutines.flow.Flow

interface MeasureUnitRepository {

    suspend fun getAllMeasureUnits(searchText: String): Flow<List<MeasureUnit>>

    suspend fun getMeasureUnitById(unitId: Int): Resource<MeasureUnit?>

    suspend fun upsertMeasureUnit(newUnit: MeasureUnit): Resource<Boolean>

    suspend fun deleteMeasureUnits(unitIds: List<Int>): Resource<Boolean>

    suspend fun importDataFromFilesToDatabase(units: List<MeasureUnit>): Resource<Boolean>
}