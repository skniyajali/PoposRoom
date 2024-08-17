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
import com.niyaj.common.utils.getStartDateLong
import com.niyaj.data.repository.MarketTypeRepository
import com.niyaj.model.MarketType
import com.niyaj.model.searchMarketType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestMarketTypeRepository : MarketTypeRepository {

    /**
     * The backing market type list for testing
     */
    private val items = MutableStateFlow(mutableListOf<MarketType>())

    override suspend fun getAllMarketTypes(searchText: String): Flow<List<MarketType>> {
        return items.mapLatest { it.searchMarketType(searchText) }
    }

    override suspend fun getMarketTypeById(id: Int): MarketType? {
        return items.value.find { it.typeId == id }
    }

    override suspend fun createOrUpdateMarketType(marketType: MarketType): Resource<Boolean> {
        val index = items.value.indexOfFirst { it.typeId == marketType.typeId }

        if (index != -1) {
            items.value[index] = marketType
        } else {
            items.value.add(marketType)
        }

        return Resource.Success(true)
    }

    override suspend fun deleteMarketTypes(items: List<Int>): Resource<Boolean> {
        return Resource.Success(this.items.value.removeAll { it.typeId in items })
    }

    override suspend fun findMarketTypeByName(typeName: String, typeId: Int?): Boolean {
        return items.value.any {
            if (typeId != null) {
                it.typeName == typeName && it.typeId != typeId
            } else {
                it.typeName == typeName
            }
        }
    }

    override suspend fun importDataFromFilesToDatabase(data: List<MarketType>): Resource<Boolean> {
        data.forEach {
            createOrUpdateMarketType(it)
        }

        return Resource.Success(true)
    }

    @TestOnly
    fun createTestItem(): MarketType {
        val item = MarketType(
            typeId = 1,
            typeName = "Test Type",
            listTypes = listOf("Test List Type"),
            createdAt = getStartDateLong,
        )

        items.value.add(item)

        return item
    }

    @TestOnly
    fun updateMarketTypeData(marketTypes: List<MarketType>) {
        items.update { marketTypes.toMutableList() }
    }
}
