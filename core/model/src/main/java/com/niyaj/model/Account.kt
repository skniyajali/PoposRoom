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

package com.niyaj.model

data class Account(
    val restaurantId: Int,

    val email: String,

    val phone: String,

    val password: String,

    val isLoggedIn: Boolean,

    val createdAt: Long,

    val updatedAt: Long? = null,
) {

    companion object {
        private const val DEFAULT_RES_PASSWORD = "Popos@123"
        private val profile = Profile.defaultProfileInfo

        val defaultAccount: Account = Account(
            restaurantId = profile.restaurantId,
            email = profile.email,
            phone = profile.primaryPhone,
            password = DEFAULT_RES_PASSWORD,
            isLoggedIn = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = null,
        )
    }
}
