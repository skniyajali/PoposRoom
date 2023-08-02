package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.CategoryRepositoryImpl
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.data.repository.validation.CategoryValidationRepository
import com.niyaj.database.dao.CategoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {

    @Provides
    fun provideCategoryValidationRepository(
        categoryDao: CategoryDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CategoryValidationRepository {
        return CategoryRepositoryImpl(categoryDao, ioDispatcher)
    }

    @Provides
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao, ioDispatcher)
    }
}