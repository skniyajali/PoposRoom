package com.niyaj.poposroom.features.expenses.di

import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.expenses.data.dao.ExpenseDao
import com.niyaj.poposroom.features.expenses.data.repository.ExpenseRepositoryImpl
import com.niyaj.poposroom.features.expenses.domain.repository.ExpenseRepository
import com.niyaj.poposroom.features.expenses.domain.repository.ExpenseValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ExpenseModule {

    @Provides
    fun provideExpenseDao(database: PoposDatabase) : ExpenseDao {
        return database.expenseDao()
    }

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