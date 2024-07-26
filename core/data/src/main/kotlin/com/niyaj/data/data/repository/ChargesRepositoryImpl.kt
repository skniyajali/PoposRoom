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
import com.niyaj.data.repository.ChargesRepository
import com.niyaj.database.dao.ChargesDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Charges
import com.niyaj.model.searchCharges
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChargesRepositoryImpl @Inject constructor(
    private val chargesDao: ChargesDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : ChargesRepository {

    override suspend fun getAllCharges(searchText: String): Flow<List<Charges>> {
        return withContext(ioDispatcher) {
            chargesDao.getAllCharges().mapLatest { it ->
                it.map {
                    it.asExternalModel()
                }.searchCharges(searchText)
            }
        }
    }

    override suspend fun getChargesById(chargesId: Int): Resource<Charges?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(chargesDao.getChargesById(chargesId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun upsertCharges(newCharges: Charges): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = chargesDao.upsertCharges(newCharges.toEntity())

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error creating Charges Item")
        }
    }

    override suspend fun deleteCharges(chargesIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = chargesDao.deleteCharges(chargesIds)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error deleting Charges Item")
        }
    }

    override suspend fun findChargesByNameAndId(chargesName: String, chargesId: Int?): Boolean {
        return withContext(ioDispatcher) {
            chargesDao.findChargesByName(chargesName, chargesId) != null
        }
    }

    override suspend fun importChargesItemsToDatabase(charges: List<Charges>): Resource<Boolean> {
        try {
            charges.forEach { newCharges ->
                withContext(ioDispatcher) {
                    chargesDao.upsertCharges(newCharges.toEntity())
                }
            }
            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error(e.message)
        }
    }
}
