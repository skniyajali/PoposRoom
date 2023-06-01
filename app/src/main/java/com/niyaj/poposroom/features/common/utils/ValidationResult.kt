package com.niyaj.poposroom.features.common.utils

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)