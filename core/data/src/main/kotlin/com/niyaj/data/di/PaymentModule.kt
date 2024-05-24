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

package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.PaymentRepositoryImpl
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.data.repository.validation.PaymentValidationRepository
import com.niyaj.database.dao.PaymentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {

    @Provides
    fun providePaymentValidationRepository(
        paymentDao: PaymentDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): PaymentValidationRepository {
        return PaymentRepositoryImpl(paymentDao, ioDispatcher)
    }

    @Provides
    fun providePaymentRepository(
        paymentDao: PaymentDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): PaymentRepository {
        return PaymentRepositoryImpl(paymentDao, ioDispatcher)
    }
}