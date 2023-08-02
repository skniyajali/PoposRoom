package com.niyaj.data.mapper

import com.niyaj.database.model.PrinterEntity
import com.niyaj.model.Printer

fun Printer.toEntity(): PrinterEntity {
    return PrinterEntity(
        printerId = this.printerId,
        printerDpi = this.printerDpi,
        printerWidth = this.printerWidth,
        printerNbrLines = this.printerNbrLines,
        productNameLength = this.productNameLength,
        productWiseReportLimit = this.productWiseReportLimit,
        addressWiseReportLimit = this.addressWiseReportLimit,
        customerWiseReportLimit = this.customerWiseReportLimit,
        printQRCode = this.printQRCode,
        printResLogo = this.printResLogo,
        printWelcomeText = this.printWelcomeText,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}