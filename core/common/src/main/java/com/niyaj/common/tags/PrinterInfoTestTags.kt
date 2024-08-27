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

package com.niyaj.common.tags

object PrinterInfoTestTags {

    const val PRINTER_SCREEN_TITLE = "Printer Information"
    const val PRINTER_NOT_AVAILABLE = "Printer Info Not Available"

    const val UPDATE_PRINTER_BUTTON = "Update Printer Button"
    const val UPDATE_PRINTER_INFO = "Update Printer Info"

    const val PRINTER_INFO_NOTES_ONE = "First paired bluetooth printer will be connected automatically."
    const val PRINTER_INFO_NOTES_TWO = "Do not worry about connection status," +
        " click on test print button to print test data."
    const val PRINTER_INFO_NOTES_THREE = "If you did not uploaded the print logo then" +
        " default logo will be used for printing."
    const val PRINTER_INFO_NOTES_FOUR = "Print some order bill and check how it's look," +
        " if it's look weird then change printer info accordingly."

    const val PRINTER_DPI_FIELD = "Printer DPI"
    const val PRINTER_DPI_MESSAGE = "Usually found in the back side of the printer"

    const val PRINTER_WIDTH_FIELD = "Printer Width"
    const val PRINTER_WIDTH_MESSAGE = "The printer width in millimeters(mm)"

    const val PRINTER_NBR_LINES_FIELD = "Printer NBR Lines"
    const val PRINTER_NBR_LINES_MESSAGE = "How many characters should be printed in one line"

    const val PRINTER_PRODUCT_NAME_LENGTH_FIELD = "Product Name Length"
    const val PRINTER_PRODUCT_NAME_LENGTH_MESSAGE = "Product name should be printed in one line"

    const val PRODUCT_REPORT_LIMIT_FIELD = "Product Report Limit"
    const val PRODUCT_REPORT_LIMIT_MESSAGE = "How many products should be printed in a report"

    const val ADDRESS_REPORT_LIMIT_FIELD = "Address Report Limit"
    const val ADDRESS_REPORT_LIMIT_MESSAGE = "How many addresses should be printed in a report"

    const val CUSTOMER_REPORT_LIMIT_FIELD = "Customer Report Limit"
    const val CUSTOMER_REPORT_LIMIT_MESSAGE = "How many customers should be printed in a report"

    const val PRINT_QR_CODE_IN_BILL = "Print QR Code In Bill"

    const val PRINT_LOGO_IN_BILL = "Print Logo In Bill"

    const val PRINT_WELCOME_TEXT_IN_BILL = "Print Welcome Text In Bill"

    const val DPI_IS_REQUIRED = "Printer dpi is required."

    const val WIDTH_IS_REQUIRED = "Printer width is required."

    const val NBR_LINES_IS_REQUIRED = "Printer NBR line length is required."

    const val PRODUCT_NAME_LENGTH_IS_REQUIRED = "Product name length is required."
    const val PRODUCT_NAME_LENGTH_IS_ERROR = "Product name length must be more than ten character."

    const val PRODUCT_REPORT_LIMIT_IS_REQUIRED = "Product report limit is required."
    const val PRODUCT_REPORT_LENGTH_ERROR = "Product name limit must be more than 20."

    const val ADDRESS_REPORT_LIMIT_IS_REQUIRED = "Address report limit is required."
    const val ADDRESS_REPORT_LENGTH_ERROR = "Address name limit must be more than 10."

    const val CUSTOM_REPORT_LIMIT_IS_REQUIRED = "Customer report limit is required."
    const val CUSTOM_REPORT_LENGTH_ERROR = "Customer name limit must be more than 10."
}
