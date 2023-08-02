package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.common.utils.Constants.PRINTER_DPI
import com.niyaj.common.utils.Constants.PRINTER_ID
import com.niyaj.common.utils.Constants.PRINTER_NBR_LINE
import com.niyaj.common.utils.Constants.PRINTER_WIDTH_MM
import com.niyaj.common.utils.Constants.PRINT_ADDRESS_WISE_REPORT_LIMIT
import com.niyaj.common.utils.Constants.PRINT_CUSTOMER_WISE_REPORT_LIMIT
import com.niyaj.common.utils.Constants.PRINT_PRODUCT_WISE_REPORT_LIMIT
import com.niyaj.common.utils.Constants.PRODUCT_NAME_LENGTH
import com.niyaj.model.Printer

@Entity(
    tableName = "printerInfo"
)
data class PrinterEntity(
    @PrimaryKey
    @ColumnInfo(index = true)
    val printerId: String = PRINTER_ID,

    val printerDpi: Int = PRINTER_DPI,

    val printerWidth: Float = PRINTER_WIDTH_MM,

    val printerNbrLines: Int = PRINTER_NBR_LINE,

    val productNameLength: Int = PRODUCT_NAME_LENGTH,

    val productWiseReportLimit: Int = PRINT_PRODUCT_WISE_REPORT_LIMIT,

    val addressWiseReportLimit: Int = PRINT_ADDRESS_WISE_REPORT_LIMIT,

    val customerWiseReportLimit: Int = PRINT_CUSTOMER_WISE_REPORT_LIMIT,

    val printQRCode: Boolean = true,

    val printResLogo: Boolean = true,

    val printWelcomeText: Boolean = true,

    val createdAt: String = System.currentTimeMillis().toString(),

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