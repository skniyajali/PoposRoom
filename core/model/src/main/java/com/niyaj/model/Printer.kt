package com.niyaj.model

data class Printer(
    val printerId: String = "",

    val printerDpi: Int = 0,

    val printerWidth: Float = 0f,

    val printerNbrLines: Int = 0,

    val productNameLength: Int = 0,

    val productWiseReportLimit: Int = 0,

    val addressWiseReportLimit: Int = 0,

    val customerWiseReportLimit: Int = 0,

    val printQRCode: Boolean = true,

    val printResLogo: Boolean = true,

    val printWelcomeText: Boolean = true,

    val createdAt: String = "",

    val updatedAt: String? = null,
)
