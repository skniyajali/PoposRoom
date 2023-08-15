package com.niyaj.data.di

import com.niyaj.data.data.repository.DataDeletionRepositoryImpl
import com.niyaj.data.repository.DataDeletionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataDeletionModule {

    @Provides
    fun provideDataDeletionRepositoryImpl(): DataDeletionRepository {
        return DataDeletionRepositoryImpl()
    }
}