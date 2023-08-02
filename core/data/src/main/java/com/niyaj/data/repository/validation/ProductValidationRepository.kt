package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult

interface ProductValidationRepository {

    fun validateCategoryId(categoryId: Int): ValidationResult

    suspend fun validateProductName(productName: String, productId: Int? = null): ValidationResult

    fun validateProductPrice(productPrice: Int, type: String? = null): ValidationResult
}