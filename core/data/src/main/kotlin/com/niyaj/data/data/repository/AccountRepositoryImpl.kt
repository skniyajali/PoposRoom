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
import com.niyaj.data.repository.AccountRepository
import com.niyaj.database.dao.AccountDao
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Account
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AccountRepositoryImpl(
    private val accountDao: AccountDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : AccountRepository {

    override fun getCurrentLoggedInResId(): Flow<Int> {
        return accountDao.getCurrentLoggedInResId()
    }

    override suspend fun getAccountInfo(resId: Int): Flow<Account> {
        return withContext(ioDispatcher) {
            accountDao.getAccountInfo(resId).map {
                it?.toExternalModel() ?: Account.defaultAccount
            }
        }
    }

    override suspend fun updateAccountInfo(
        resId: Int,
        email: String,
        phone: String,
    ): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val result = accountDao.updateEmailAndPhone(resId, email, phone)
                Resource.Success(result > 0)
            } catch (e: Exception) {
                Resource.Error(e.message)
            }
        }
    }

    override suspend fun register(account: Account): Resource<Int> {
        return withContext(ioDispatcher) {
            try {
                val result = accountDao.createOrUpdateAccount(account.toEntity())

                Resource.Success(result.toInt())
            } catch (e: Exception) {
                Resource.Error(e.message)
            }
        }
    }

    override suspend fun login(emailOrPhone: String, password: String): Resource<Int> {
        return withContext(ioDispatcher) {
            try {
                val findUser = accountDao.findAccountByEmailOrPhone(emailOrPhone)

                if (findUser != null) {
                    if (findUser.password == password) {
                        accountDao.markAsLoggedIn(findUser.restaurantId)

                        Resource.Success(findUser.restaurantId)
                    } else {
                        Resource.Error("Password is incorrect.")
                    }
                } else {
                    Resource.Error("Could not to find any account using this email or phone number.")
                }
            } catch (e: Exception) {
                Resource.Error(e.message)
            }
        }
    }

    override suspend fun logOut(resId: Int): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val result = accountDao.markAsLoggedOut(resId)
                Resource.Success(result > 0)
            } catch (e: Exception) {
                Resource.Error(e.message)
            }
        }
    }

    override suspend fun changePassword(
        resId: Int,
        currentPassword: String,
        newPassword: String,
    ): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val findUser = accountDao.findAccountIdAndPassword(resId, currentPassword)

                if (findUser != null) {
                    val result = accountDao.updatePassword(resId, newPassword)
                    Resource.Success(result > 0)
                } else {
                    Resource.Error("Current password is incorrect.")
                }
            } catch (e: Exception) {
                Resource.Error(e.message)
            }
        }
    }

    override fun checkIsLoggedIn(resId: Int): Flow<Boolean> {
        return accountDao.checkIsLoggedIn(resId).map { it ?: false }
    }

    override suspend fun checkUserLoggedIn(resId: Int): Boolean {
        return withContext(ioDispatcher) {
            accountDao.checkUserIsLoggedIn(resId) ?: false
        }
    }
}
