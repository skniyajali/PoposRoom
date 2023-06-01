package com.niyaj.poposroom.features.category.domain.validation

import com.niyaj.poposroom.features.common.utils.ValidationResult


interface CategoryValidationRepository {

    suspend fun validateCategoryName(categoryId: Int? = null, categoryName: String): ValidationResult
}