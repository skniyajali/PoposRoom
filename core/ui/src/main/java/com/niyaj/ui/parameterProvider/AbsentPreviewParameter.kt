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

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.Absent
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
import com.niyaj.model.EmployeeWithAbsents
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AbsentPreviewData.employeesWithAbsents

class AbsentPreviewParameter : PreviewParameterProvider<UiState<List<EmployeeWithAbsents>>> {
    override val values: Sequence<UiState<List<EmployeeWithAbsents>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(employeesWithAbsents),
        )
}

object AbsentPreviewData {

    val employeesWithAbsents = listOf(
        EmployeeWithAbsents(
            employee = Employee(
                employeeId = 1,
                employeeName = "John Doe",
                employeePhone = "1234567890",
                employeeSalary = "5000",
                employeePosition = "Software Engineer",
                employeeJoinedDate = "1673308800000",
                employeeEmail = "john.doe@company.com",
                employeeSalaryType = EmployeeSalaryType.Monthly,
                employeeType = EmployeeType.FullTime,
                isDeliveryPartner = false,
                partnerQRCode = null,
                createdAt = 1673308800000,
                updatedAt = null,
            ),
            absents = listOf(
                Absent(
                    absentId = 1,
                    employeeId = 1,
                    absentReason = "Sick",
                    absentDate = "1674718800000",
                    createdAt = 1674718800000,
                    updatedAt = null,
                ),
                Absent(
                    absentId = 2,
                    employeeId = 1,
                    absentReason = "Personal",
                    absentDate = "1675323600000",
                    createdAt = 1675323600000,
                    updatedAt = null,
                ),
                Absent(
                    absentId = 11,
                    employeeId = 1,
                    absentReason = "Vacation",
                    absentDate = "1676592000000",
                    createdAt = 1676592000000,
                    updatedAt = null,
                ),
            ),
        ),
        EmployeeWithAbsents(
            employee = Employee(
                employeeId = 2,
                employeeName = "Jane Smith",
                employeePhone = "9876543210",
                employeeSalary = "4500",
                employeePosition = "Marketing Manager",
                employeeJoinedDate = "1675987200000",
                employeeEmail = "jane.smith@company.com",
                employeeSalaryType = EmployeeSalaryType.Monthly,
                employeeType = EmployeeType.FullTime,
                isDeliveryPartner = false,
                partnerQRCode = null,
                createdAt = 1675987200000,
                updatedAt = null,
            ),
            absents = listOf(
                Absent(
                    absentId = 3,
                    employeeId = 2,
                    absentReason = "Vacation",
                    absentDate = "1676672400000",
                    createdAt = 1676672400000,
                    updatedAt = null,
                ),
                Absent(
                    absentId = 4,
                    employeeId = 2,
                    absentReason = "Sick",
                    absentDate = "1678080000000",
                    createdAt = 1678080000000,
                    updatedAt = null,
                ),
                Absent(
                    absentId = 12,
                    employeeId = 2,
                    absentReason = "Personal",
                    absentDate = "1679186400000",
                    createdAt = 1679186400000,
                    updatedAt = null,
                ),
            ),
        ),
        EmployeeWithAbsents(
            employee = Employee(
                employeeId = 3,
                employeeName = "Michael Johnson",
                employeePhone = "5555555555",
                employeeSalary = "3000",
                employeePosition = "Sales Representative",
                employeeJoinedDate = "1678665600000",
                employeeEmail = "michael.johnson@company.com",
                employeeSalaryType = EmployeeSalaryType.Weekly,
                employeeType = EmployeeType.PartTime,
                isDeliveryPartner = true,
                partnerQRCode = "QR1234",
                createdAt = 1678665600000,
                updatedAt = null,
            ),
            absents = listOf(
                Absent(
                    absentId = 5,
                    employeeId = 3,
                    absentReason = "Personal",
                    absentDate = "1679350800000",
                    createdAt = 1679350800000,
                    updatedAt = null,
                ),
                Absent(
                    absentId = 6,
                    employeeId = 3,
                    absentReason = "Sick",
                    absentDate = "1680758400000",
                    createdAt = 1680758400000,
                    updatedAt = null,
                ),
                Absent(
                    absentId = 13,
                    employeeId = 3,
                    absentReason = "Vacation",
                    absentDate = "1681781200000",
                    createdAt = 1681781200000,
                    updatedAt = null,
                ),
            ),
        ),
        EmployeeWithAbsents(
            employee = Employee(
                employeeId = 4,
                employeeName = "Emily Wilson",
                employeePhone = "1112223333",
                employeeSalary = "6000",
                employeePosition = "Finance Manager",
                employeeJoinedDate = "1681257600000",
                employeeEmail = "emily.wilson@company.com",
                employeeSalaryType = EmployeeSalaryType.Monthly,
                employeeType = EmployeeType.FullTime,
                isDeliveryPartner = false,
                partnerQRCode = null,
                createdAt = 1681257600000,
                updatedAt = null,
            ),
            absents = listOf(
                Absent(
                    absentId = 7,
                    employeeId = 4,
                    absentReason = "Vacation",
                    absentDate = "1681944000000",
                    createdAt = 1681944000000,
                    updatedAt = null,
                ),
                Absent(
                    absentId = 8,
                    employeeId = 4,
                    absentReason = "Personal",
                    absentDate = "1683354000000",
                    createdAt = 1683354000000,
                    updatedAt = null,
                ),
            ),
        ),
        EmployeeWithAbsents(
            employee = Employee(
                employeeId = 5,
                employeeName = "David Brown",
                employeePhone = "4445556666",
                employeeSalary = "2500",
                employeePosition = "Customer Service Representative",
                employeeJoinedDate = "1683849600000",
                employeeEmail = "david.brown@company.com",
                employeeSalaryType = EmployeeSalaryType.Daily,
                employeeType = EmployeeType.PartTime,
                isDeliveryPartner = true,
                partnerQRCode = "QR5678",
                createdAt = 1683849600000,
                updatedAt = null,
            ),
            absents = listOf(
                Absent(
                    absentId = 9,
                    employeeId = 5,
                    absentReason = "Sick",
                    absentDate = "1684537200000",
                    createdAt = 1684537200000,
                    updatedAt = null,
                ),
                Absent(
                    absentId = 10,
                    employeeId = 5,
                    absentReason = "Personal",
                    absentDate = "1685949600000",
                    createdAt = 1685949600000,
                    updatedAt = null,
                ),
            ),
        ),
    )
}
