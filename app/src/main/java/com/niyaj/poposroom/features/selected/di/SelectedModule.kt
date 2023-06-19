package com.niyaj.poposroom.features.selected.di

import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.selected.data.dao.SelectedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SelectedModule {

    @Provides
    fun provideSelectedDao(database: PoposDatabase) : SelectedDao {
        return database.selectedDao()
    }
}