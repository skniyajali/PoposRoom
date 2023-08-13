package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.ReportsRepositoryImpl
import com.niyaj.data.repository.ReportsRepository
import com.niyaj.database.dao.ReportsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ReportsModule {

    @Provides
    fun provideReportsRepositoryImpl(
        reportsDao: ReportsDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): ReportsRepository {
        return ReportsRepositoryImpl(reportsDao, ioDispatcher)
    }

}