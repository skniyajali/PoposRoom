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

package com.niyaj.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.niyaj.database.model.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Query(
        value = """
        SELECT * FROM profile WHERE restaurantId = :restaurantId
    """,
    )
    fun getProfileInfo(restaurantId: Int): Flow<ProfileEntity?>

    @Query(
        value = """
        SELECT * FROM profile WHERE restaurantId = :restaurantId
    """,
    )
    fun getProfileById(restaurantId: Int): ProfileEntity?

    @Query(
        value = """
            UPDATE profile SET logo = :resLogo WHERE restaurantId = :restaurantId
        """,
    )
    suspend fun updateProfileLogo(restaurantId: Int, resLogo: String): Int

    @Query(
        value = """
            UPDATE profile SET printLogo = :printLogo WHERE restaurantId = :restaurantId
        """,
    )
    suspend fun updatePrintLogo(restaurantId: Int, printLogo: String): Int

    @Upsert
    suspend fun insertOrUpdateProfile(profile: ProfileEntity): Long
}
