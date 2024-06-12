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

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import com.niyaj.ui.event.UiState


class PaymentPreviewParameter : PreviewParameterProvider<UiState<List<EmployeeWithPayments>>> {
    override val values: Sequence<UiState<List<EmployeeWithPayments>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(PaymentPreviewData.employeesWithPayments)
        )
}

object PaymentPreviewData {

    val payments = listOf(
        Payment(
            paymentId = 1,
            employeeId = 1,
            paymentAmount = "2000",
            paymentDate = "1673994000000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Cash,
            paymentNote = "Advance payment",
            createdAt = 1673994000000,
            updatedAt = null
        ),
        Payment(
            paymentId = 2,
            employeeId = 1,
            paymentAmount = "2500",
            paymentDate = "1675404000000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Online,
            paymentNote = "Advance payment",
            createdAt = 1675404000000,
            updatedAt = null
        ),
        Payment(
            paymentId = 3,
            employeeId = 2,
            paymentAmount = "1800",
            paymentDate = "1676672400000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Cash,
            paymentNote = "Advance payment",
            createdAt = 1676672400000,
            updatedAt = null
        ),
        Payment(
            paymentId = 4,
            employeeId = 2,
            paymentAmount = "2200",
            paymentDate = "1678080000000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Online,
            paymentNote = "Advance payment",
            createdAt = 1678080000000,
            updatedAt = null
        ),
        Payment(
            paymentId = 5,
            employeeId = 3,
            paymentAmount = "3000",
            paymentDate = "1679350800000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Cash,
            paymentNote = "Advance payment",
            createdAt = 1679350800000,
            updatedAt = null
        ),
        Payment(
            paymentId = 6,
            employeeId = 3,
            paymentAmount = "2500",
            paymentDate = "1680758400000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Online,
            paymentNote = "Advance payment",
            createdAt = 1680758400000,
            updatedAt = null
        ),
        Payment(
            paymentId = 7,
            employeeId = 4,
            paymentAmount = "4000",
            paymentDate = "1681944000000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Cash,
            paymentNote = "Advance payment",
            createdAt = 1681944000000,
            updatedAt = null
        ),
        Payment(
            paymentId = 8,
            employeeId = 4,
            paymentAmount = "3500",
            paymentDate = "1683354000000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Online,
            paymentNote = "Advance payment",
            createdAt = 1683354000000,
            updatedAt = null
        ),
        Payment(
            paymentId = 9,
            employeeId = 5,
            paymentAmount = "2000",
            paymentDate = "1684537200000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Cash,
            paymentNote = "Advance payment",
            createdAt = 1684537200000,
            updatedAt = null
        ),
        Payment(
            paymentId = 10,
            employeeId = 5,
            paymentAmount = "2500",
            paymentDate = "1685949600000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Online,
            paymentNote = "Advance payment",
            createdAt = 1685949600000,
            updatedAt = null
        )
    )

    val employeesWithPayments = listOf(
        EmployeeWithPayments(
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
                updatedAt = null
            ),
            payments = listOf(
                Payment(
                    paymentId = 1,
                    employeeId = 1,
                    paymentAmount = "2000",
                    paymentDate = "1673994000000",
                    paymentType = PaymentType.Advanced,
                    paymentMode = PaymentMode.Cash,
                    paymentNote = "Advance payment",
                    createdAt = 1673994000000,
                    updatedAt = null
                ),
                Payment(
                    paymentId = 18,
                    employeeId = 1,
                    paymentAmount = "500",
                    paymentDate = "1673994000000",
                    paymentType = PaymentType.Advanced,
                    paymentMode = PaymentMode.Online,
                    paymentNote = "Advance payment",
                    createdAt = 1674080400000,
                    updatedAt = null
                ),
                Payment(
                    paymentId = 19,
                    employeeId = 1,
                    paymentAmount = "400",
                    paymentDate = "1673994000000",
                    paymentType = PaymentType.Advanced,
                    paymentMode = PaymentMode.Online,
                    paymentNote = "Advance payment",
                    createdAt = 1674166800000,
                    updatedAt = null
                ),
                Payment(
                    paymentId = 20,
                    employeeId = 1,
                    paymentAmount = "400",
                    paymentDate = "1674339600000",
                    paymentType = PaymentType.Advanced,
                    paymentMode = PaymentMode.Cash,
                    paymentNote = "Advance payment",
                    createdAt = 1674166800000,
                    updatedAt = null
                ),
                Payment(
                    paymentId = 2,
                    employeeId = 1,
                    paymentAmount = "2500",
                    paymentDate = "1675404000000",
                    paymentType = PaymentType.Advanced,
                    paymentMode = PaymentMode.Online,
                    paymentNote = "Advance payment",
                    createdAt = 1675404000000,
                    updatedAt = null
                )
            )
        ),
        EmployeeWithPayments(
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
                updatedAt = null
            ),
            payments = listOf(
                Payment(
                    paymentId = 3,
                    employeeId = 2,
                    paymentAmount = "1800",
                    paymentDate = "1676672400000",
                    paymentType = PaymentType.Advanced,
                    paymentMode = PaymentMode.Cash,
                    paymentNote = "Advance payment",
                    createdAt = 1676672400000,
                    updatedAt = null
                ),
                Payment(
                    paymentId = 4,
                    employeeId = 2,
                    paymentAmount = "2200",
                    paymentDate = "1678080000000",
                    paymentType = PaymentType.Advanced,
                    paymentMode = PaymentMode.Online,
                    paymentNote = "Advance payment",
                    createdAt = 1678080000000,
                    updatedAt = null
                )
            )
        )
    )
}