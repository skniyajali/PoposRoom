/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.Account

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey(autoGenerate = false)
    val restaurantId: Int,

    val email: String,

    val phone: String,

    val password: String,

    val isLoggedIn: Boolean,

    val createdAt: Long,

    val updatedAt: Long? = null,
)

fun AccountEntity.toExternalModel() = Account(
    restaurantId = restaurantId,
    email = email,
    phone = phone,
    password = password,
    isLoggedIn = isLoggedIn,
    createdAt = createdAt,
    updatedAt = updatedAt,
)