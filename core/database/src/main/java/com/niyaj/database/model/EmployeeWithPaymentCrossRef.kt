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
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Junction
import androidx.room.Relation
import com.niyaj.model.EmployeeWithPayments

@Entity(
    primaryKeys = ["employeeId", "paymentId"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = arrayOf("employeeId"),
            childColumns = arrayOf("employeeId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = PaymentEntity::class,
            parentColumns = arrayOf("paymentId"),
            childColumns = arrayOf("paymentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
)
data class EmployeeWithPaymentCrossRef(
    @ColumnInfo(index = true)
    val employeeId: Int,

    @ColumnInfo(index = true)
    val paymentId: Int,
)

data class EmployeeWithPaymentsDto(
    @Embedded
    val employee: EmployeeEntity,

    @Relation(
        parentColumn = "employeeId",
        entity = PaymentEntity::class,
        entityColumn = "paymentId",
        associateBy = Junction(EmployeeWithPaymentCrossRef::class),
    )
    val payments: List<PaymentEntity> = emptyList(),
)

fun EmployeeWithPaymentsDto.asExternalModel(): EmployeeWithPayments {
    return EmployeeWithPayments(
        employee = this.employee.asExternalModel(),
        payments = this.payments.map { it.asExternalModel() },
    )
}
