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

package com.niyaj.employee.createOrUpdate

import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType

sealed interface AddEditEmployeeEvent {
    /**
     * Employee Name Changed Event Class
     * @param employeeName [String]
     */
    data class EmployeeNameChanged(val employeeName: String) : AddEditEmployeeEvent

    /**
     * Employee Email Changed Event Class
     * @param employeeSalary [String]
     */
    data class EmployeeSalaryChanged(val employeeSalary: String) : AddEditEmployeeEvent

    /**
     * Employee Phone Changed Event Class
     * @param employeePhone [String]
     */
    data class EmployeePhoneChanged(val employeePhone: String) : AddEditEmployeeEvent

    data class EmployeeEmailChanged(val employeeEmail: String) : AddEditEmployeeEvent

    data class EmployeeSalaryTypeChanged(val employeeSalaryType: EmployeeSalaryType) :
        AddEditEmployeeEvent

    data class EmployeePositionChanged(val employeePosition: String) : AddEditEmployeeEvent

    data class EmployeeTypeChanged(val employeeType: EmployeeType) : AddEditEmployeeEvent

    data class EmployeeJoinedDateChanged(val employeeJoinedDate: String) : AddEditEmployeeEvent

    data object UpdateDeliveryPartner : AddEditEmployeeEvent

    data object ScanQRCode : AddEditEmployeeEvent

    data object CreateOrUpdateEmployee : AddEditEmployeeEvent
}
