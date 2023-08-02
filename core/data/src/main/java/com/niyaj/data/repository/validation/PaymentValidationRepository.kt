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