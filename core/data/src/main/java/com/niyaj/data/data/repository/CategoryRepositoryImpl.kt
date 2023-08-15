package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.data.mapper.toEntity
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.data.repository.validation.CategoryValidationRepository
import com.niyaj.common.tags.CategoryConstants
import com.niyaj.database.dao.CategoryDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Category
import com.niyaj.model.searchCategory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : CategoryRepository, CategoryValidationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllCategory(searchText: String): Flow<List<Category>> {
        return withContext(ioDispatcher) {
            categoryDao.getAllCategories().mapLatest { it ->
                it.map {
                    it.asExternalModel()
                }.searchCategory(searchText)
            }
        }
    }

    override suspend fun getCategoryById(categoryId: Int): Resource<Category?> {
        return try {
            withContext(ioDispatcher) {
                Resource.Success(categoryDao.getCategoryById(categoryId)?.asExternalModel())
            }
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun addOrIgnoreCategory(newCategory: Category): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategoryName = validateCategoryName(newCategory.categoryName)

                if (validateCategoryName.successful) {
                    val result = categoryDao.insertOrIgnoreCategory(newCategory.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error(validateCategoryName.errorMessage ?: "Unable to create category")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create new category")
        }
    }

    override suspend fun updateCategory(newCategory: Category): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategoryName =
                    validateCategoryName(newCategory.categoryName, newCategory.categoryId)

                if (validateCategoryName.successful) {
                    val result = categoryDao.updateCategory(newCategory.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error(validateCategoryName.errorMessage ?: "Unable to update category")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update category")
        }
    }

    override suspend fun upsertCategory(newCategory: Category): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateCategoryName =
                    validateCategoryName(newCategory.categoryName, newCategory.categoryId)

                if (validateCategoryName.successful) {
                    val result = categoryDao.upsertCategory(newCategory.toEntity())

                    Resource.Success(result > 0)
                } else {
                    Resource.Error(validateCategoryName.errorMessage ?: "Unable")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable")
        }
    }

    override suspend fun deleteCategory(categoryId: Int): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val result = categoryDao.deleteCategory(categoryId)

                Resource.Success(result > 0)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable")
        }
    }

    override suspend fun deleteCategories(categoryIds: List<Int>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                withContext(ioDispatcher) {
                    val result = categoryDao.deleteCategories(categoryIds)

                    Resource.Success(result > 0)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete categories")
        }
    }

    override suspend fun validateCategoryName(
        categoryName: String,
        categoryId: Int?,
    ): ValidationResult {
        if (categoryName.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CategoryConstants.CATEGORY_NAME_EMPTY_ERROR
            )
        }

        if (categoryName.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = CategoryConstants.CATEGORY_NAME_LENGTH_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            categoryDao.findCategoryByName(categoryName, categoryId) != null
        }

        if (serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = CategoryConstants.CATEGORY_NAME_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}