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
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
import java.util.Date

@Entity(tableName = "employee")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val employeeId: Int = 0,

    val employeeName: String,

    val employeePhone: String,

    val employeeSalary: String,

    val employeePosition: String,

    val employeeJoinedDate: String,

    val employeeEmail: String? = null,

    val employeeSalaryType: EmployeeSalaryType,

    val employeeType: EmployeeType,

    val isDeliveryPartner: Boolean,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)

fun EmployeeEntity.asExternalModel(): Employee {
    return Employee(
        employeeId = this.employeeId,
        employeeName = this.employeeName,
        employeePhone = this.employeePhone,
        employeeSalary = this.employeeSalary,
        employeePosition = this.employeePosition,
        employeeJoinedDate = this.employeeJoinedDate,
        employeeEmail = this.employeeEmail,
        employeeSalaryType = this.employeeSalaryType,
        employeeType = this.employeeType,
        isDeliveryPartner = this.isDeliveryPartner,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt?.time,
    )
}
