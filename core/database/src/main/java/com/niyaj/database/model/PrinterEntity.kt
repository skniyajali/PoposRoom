package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.Printer

@Entity(
    tableName = "printerInfo"
)
data class PrinterEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
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
)

fun PrinterEntity.toExternalModel(): Printer {
    return Printer(
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