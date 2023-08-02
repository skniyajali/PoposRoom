package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.PrinterRepositoryImpl
import com.niyaj.data.data.repository.PrinterValidationRepositoryImpl
import com.niyaj.data.repository.PrinterRepository
import com.niyaj.data.repository.validation.PrinterValidationRepository
import com.niyaj.database.dao.PrinterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrinterModule {

    @Provides
    fun providePrinterValidationRepository(): PrinterValidationRepository {
        return PrinterValidationRepositoryImpl()
    }

    @Provides
    @Singleton
    fun providePrinterRepository(
        printerDao: PrinterDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): PrinterRepository {
        return PrinterRepositoryImpl(printerDao, ioDispatcher)
    }
}