/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

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