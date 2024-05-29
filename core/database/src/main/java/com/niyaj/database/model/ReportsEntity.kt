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
import com.niyaj.model.Reports

@Entity(
    tableName = "reports",
)
data class ReportsEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val reportId: Int = 0,

    val expensesQty: Long = 0,
    val expensesAmount: Long = 0,

    val dineInSalesQty: Long = 0,
    val dineInSalesAmount: Long = 0,

    val dineOutSalesQty: Long = 0,
    val dineOutSalesAmount: Long = 0,

    val reportDate: String = "",

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)

fun ReportsEntity.toExternalModel(): Reports {
    return Reports(
        reportId = this.reportId,
        expensesQty = this.expensesQty,
        expensesAmount = this.expensesAmount,
        dineInSalesQty = this.dineInSalesQty,
        dineInSalesAmount = this.dineInSalesAmount,
        dineOutSalesQty = this.dineOutSalesQty,
        dineOutSalesAmount = this.dineOutSalesAmount,
        reportDate = this.reportDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}
