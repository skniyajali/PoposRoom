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

package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.Printer

@Entity(
    tableName = "printerInfo",
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
        updatedAt = this.updatedAt,
    )
}
