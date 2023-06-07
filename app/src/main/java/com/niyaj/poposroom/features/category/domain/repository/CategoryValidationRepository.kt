package com.niyaj.poposroom.features.category.domain.repository

import com.niyaj.poposroom.features.common.utils.ValidationResult


interface CategoryValidationRepository {

    suspend fun validateCategoryName(categoryName: String, categoryId: Int? = null): ValidationResult
}