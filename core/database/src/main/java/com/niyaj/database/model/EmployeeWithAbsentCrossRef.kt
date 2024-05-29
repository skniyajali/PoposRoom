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
import com.niyaj.model.EmployeeWithAbsents

@Entity(
    primaryKeys = ["employeeId", "absentId"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = arrayOf("employeeId"),
            childColumns = arrayOf("employeeId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = AbsentEntity::class,
            parentColumns = arrayOf("absentId"),
            childColumns = arrayOf("absentId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
)
data class EmployeeWithAbsentCrossRef(
    @ColumnInfo(index = true)
    val employeeId: Int,

    @ColumnInfo(index = true)
    val absentId: Int,
)

data class EmployeeWithAbsentsDto(
    @Embedded
    val employee: EmployeeEntity,

    @Relation(
        parentColumn = "employeeId",
        entity = AbsentEntity::class,
        entityColumn = "absentId",
        associateBy = Junction(EmployeeWithAbsentCrossRef::class),
    )
    val absents: List<AbsentEntity> = emptyList(),
)

fun EmployeeWithAbsentsDto.asExternalModel(): EmployeeWithAbsents {
    return EmployeeWithAbsents(
        employee = this.employee.asExternalModel(),
        absents = this.absents.map { it.asExternalModel() },
    )
}
