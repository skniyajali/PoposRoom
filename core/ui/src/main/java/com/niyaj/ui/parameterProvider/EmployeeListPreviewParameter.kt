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
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeePayments
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.EmployeePreviewData.employeeList
import com.niyaj.ui.parameterProvider.EmployeePreviewData.employeeSalaryEstimations
import kotlinx.collections.immutable.persistentListOf

class EmployeeListPreviewParameter : PreviewParameterProvider<UiState<List<Employee>>> {
    override val values: Sequence<UiState<List<Employee>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(employeeList),
        )
}

class EmployeePreviewParameter : PreviewParameterProvider<UiState<Employee>> {
    override val values: Sequence<UiState<Employee>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(employeeList.first()),
        )
}

class EmployeeSalaryEstimationPreviewParameter :
    PreviewParameterProvider<UiState<EmployeeSalaryEstimation>> {
    override val values: Sequence<UiState<EmployeeSalaryEstimation>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(employeeSalaryEstimations.last()),
        )
}

class EmployeePaymentsPreviewParameter : PreviewParameterProvider<UiState<List<EmployeePayments>>> {
    override val values: Sequence<UiState<List<EmployeePayments>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(EmployeePreviewData.employeePayments),
        )
}

class EmployeeAbsentsPreviewParameter : PreviewParameterProvider<UiState<List<EmployeeAbsentDates>>> {
    override val values: Sequence<UiState<List<EmployeeAbsentDates>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(EmployeePreviewData.employeeAbsentDates),
        )
}

@Suppress("MaxLineLength")
object EmployeePreviewData {

