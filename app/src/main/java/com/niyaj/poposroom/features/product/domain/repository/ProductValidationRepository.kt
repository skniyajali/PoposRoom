package com.niyaj.poposroom.features.product.domain.repository

import com.niyaj.poposroom.features.common.utils.ValidationResult

interface ProductValidationRepository {

    fun validateCategoryName(categoryId: Int): ValidationResult

    suspend fun validateProductName(productName: String, productId: Int? = null): ValidationResult

    fun validateProductPrice(productPrice: Int, type: String? = null): ValidationResult
}