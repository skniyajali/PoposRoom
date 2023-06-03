package com.niyaj.poposroom.features.category.di

import com.niyaj.poposroom.features.category.dao.CategoryDao
import com.niyaj.poposroom.features.category.domain.use_cases.GetAllCategories
import com.niyaj.poposroom.features.category.domain.validation.CategoryValidationRepository
import com.niyaj.poposroom.features.category.domain.validation.CategoryValidationRepositoryImpl
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
        addOnItemDao: CategoryDao,
        @Dispatcher(PoposDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ): CategoryValidationRepository {
        return CategoryValidationRepositoryImpl(addOnItemDao, ioDispatcher)
    }

    @Provides
    fun getAllCategories(categoryDao: CategoryDao): GetAllCategories {
        return GetAllCategories(categoryDao)
    }
}