    val employeeList = listOf(
        Employee(
            employeeId = 1,
            employeeName = "John Doe",
            employeePhone = "1234567890",
            employeeSalary = "12000",
            employeePosition = "Chef",
            employeeJoinedDate = "1682659200000",
            employeeEmail = "john.doe@company.com",
            employeeSalaryType = EmployeeSalaryType.Monthly,
            employeeType = EmployeeType.FullTime,
            isDeliveryPartner = false,
            partnerQRCode = null,
            createdAt = 1685856000000,
            updatedAt = 1686460800000,
        ),
        Employee(
            employeeId = 2,
            employeeName = "Jane Smith",
            employeePhone = "9876543210",
            employeeSalary = "14500",
            employeePosition = "Master",
            employeeJoinedDate = "1684069200000",
            employeeEmail = "jane.smith@company.com",
            employeeSalaryType = EmployeeSalaryType.Monthly,
            employeeType = EmployeeType.FullTime,
            isDeliveryPartner = false,
            partnerQRCode = null,
            createdAt = 1685942400000,
            updatedAt = 1686547200000,
        ),
        Employee(
            employeeId = 3,
            employeeName = "Michael Johnson",
            employeePhone = "5555555555",
            employeeSalary = "13000",
            employeePosition = "Assistant",
            employeeJoinedDate = "1685479200000",
            employeeEmail = "michael.johnson@company.com",
            employeeSalaryType = EmployeeSalaryType.Weekly,
            employeeType = EmployeeType.PartTime,
            isDeliveryPartner = true,
            partnerQRCode = "QR1234",
            createdAt = 1686028800000,
            updatedAt = 1686633600000,
        ),
        Employee(
            employeeId = 4,
            employeeName = "Emily Wilson",
            employeePhone = "1112223333",
            employeeSalary = "16000",
            employeePosition = "Chef",
            employeeJoinedDate = "1686889200000",
            employeeEmail = "emily.wilson@company.com",
            employeeSalaryType = EmployeeSalaryType.Monthly,
            employeeType = EmployeeType.FullTime,
            isDeliveryPartner = false,
            partnerQRCode = null,
            createdAt = 1686115200000,
            updatedAt = 1686720000000,
        ),
        Employee(
            employeeId = 5,
            employeeName = "David Brown",
            employeePhone = "4445556666",
            employeeSalary = "12500",
            employeePosition = "Master",
            employeeJoinedDate = "1688299200000",
            employeeEmail = "david.brown@company.com",
            employeeSalaryType = EmployeeSalaryType.Daily,
            employeeType = EmployeeType.PartTime,
            isDeliveryPartner = true,
            partnerQRCode = "QR5678",
            createdAt = 1686201600000,
            updatedAt = 1686806400000,
        ),
        Employee(
            employeeId = 6,
            employeeName = "Sarah Thompson",
            employeePhone = "7778889999",
            employeeSalary = "14000",
            employeePosition = "Master",
            employeeJoinedDate = "1689709200000",
            employeeEmail = "sarah.thompson@company.com",
            employeeSalaryType = EmployeeSalaryType.Weekly,
            employeeType = EmployeeType.FullTime,
            isDeliveryPartner = false,
            partnerQRCode = null,
            createdAt = 1686288000000,
            updatedAt = 1686892800000,
        ),
        Employee(
            employeeId = 7,
            employeeName = "Robert Garcia",
            employeePhone = "2223334444",
            employeeSalary = "13500",
            employeePosition = "Assistant",
            employeeJoinedDate = "1691119200000",
            employeeEmail = "robert.garcia@company.com",
            employeeSalaryType = EmployeeSalaryType.Monthly,
            employeeType = EmployeeType.FullTime,
            isDeliveryPartner = false,
            partnerQRCode = null,
            createdAt = 1686374400000,
            updatedAt = 1686979200000,
        ),
        Employee(
            employeeId = 8,
            employeeName = "Jennifer Davis",
            employeePhone = "6667778888",
            employeeSalary = "12000",
            employeePosition = "Master",
            employeeJoinedDate = "1692529200000",
            employeeEmail = null,
            employeeSalaryType = EmployeeSalaryType.Daily,
            employeeType = EmployeeType.PartTime,
            isDeliveryPartner = true,
            partnerQRCode = "QR9012",
            createdAt = 1686460800000,
            updatedAt = 1687065600000,
        ),
        Employee(
            employeeId = 9,
            employeeName = "Christopher Lee",
            employeePhone = "9998887777",
            employeeSalary = "15500",
            employeePosition = "Chef",
            employeeJoinedDate = "1693939200000",
            employeeEmail = "christopher.lee@company.com",
            employeeSalaryType = EmployeeSalaryType.Monthly,
            employeeType = EmployeeType.FullTime,
            isDeliveryPartner = false,
            partnerQRCode = null,
            createdAt = 1686547200000,
            updatedAt = 1687152000000,
        ),
        Employee(
            employeeId = 10,
            employeeName = "Amanda Taylor",
            employeePhone = "6665554444",
            employeeSalary = "13800",
            employeePosition = "Assistant",
            employeeJoinedDate = "1695349200000",
            employeeEmail = "amanda.taylor@company.com",
            employeeSalaryType = EmployeeSalaryType.Daily,
            employeeType = EmployeeType.FullTime,
            isDeliveryPartner = false,
            partnerQRCode = null,
            createdAt = 1686633600000,
            updatedAt = 1687238400000,
        ),
    )

    val employeeMonthlyDates = listOf(
        EmployeeMonthlyDate(
            // January 10, 2024
            startDate = "1673308800000",
            // February 9, 2024
            endDate = "1675987200000",
        ),
        EmployeeMonthlyDate(
            // February 10, 2024
            startDate = "1675987200000",
            // March 11, 2024
            endDate = "1678665600000",
        ),
        EmployeeMonthlyDate(
            // March 11, 2024
            startDate = "1678665600000",
            // April 10, 2024
            endDate = "1681257600000",
        ),
        EmployeeMonthlyDate(
            // April 10, 2024
            startDate = "1681257600000",
            // May 10, 2024
            endDate = "1683849600000",
        ),
        EmployeeMonthlyDate(
            // May 10, 2024
            startDate = "1683849600000",
            // June 10, 2024
            endDate = "1686528000000",
        ),
    )

