package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult

interface ProfileValidationRepository {

    fun validateName(name: String): ValidationResult

    fun validateEmail(email: String): ValidationResult

    fun validatePrimaryPhone(phone: String): ValidationResult

    fun validateSecondaryPhone(phone: String): ValidationResult

    fun validateTagline(tagline: String): ValidationResult

    fun validateDescription(description: String): ValidationResult

    fun validateAddress(address: String): ValidationResult

    fun validateLogo(logo: String): ValidationResult

}