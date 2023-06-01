package com.niyaj.poposroom.features.common.database.di

import android.content.Context
import androidx.room.Room
import com.niyaj.poposroom.features.common.database.PoposDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesNiaDatabase(
        @ApplicationContext context: Context,
    ): PoposDatabase = Room.databaseBuilder(
        context,
        PoposDatabase::class.java,
        "popos-database",
    ).fallbackToDestructiveMigration().build()
}