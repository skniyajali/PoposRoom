package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.ExpenseRepositoryImpl
import com.niyaj.data.repository.ExpenseRepository
import com.niyaj.data.repository.ExpenseValidationRepository
import com.niyaj.database.dao.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ExpenseModule {

    @Provides
    fun provideExpenseValidationRepository(
        expenseDao: ExpenseDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): ExpenseValidationRepository {
        return ExpenseRepositoryImpl(expenseDao, ioDispatcher)
    }

    @Provides
    fun provideExpenseRepository(
        expenseDao: ExpenseDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): ExpenseRepository {
        return ExpenseRepositoryImpl(expenseDao, ioDispatcher)
    }
}