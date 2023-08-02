package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult


interface CategoryValidationRepository {

    suspend fun validateCategoryName(categoryName: String, categoryId: Int? = null): ValidationResult
}