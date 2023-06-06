package com.niyaj.poposroom.features.employee_payment.domain.repository

import com.niyaj.poposroom.features.common.utils.ValidationResult
import com.niyaj.poposroom.features.employee.domain.utils.PaymentMode
import com.niyaj.poposroom.features.employee.domain.utils.PaymentType

interface PaymentValidationRepository {

    fun validateEmployee(employeeId: Int): ValidationResult

    fun validateGivenDate(givenDate: String): ValidationResult

    fun validatePaymentMode(paymentMode: PaymentMode) : ValidationResult

    fun validateGivenAmount(salary: String): ValidationResult

    fun validatePaymentNote(paymentNote: String, isRequired: Boolean = false): ValidationResult

    fun validatePaymentType(paymentType: PaymentType): ValidationResult
}