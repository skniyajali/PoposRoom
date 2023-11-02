package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.MeasureUnitRepositoryImpl
import com.niyaj.data.repository.MeasureUnitRepository
import com.niyaj.data.repository.validation.MeasureUnitValidationRepository
import com.niyaj.database.dao.MeasureUnitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object MeasureUnitModule {


    @Provides
    fun provideMeasureUnitValidationRepository(
        measureUnitDao: MeasureUnitDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): MeasureUnitValidationRepository {
        return MeasureUnitRepositoryImpl(measureUnitDao, ioDispatcher)
    }

    @Provides
    fun provideMeasureUnitRepository(
        measureUnitDao: MeasureUnitDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): MeasureUnitRepository {
        return MeasureUnitRepositoryImpl(measureUnitDao, ioDispatcher)
    }
}