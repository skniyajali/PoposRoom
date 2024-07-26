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

package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.Charges
import kotlinx.coroutines.flow.Flow

interface ChargesRepository {

    suspend fun getAllCharges(searchText: String): Flow<List<Charges>>

    suspend fun getChargesById(chargesId: Int): Resource<Charges?>

    suspend fun upsertCharges(newCharges: Charges): Resource<Boolean>

    suspend fun deleteCharges(chargesIds: List<Int>): Resource<Boolean>

    suspend fun findChargesByNameAndId(chargesName: String, chargesId: Int? = null): Boolean

    suspend fun importChargesItemsToDatabase(charges: List<Charges>): Resource<Boolean>
}
