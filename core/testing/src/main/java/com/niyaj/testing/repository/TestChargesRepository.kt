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
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.model.Charges
import com.niyaj.model.searchCharges
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class TestChargesRepository : ChargesRepository {

    /**
     * The backing charges list for testing
     */
    private val items = MutableStateFlow(mutableListOf<Charges>())

    override suspend fun getAllCharges(searchText: String): Flow<List<Charges>> {
        return items.mapLatest { it.searchCharges(searchText) }
    }

    override suspend fun getChargesById(chargesId: Int): Resource<Charges?> {
        return Resource.Success(items.value.find { it.chargesId == chargesId })
    }

    override suspend fun upsertCharges(newCharges: Charges): Resource<Boolean> {
        val result = items.value.find { it.chargesId == newCharges.chargesId }

        return Resource.Success(
            if (result == null) {
                items.value.add(newCharges)
            } else {
                items.value.remove(result)
                items.value.add(newCharges)
            },
        )
    }

    override suspend fun deleteCharges(chargesIds: List<Int>): Resource<Boolean> {
        return Resource.Success(items.value.removeAll { it.chargesId in chargesIds })
    }

    override suspend fun findChargesByNameAndId(chargesName: String, chargesId: Int?): Boolean {
        return items.value.any {
            if (chargesId != null) {
                it.chargesName == chargesName && it.chargesId != chargesId
            } else {
                it.chargesName == chargesName
            }
        }
    }

    override suspend fun importChargesItemsToDatabase(charges: List<Charges>): Resource<Boolean> {
        charges.forEach { upsertCharges(it) }

        return Resource.Success(true)
    }

    @TestOnly
    fun updateChargesData(chargesList: List<Charges>) {
        items.update { chargesList.toMutableList() }
    }

    @TestOnly
    fun createTestCharges(): Charges {
        val charges = Charges(
            chargesId = 1,
            chargesName = "Test Charges",
            chargesPrice = 10,
            isApplicable = true,
        )

        items.value.add(charges)
        return charges
    }
}
