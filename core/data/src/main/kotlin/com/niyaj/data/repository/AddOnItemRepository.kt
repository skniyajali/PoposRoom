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
import com.niyaj.model.AddOnItem
import kotlinx.coroutines.flow.Flow

interface AddOnItemRepository {

    suspend fun getAllAddOnItem(searchText: String): Flow<List<AddOnItem>>

    suspend fun getAddOnItemById(itemId: Int): Resource<AddOnItem?>

    suspend fun upsertAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean>

    suspend fun deleteAddOnItems(itemIds: List<Int>): Resource<Boolean>

    suspend fun findAddOnItemByName(name: String, addOnItemId: Int?): Boolean

    suspend fun importAddOnItemsToDatabase(addOnItems: List<AddOnItem>): Resource<Boolean>
}
