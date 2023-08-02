package com.niyaj.data.repository.validation

import com.niyaj.common.result.ValidationResult

interface PrinterValidationRepository {

    fun validatePrinterDpi(dpi: Int): ValidationResult

    fun validatePrinterWidth(width: Float): ValidationResult

    fun validatePrinterNbrLines(lines: Int): ValidationResult

    fun validateProductNameLength(length: Int): ValidationResult

    fun validateProductReportLimit(limit: Int): ValidationResult

    fun validateAddressReportLimit(limit: Int): ValidationResult

    fun validateCustomerReportLimit(limit: Int): ValidationResult

}