/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType

interface PaymentValidationRepository {

    fun validateEmployee(employeeId: Int): ValidationResult

    fun validateGivenDate(givenDate: String): ValidationResult

    fun validatePaymentMode(paymentMode: PaymentMode) : ValidationResult

    fun validateGivenAmount(salary: String): ValidationResult

    fun validatePaymentNote(paymentNote: String, isRequired: Boolean = false): ValidationResult

    fun validatePaymentType(paymentType: PaymentType): ValidationResult
}