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

package com.niyaj.employee

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_DIGIT_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_EMPTY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_LENGTH_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_EMPTY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_LENGTH_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_LETTER_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_POSITION_EMPTY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_EMPTY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_LENGTH_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_LETTER_ERROR
import com.niyaj.domain.employee.ValidateEmployeeNameUseCase
import com.niyaj.domain.employee.ValidateEmployeePhoneUseCase
import com.niyaj.domain.employee.ValidateEmployeePositionUseCase
import com.niyaj.domain.employee.ValidateEmployeeSalaryUseCase
import com.niyaj.employee.createOrUpdate.AddEditEmployeeEvent
import com.niyaj.employee.createOrUpdate.AddEditEmployeeViewModel
import com.niyaj.model.EmployeeSalaryType.Daily
import com.niyaj.model.EmployeeSalaryType.Monthly
import com.niyaj.model.EmployeeType.FullTime
import com.niyaj.model.EmployeeType.PartTime
import com.niyaj.testing.repository.TestEmployeeRepository
import com.niyaj.testing.repository.TestQRCodeScanner
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertNull

class AddEditEmployeeViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestEmployeeRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()
    private val qrCodeScanner = TestQRCodeScanner()

    private val validateEmployeePhone =
        ValidateEmployeePhoneUseCase(repository, UnconfinedTestDispatcher())
    private val validateEmployeeName =
        ValidateEmployeeNameUseCase(repository, UnconfinedTestDispatcher())
    private val validateEmployeePosition = ValidateEmployeePositionUseCase()
    private val validateEmployeeSalary = ValidateEmployeeSalaryUseCase()

    private lateinit var viewModel: AddEditEmployeeViewModel

    @Before
    fun setup() {
        viewModel = AddEditEmployeeViewModel(
            repository,
            validateEmployeePhone,
            validateEmployeeName,
            validateEmployeePosition,
            validateEmployeeSalary,
            analyticsHelper = analyticsHelper,
            scanner = qrCodeScanner,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `init with employeeId loads employee`() = runTest {
        val employee = repository.createTestItem()

        val savedStateHandle = SavedStateHandle()
        savedStateHandle["employeeId"] = employee.employeeId

        viewModel = AddEditEmployeeViewModel(
            repository,
            validateEmployeePhone,
            validateEmployeeName,
            validateEmployeePosition,
            validateEmployeeSalary,
            analyticsHelper = analyticsHelper,
            scanner = qrCodeScanner,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(employee.employeePhone, viewModel.state.employeePhone)
        assertEquals(employee.employeeName, viewModel.state.employeeName)
        assertEquals(employee.employeeSalary, viewModel.state.employeeSalary)
        assertEquals(employee.employeePosition, viewModel.state.employeePosition)
        assertEquals(employee.employeeEmail, viewModel.state.employeeEmail)
        assertEquals(employee.employeeSalaryType, viewModel.state.employeeSalaryType)
        assertEquals(employee.employeeType, viewModel.state.employeeType)
        assertEquals(employee.isDeliveryPartner, viewModel.state.isDeliveryPartner)
        assertEquals(employee.employeeJoinedDate, viewModel.state.employeeJoinedDate)
    }

    @Test
    fun `when EmployeeNameChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged("New Customer"))
        assertEquals("New Customer", viewModel.state.employeeName)
    }

    @Test
    fun `when EmployeePhoneChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged("9078563421"))
        assertEquals("9078563421", viewModel.state.employeePhone)
    }

    @Test
    fun `when EmployeeSalaryChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged("10000"))
        assertEquals("10000", viewModel.state.employeeSalary)
    }

    @Test
    fun `when EmployeeEmailChanged event is received, state is toggled`() {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeEmailChanged("new@gmail.com"))

        assertEquals("new@gmail.com", viewModel.state.employeeEmail)
    }

    @Test
    fun `when EmployeeJoinedDateChanged event is received, state is toggled`() {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeJoinedDateChanged("160020300"))

        assertEquals("160020300", viewModel.state.employeeJoinedDate)
    }

    @Test
    fun `when EmployeePositionChanged event is received, state is toggled`() {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePositionChanged("Chef"))

        assertEquals("Chef", viewModel.state.employeePosition)
    }

    @Test
    fun `when EmployeeSalaryTypeChanged event is received, state is toggled`() {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryTypeChanged(Monthly))

        assertEquals(Monthly, viewModel.state.employeeSalaryType)
    }

    @Test
    fun `when EmployeeTypeChanged event is received, state is toggled`() {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeTypeChanged(FullTime))

        assertEquals(FullTime, viewModel.state.employeeType)
    }

    @Test
    fun `when UpdateDeliveryPartner event is received, state is toggled`() {
        val state = viewModel.state.isDeliveryPartner
        viewModel.onEvent(AddEditEmployeeEvent.UpdateDeliveryPartner)

        assertEquals(!state, viewModel.state.isDeliveryPartner)
    }

    /*
    // Commented out because of the dependency on the QRCodeScanner
    @Test
    fun `when ScanQRCode event is received, state is changed`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.ScanQRCode)
        qrCodeScanner.setQRCode("result")

        assertEquals("result", viewModel.state.partnerQRCode)
    }
     */

    @Test
    fun `phoneError updates when phone number is empty`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged(""))

        viewModel.phoneError.test {
            assertEquals(EMPLOYEE_PHONE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `phoneError updates when phone number is less than 10 digit`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged("9078"))

        viewModel.phoneError.test {
            assertEquals(EMPLOYEE_PHONE_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `phoneError updates when phone number is greater than 10 digit`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged("907890785634"))

        viewModel.phoneError.test {
            assertEquals(EMPLOYEE_PHONE_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `phoneError updates when phone number contains any letter`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged("TEST123456"))

        viewModel.phoneError.test {
            assertEquals(EMPLOYEE_PHONE_LETTER_ERROR, awaitItem())
        }
    }

    @Test
    fun `phoneError updates when phone number is not unique`() = runTest {
        val charges = repository.createTestItem()
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged(charges.employeePhone))

        viewModel.phoneError.test {
            assertEquals(EMPLOYEE_PHONE_ALREADY_EXIST_ERROR, awaitItem())
        }
    }

    @Test
    fun `phoneError updates when phone number and id is same`() = runTest {
        val charges = repository.createTestItem()
        viewModel.setEmployeeId(charges.employeeId)

        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged(charges.employeePhone))

        viewModel.phoneError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `nameError updates when name is empty`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged(""))

        viewModel.nameError.test {
            assertEquals(EMPLOYEE_NAME_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when name is and length is shorter`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged("TE"))

        viewModel.nameError.test {
            assertEquals(EMPLOYEE_NAME_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when name contains any digit`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged("TEST4"))

        viewModel.nameError.test {
            assertEquals(EMPLOYEE_NAME_DIGIT_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when phone number is not unique`() = runTest {
        val charges = repository.createTestItem()
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged(charges.employeeName))

        viewModel.nameError.test {
            assertEquals(EMPLOYEE_NAME_ALREADY_EXIST_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when phone number and id is same`() = runTest {
        val charges = repository.createTestItem()
        viewModel.setEmployeeId(charges.employeeId)

        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged(charges.employeeName))

        viewModel.nameError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `positionError updates when employee position is empty`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePositionChanged(""))

        viewModel.positionError.test {
            assertEquals(EMPLOYEE_POSITION_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `salaryError updates when employee salary is empty`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged(""))

        viewModel.salaryError.test {
            assertEquals(EMPLOYEE_SALARY_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `salaryError updates when employee salary when less than five digit`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged("1232"))

        viewModel.salaryError.test {
            assertEquals(EMPLOYEE_SALARY_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `salaryError updates when employee salary when greater than five digit`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged("123256"))

        viewModel.salaryError.test {
            assertEquals(EMPLOYEE_SALARY_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `salaryError updates when employee salary contains any letter`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged("1232#"))

        viewModel.salaryError.test {
            assertEquals(EMPLOYEE_SALARY_LETTER_ERROR, awaitItem())
        }
    }

    @Test
    fun `create a new employee with valid input emits success event`() = runTest {
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged("Test Employee"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged("9078563421"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged("10000"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeEmailChanged("test@gmail.com"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePositionChanged("Chef"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryTypeChanged(Monthly))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeTypeChanged(PartTime))
        viewModel.onEvent(AddEditEmployeeEvent.UpdateDeliveryPartner)
        viewModel.onEvent(AddEditEmployeeEvent.CreateOrUpdateEmployee)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Employee Created Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getEmployeeById(0)
        assertNotNull(data)
        assertEquals("Test Employee", data?.employeeName)
        assertEquals("9078563421", data?.employeePhone)
        assertEquals("10000", data?.employeeSalary)
        assertEquals("test@gmail.com", data?.employeeEmail)
        assertEquals("Chef", data?.employeePosition)
        assertEquals(Monthly, data?.employeeSalaryType)
        assertEquals(PartTime, data?.employeeType)
        assertEquals(true, data?.isDeliveryPartner)
    }

    @Test
    fun `update a employee with valid input emit success event`() = runTest {
        viewModel.setEmployeeId(1)
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged("Test Employee"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged("9078563421"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged("10000"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeEmailChanged("test@gmail.com"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePositionChanged("Chef"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryTypeChanged(Monthly))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeTypeChanged(PartTime))
        viewModel.onEvent(AddEditEmployeeEvent.UpdateDeliveryPartner)
        viewModel.onEvent(AddEditEmployeeEvent.CreateOrUpdateEmployee)

        advanceUntilIdle()

        viewModel.onEvent(AddEditEmployeeEvent.EmployeeNameChanged("Updated Employee"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePhoneChanged("1234567890"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryChanged("12000"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeEmailChanged("updated@gmail.com"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeePositionChanged("Master"))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeSalaryTypeChanged(Daily))
        viewModel.onEvent(AddEditEmployeeEvent.EmployeeTypeChanged(FullTime))
        viewModel.onEvent(AddEditEmployeeEvent.UpdateDeliveryPartner)
        viewModel.onEvent(AddEditEmployeeEvent.CreateOrUpdateEmployee)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Employee Updated Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getEmployeeById(1)
        assertNotNull(data)
        assertEquals("1234567890", data?.employeePhone)
        assertEquals("Updated Employee", data?.employeeName)
        assertEquals("12000", data?.employeeSalary)
        assertEquals("updated@gmail.com", data?.employeeEmail)
        assertEquals("Master", data?.employeePosition)
        assertEquals(Daily, data?.employeeSalaryType)
        assertEquals(FullTime, data?.employeeType)
        assertEquals(true, data?.isDeliveryPartner)
    }
}
