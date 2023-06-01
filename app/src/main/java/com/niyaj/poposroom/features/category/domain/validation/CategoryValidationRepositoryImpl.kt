package com.niyaj.poposroom.features.category.domain.validation

import com.niyaj.poposroom.features.category.dao.CategoryDao
import com.niyaj.poposroom.features.category.domain.utils.CategoryConstants.CATEGORY_NAME_ALREADY_EXIST_ERROR
import com.niyaj.poposroom.features.category.domain.utils.CategoryConstants.CATEGORY_NAME_EMPTY_ERROR
import com.niyaj.poposroom.features.category.domain.utils.CategoryConstants.CATEGORY_NAME_LENGTH_ERROR
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.ValidationResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryValidationRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher

) : CategoryValidationRepository {
    override suspend fun validateCategoryName(categoryId: Int?, categoryName: String): ValidationResult {
        if(categoryName.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = CATEGORY_NAME_EMPTY_ERROR
            )
        }

        if(categoryName.length < 3) {
            return ValidationResult(
                successful = false,
                errorMessage = CATEGORY_NAME_LENGTH_ERROR
            )
        }

        val serverResult = withContext(ioDispatcher) {
            categoryDao.findCategoryByName(categoryId, categoryName) != null
        }

        if(serverResult) {
            return ValidationResult(
                successful = false,
                errorMessage = CATEGORY_NAME_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}