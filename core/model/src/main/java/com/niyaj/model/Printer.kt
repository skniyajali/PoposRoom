package com.niyaj.model

import androidx.compose.runtime.Stable

@Stable
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
        private const val PRINTER_ID = "PRINTER11"
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
