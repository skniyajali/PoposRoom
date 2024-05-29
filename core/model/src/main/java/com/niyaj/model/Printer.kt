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

package com.niyaj.model

data class Printer(
    val printerId: String,

    val printerDpi: Int,

    val printerWidth: Float,

    val printerNbrLines: Int,

    val productNameLength: Int,

    val productWiseReportLimit: Int,

    val addressWiseReportLimit: Int,

    val customerWiseReportLimit: Int,

    val printQRCode: Boolean,

    val printResLogo: Boolean,

    val printWelcomeText: Boolean,

    val createdAt: String,

    val updatedAt: String? = null,
) {
    companion object {
        const val PRINTER_ID = "PRINTER11"
        private const val PRINTER_DPI = 176
        private const val PRINTER_WIDTH_MM = 58f
        private const val PRINTER_NBR_LINE = 31
        private const val PRODUCT_NAME_LENGTH = 18
        private const val PRINT_PRODUCT_WISE_REPORT_LIMIT = 30
        private const val PRINT_ADDRESS_WISE_REPORT_LIMIT = 15
        private const val PRINT_CUSTOMER_WISE_REPORT_LIMIT = 15

        val defaultPrinterInfo = Printer(
            printerId = PRINTER_ID,
            printerDpi = PRINTER_DPI,
            printerWidth = PRINTER_WIDTH_MM,
            printerNbrLines = PRINTER_NBR_LINE,
            productNameLength = PRODUCT_NAME_LENGTH,
            productWiseReportLimit = PRINT_PRODUCT_WISE_REPORT_LIMIT,
            addressWiseReportLimit = PRINT_ADDRESS_WISE_REPORT_LIMIT,
            customerWiseReportLimit = PRINT_CUSTOMER_WISE_REPORT_LIMIT,
            printQRCode = true,
            printResLogo = true,
            printWelcomeText = true,
            createdAt = System.currentTimeMillis().toString(),
            updatedAt = null,
        )
    }
}
