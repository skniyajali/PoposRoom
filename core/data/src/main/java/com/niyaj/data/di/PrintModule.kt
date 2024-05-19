package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.PrintRepositoryImpl
import com.niyaj.data.repository.PrintRepository
import com.niyaj.database.dao.PrintDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object PrintModule {

    @Provides
    fun providePrintRepository(
        printDao: PrintDao,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher,
    ): PrintRepository {
        return PrintRepositoryImpl(printDao, ioDispatcher)
    }
}