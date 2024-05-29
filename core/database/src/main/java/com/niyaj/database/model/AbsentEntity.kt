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
import com.niyaj.model.Absent
import java.util.Date

@Entity(
    tableName = "absent",
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = arrayOf("employeeId"),
            childColumns = arrayOf("employeeId"),
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class AbsentEntity(
    @PrimaryKey(autoGenerate = true)
    val absentId: Int = 0,

    @ColumnInfo(index = true)
    val employeeId: Int,

    val absentReason: String = "",

    val absentDate: String,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date,

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun AbsentEntity.asExternalModel(): Absent {
    return Absent(
        absentId = this.absentId,
        employeeId = this.employeeId,
        absentReason = this.absentReason,
        absentDate = this.absentDate,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt?.time,
    )
}
