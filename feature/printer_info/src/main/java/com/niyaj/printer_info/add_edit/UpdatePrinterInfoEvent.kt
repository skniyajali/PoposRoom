package com.niyaj.printer_info.add_edit

sealed interface UpdatePrinterInfoEvent {

    data class PrinterDpiChanged(val printerDpi : String) : UpdatePrinterInfoEvent
    data class PrinterWidthChanged(val printerWidth : String) : UpdatePrinterInfoEvent
    data class PrinterNbrLinesChanged(val printerNbrLines : String) : UpdatePrinterInfoEvent
    data class ProductNameLengthChanged(val length : String) : UpdatePrinterInfoEvent

    data class ProductReportLimitChanged(val limit : String) : UpdatePrinterInfoEvent
    data class AddressReportLimitChanged(val limit : String) : UpdatePrinterInfoEvent
    data class CustomerReportLimitChanged(val limit : String) : UpdatePrinterInfoEvent

    data object PrintQrCodeChanged : UpdatePrinterInfoEvent
    data object PrintResLogoChanged : UpdatePrinterInfoEvent
    data object PrintWelcomeTextChanged : UpdatePrinterInfoEvent

    data object UpdatePrinterInfo : UpdatePrinterInfoEvent
}