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

interface PrinterValidationRepository {

    fun validatePrinterDpi(dpi: Int): ValidationResult

    fun validatePrinterWidth(width: Float): ValidationResult

    fun validatePrinterNbrLines(lines: Int): ValidationResult

    fun validateProductNameLength(length: Int): ValidationResult

    fun validateProductReportLimit(limit: Int): ValidationResult

    fun validateAddressReportLimit(limit: Int): ValidationResult

    fun validateCustomerReportLimit(limit: Int): ValidationResult
}
