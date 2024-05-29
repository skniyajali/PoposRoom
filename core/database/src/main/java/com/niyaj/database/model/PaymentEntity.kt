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
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import java.util.Date

@Entity(
    tableName = "payment",
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = arrayOf("employeeId"),
            childColumns = arrayOf("employeeId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val paymentId: Int = 0,

    @ColumnInfo(index = true)
    val employeeId: Int,

    val paymentAmount: String = "",

    val paymentDate: String = "",

    val paymentType: PaymentType = PaymentType.Advanced,

    val paymentMode: PaymentMode = PaymentMode.Cash,

    val paymentNote: String = "",

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun PaymentEntity.asExternalModel(): Payment {
    return Payment(
        paymentId = this.paymentId,
        employeeId = this.employeeId,
        paymentAmount = this.paymentAmount,
        paymentDate = this.paymentDate,
        paymentType = this.paymentType,
        paymentMode = this.paymentMode,
        paymentNote = this.paymentNote,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt?.time,
    )
}
