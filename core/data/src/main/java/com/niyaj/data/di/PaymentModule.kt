package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.PaymentRepositoryImpl
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.data.repository.validation.PaymentValidationRepository
import com.niyaj.poposroom.features.employee_payment.data.dao.PaymentDao
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