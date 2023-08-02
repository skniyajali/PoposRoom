package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.ProfileRepositoryImpl
import com.niyaj.data.repository.ProfileRepository
import com.niyaj.data.repository.validation.ProfileValidationRepository
import com.niyaj.database.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    fun profileValidationRepository(
        profileDao: ProfileDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): ProfileValidationRepository {
        return ProfileRepositoryImpl(profileDao, ioDispatcher)
    }

    @Provides
    fun provideProfileRepository(
        profileDao: ProfileDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): ProfileRepository {
        return ProfileRepositoryImpl(profileDao, ioDispatcher)
    }

}