package com.niyaj.poposroom.features.employee_payment.di

import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.employee_payment.data.dao.PaymentDao
import com.niyaj.poposroom.features.employee_payment.data.repository.PaymentRepositoryImpl
import com.niyaj.poposroom.features.employee_payment.domain.repository.PaymentRepository
import com.niyaj.poposroom.features.employee_payment.domain.repository.PaymentValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {

    @Provides
    fun providePaymentDao(database: PoposDatabase) : PaymentDao {
        return database.paymentDao()
    }

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