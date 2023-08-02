package com.niyaj.data.data.repository

import com.niyaj.common.result.ValidationResult
import com.niyaj.data.repository.validation.PrinterValidationRepository
import com.niyaj.data.utils.PrinterInfoTestTags.ADDRESS_REPORT_LENGTH_ERROR
import com.niyaj.data.utils.PrinterInfoTestTags.ADDRESS_REPORT_LIMIT_IS_REQUIRED
import com.niyaj.data.utils.PrinterInfoTestTags.CUSTOM_REPORT_LENGTH_ERROR
import com.niyaj.data.utils.PrinterInfoTestTags.CUSTOM_REPORT_LIMIT_IS_REQUIRED
import com.niyaj.data.utils.PrinterInfoTestTags.DPI_IS_REQUIRED
import com.niyaj.data.utils.PrinterInfoTestTags.NBR_LINES_IS_REQUIRED
import com.niyaj.data.utils.PrinterInfoTestTags.PRODUCT_NAME_LENGTH_IS_ERROR
import com.niyaj.data.utils.PrinterInfoTestTags.PRODUCT_NAME_LENGTH_IS_REQUIRED
import com.niyaj.data.utils.PrinterInfoTestTags.PRODUCT_REPORT_LENGTH_ERROR
import com.niyaj.data.utils.PrinterInfoTestTags.PRODUCT_REPORT_LIMIT_IS_REQUIRED
import com.niyaj.data.utils.PrinterInfoTestTags.WIDTH_IS_REQUIRED

class PrinterValidationRepositoryImpl : PrinterValidationRepository {

    override fun validatePrinterDpi(dpi: Int): ValidationResult {
        if (dpi <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = DPI_IS_REQUIRED
            )
        }

        return ValidationResult(true)
    }

    override fun validatePrinterWidth(width: Float): ValidationResult {
        if (width <= 0f) {
            return ValidationResult(
                successful = false,
                errorMessage = WIDTH_IS_REQUIRED
            )
        }

        return ValidationResult(true)
    }

    override fun validatePrinterNbrLines(lines: Int): ValidationResult {
        if (lines <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = NBR_LINES_IS_REQUIRED
            )
        }

        return ValidationResult(true)
    }

    override fun validateProductNameLength(length: Int): ValidationResult {
        if (length <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_LENGTH_IS_REQUIRED
            )
        }

        if (length <= 10) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_NAME_LENGTH_IS_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateProductReportLimit(limit: Int): ValidationResult {
        if (limit <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_REPORT_LIMIT_IS_REQUIRED
            )
        }

        if (limit < 20) {
            return ValidationResult(
                successful = false,
                errorMessage = PRODUCT_REPORT_LENGTH_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateAddressReportLimit(limit: Int): ValidationResult {
        if (limit <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDRESS_REPORT_LIMIT_IS_REQUIRED
            )
        }

        if (limit < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDRESS_REPORT_LENGTH_ERROR
            )
        }

        return ValidationResult(true)
    }

    override fun validateCustomerReportLimit(limit: Int): ValidationResult {
        if (limit <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOM_REPORT_LIMIT_IS_REQUIRED
            )
        }

        if (limit < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOM_REPORT_LENGTH_ERROR
            )
        }

        return ValidationResult(true)
    }
}