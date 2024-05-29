/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult

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
    suspend fun validateCustomerPhone(customerPhone: String, customerId: Int? = null): ValidationResult
}
