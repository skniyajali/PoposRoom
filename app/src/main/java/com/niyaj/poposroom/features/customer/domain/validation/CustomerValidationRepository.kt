package com.niyaj.poposroom.features.customer.domain.validation

import com.niyaj.poposroom.features.common.utils.ValidationResult

/**
 * Interface for validating customer data before saving to database.
 */
interface CustomerValidationRepository {

    /**
     * Validate customer name.
     * @param customerName : name of customer
     * @return [ValidationResult] object.
     * @see ValidationResult
     */
    fun validateCustomerName(customerName: String? = null): ValidationResult

    /**
     * Validate customer email.
     * @param customerEmail : email of customer
     * @return [ValidationResult] object.
     * @see ValidationResult
     */
    fun validateCustomerEmail(customerEmail: String? = null): ValidationResult

    /**
     * Validate customer phone.
     * @param customerPhone : phone of customer
     * @param customerId : id of customer
     * @return [ValidationResult] object.
     * @see ValidationResult
     */
    suspend fun validateCustomerPhone(customerId: Int? = null, customerPhone: String): ValidationResult
}