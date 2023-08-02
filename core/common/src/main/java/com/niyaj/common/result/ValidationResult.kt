package com.niyaj.common.result

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String? = null
)