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
import com.niyaj.model.Expense
import java.util.Date

@Entity(tableName = "expense")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val expenseId: Int = 0,

    val expenseName: String = "",

    val expenseAmount: String = "",

    val expenseDate: String = "",

    val expenseNote: String = "",

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun ExpenseEntity.asExternalModel(): Expense {
    return Expense(
        expenseId = this.expenseId,
        expenseName = this.expenseName,
        expenseAmount = this.expenseAmount,
        expenseDate = this.expenseDate,
        expenseNote = this.expenseNote,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt?.time,
    )
}
