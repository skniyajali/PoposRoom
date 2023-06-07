package com.niyaj.poposroom.features.category.di

import com.niyaj.poposroom.features.category.data.dao.CategoryDao
import com.niyaj.poposroom.features.category.data.repository.CategoryRepositoryImpl
import com.niyaj.poposroom.features.category.domain.repository.CategoryRepository
import com.niyaj.poposroom.features.category.domain.repository.CategoryValidationRepository
import com.niyaj.poposroom.features.common.database.PoposDatabase
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {

    @Provides
    fun provideCategoryDao(database: PoposDatabase) : CategoryDao {
        return database.categoryDao()
    }

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