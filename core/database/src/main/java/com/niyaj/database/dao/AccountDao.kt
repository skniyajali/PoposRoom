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
import com.niyaj.database.model.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT restaurantId FROM account WHERE isLoggedIn = 1 LIMIT 1")
    fun getCurrentLoggedInResId(): Flow<Int>

    @Query("SELECT * FROM account WHERE restaurantId = :resId")
    fun getAccountInfo(resId: Int): Flow<AccountEntity>

    @Upsert
    suspend fun createOrUpdateAccount(account: AccountEntity): Long

    @Query("UPDATE account SET isLoggedIn = 1 WHERE restaurantId = :resId")
    suspend fun markAsLoggedIn(resId: Int): Int

    @Query("UPDATE account SET isLoggedIn = 0 WHERE restaurantId = :resId")
    suspend fun markAsLoggedOut(resId: Int): Int

    @Query("SELECT * FROM account WHERE email = :emailOrPhone OR phone = :emailOrPhone")
    suspend fun findAccountByEmailOrPhone(emailOrPhone: String): AccountEntity?

    @Query("SELECT * FROM account WHERE restaurantId = :resId AND password = :password")
    suspend fun findAccountIdAndPassword(resId: Int, password: String): AccountEntity?

    @Query("UPDATE account SET password = :newPassword WHERE restaurantId = :resId")
    suspend fun updatePassword(resId: Int, newPassword: String): Int

    @Query("UPDATE account SET phone = :phone, email = :email WHERE restaurantId = :resId")
    suspend fun updateEmailAndPhone(resId: Int, email: String, phone: String): Int

    @Query("SELECT isLoggedIn FROM account WHERE restaurantId = :resId")
    fun checkIsLoggedIn(resId: Int): Flow<Boolean?>

    @Query("SELECT isLoggedIn FROM account WHERE restaurantId = :resId")
    fun checkUserIsLoggedIn(resId: Int): Boolean?
}