    val employeeSalaryEstimations = listOf(
        EmployeeSalaryEstimation(
            // January 10, 2024
            startDate = "1673308800000",
            // February 9, 2024
            endDate = "1675987200000",
            status = "Completed",
            message = null,
            remainingAmount = "0",
            paymentCount = "5",
            absentCount = "1",
        ),
        EmployeeSalaryEstimation(
            // February 10, 2024
            startDate = "1675987200000",
            // March 11, 2024
            endDate = "1678665600000",
            status = "Completed",
            message = null,
            remainingAmount = "0",
            paymentCount = "6",
            absentCount = "0",
        ),
        EmployeeSalaryEstimation(
            // March 11, 2024
            startDate = "1678665600000",
            // April 10, 2024
            endDate = "1681257600000",
            status = "Completed",
            message = null,
            remainingAmount = "0",
            paymentCount = "7",
            absentCount = "2",
        ),
        EmployeeSalaryEstimation(
            // April 10, 2024
            startDate = "1681257600000",
            // May 10, 2024
            endDate = "1683849600000",
            status = "Completed",
            message = "Extra amount of 800 deducted from next month's salary",
            remainingAmount = "0",
            paymentCount = "6",
            absentCount = "1",
        ),
        EmployeeSalaryEstimation(
            // May 10, 2024
            startDate = "1683849600000",
            // June 10, 2024
            endDate = "1686528000000",
            status = "In Progress",
            message = "Remaining 10000 have to pay",
            // Salary - 12000 = (2 absent days = 800), (3 payment 400 per days = 1200), 12000 - (800+1200) = 10000 (remaining)
            remainingAmount = "10000",
            paymentCount = "3",
            absentCount = "2",
        ),
    )

    val employeePayments = listOf(
        EmployeePayments(
            startDate = "1673308800000",
            endDate = "1675987200000",
            payments = persistentListOf(
                Payment(
                    paymentId = 1,
                    employeeId = 1,
                    paymentAmount = "2000",
                    paymentDate = "1673994000000",
                    paymentType = PaymentType.Advanced,
                    paymentMode = PaymentMode.Cash,
                    paymentNote = "Advance payment",
                    createdAt = 1673994000000,
                    updatedAt = null,
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
                    updatedAt = null,
                ),
            ),
        ),
        EmployeePayments(
            startDate = "1675987200000",
            endDate = "1678665600000",
            payments = persistentListOf(),
        ),
        EmployeePayments(
            startDate = "1678665600000",
            endDate = "1681257600000",
            payments = persistentListOf(
                Payment(
                    paymentId = 5,
                    employeeId = 3,
                    paymentAmount = "3000",
                    paymentDate = "1679350800000",
                    paymentType = PaymentType.Advanced,
                    paymentMode = PaymentMode.Cash,
                    paymentNote = "Advance payment",
                    createdAt = 1679350800000,
                    updatedAt = null,
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
                    updatedAt = null,
                ),
            ),
        ),
        EmployeePayments(
            startDate = "1681257600000",
            endDate = "1683849600000",
            payments = persistentListOf(),
        ),
        EmployeePayments(
            startDate = "1683849600000",
            endDate = "1686528000000",
            payments = persistentListOf(
                Payment(
                    paymentId = 9,
                    employeeId = 5,
                    paymentAmount = "2000",
                    paymentDate = "1684537200000",
                    paymentType = PaymentType.Advanced,
                    paymentMode = PaymentMode.Cash,
                    paymentNote = "Advance payment",
                    createdAt = 1684537200000,
                    updatedAt = null,
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
                    updatedAt = null,
                ),
            ),
        ),
    )

    val employeeAbsentDates = listOf(
        EmployeeAbsentDates(
            startDate = "1673308800000",
            endDate = "1675987200000",
            absentDates = persistentListOf(
                "1674718800000",
                "1675323600000",
            ),
        ),
        EmployeeAbsentDates(
            startDate = "1675987200000",
            endDate = "1678665600000",
            absentDates = persistentListOf(),
        ),
        EmployeeAbsentDates(
            startDate = "1678665600000",
            endDate = "1681257600000",
            absentDates = persistentListOf(
                "1679350800000",
                "1680758400000",
            ),
        ),
        EmployeeAbsentDates(
            startDate = "1681257600000",
            endDate = "1683849600000",
            absentDates = persistentListOf(),
        ),
        EmployeeAbsentDates(
            startDate = "1683849600000",
            endDate = "1686528000000",
            absentDates = persistentListOf(
                "1684537200000",
                "1685949600000",
            ),
        ),
    )

    val samplePayment = Payment(
        paymentId = 1,
        employeeId = 1,
        paymentAmount = "2000",
        paymentDate = "1673994000000",
        paymentType = PaymentType.Advanced,
        paymentMode = PaymentMode.Cash,
        paymentNote = "Advance payment",
        createdAt = 1673994000000,
        updatedAt = null,
    )
}
