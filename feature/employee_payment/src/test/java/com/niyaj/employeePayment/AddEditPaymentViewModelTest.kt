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

package com.niyaj.employeePayment

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_AMOUNT_EMPTY
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_AMOUNT_LENGTH_ERROR
import com.niyaj.common.tags.PaymentScreenTags.GIVEN_AMOUNT_LETTER_ERROR
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_EMPLOYEE_NAME_EMPTY
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_GIVEN_DATE_EMPTY
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_NOTE_EMPTY
import com.niyaj.common.utils.getDateInMilliseconds
import com.niyaj.common.utils.getStartTime
import com.niyaj.domain.payment.ValidateGivenAmountUseCase
import com.niyaj.domain.payment.ValidateGivenDateUseCase
import com.niyaj.domain.payment.ValidatePaymentEmployeeUseCase
import com.niyaj.domain.payment.ValidatePaymentModeUseCase
import com.niyaj.domain.payment.ValidatePaymentNoteUseCase
import com.niyaj.domain.payment.ValidatePaymentTypeUseCase
import com.niyaj.employeePayment.createOrUpdate.AddEditPaymentEvent
import com.niyaj.employeePayment.createOrUpdate.AddEditPaymentViewModel
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType
import com.niyaj.testing.repository.TestPaymentRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.EmployeePreviewData
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertNull

class AddEditPaymentViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestPaymentRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()

    private val validateGivenAmount = ValidateGivenAmountUseCase()
    private val validateGivenDate = ValidateGivenDateUseCase()
    private val validatePaymentEmployee = ValidatePaymentEmployeeUseCase()
    private val validatePaymentMode = ValidatePaymentModeUseCase()
    private val validatePaymentNote = ValidatePaymentNoteUseCase()
    private val validatePaymentType = ValidatePaymentTypeUseCase()

    private lateinit var viewModel: AddEditPaymentViewModel

    @Before
    fun setup() {
        viewModel = AddEditPaymentViewModel(
            paymentRepository = repository,
            validateGivenAmount = validateGivenAmount,
            validateGivenDate = validateGivenDate,
            validatePaymentEmployee = validatePaymentEmployee,
            validatePaymentMode = validatePaymentMode,
            validatePaymentNote = validatePaymentNote,
            validatePaymentType = validatePaymentType,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `init with paymentId loads payment`() = runTest {
        val employee = repository.createTestEmployee()
        val payment = repository.createTestPayment()

        val savedStateHandle = SavedStateHandle()
        savedStateHandle["paymentId"] = payment.paymentId
        savedStateHandle["employeeId"] = employee.employeeId

        viewModel = AddEditPaymentViewModel(
            paymentRepository = repository,
            validateGivenAmount = validateGivenAmount,
            validateGivenDate = validateGivenDate,
            validatePaymentEmployee = validatePaymentEmployee,
            validatePaymentMode = validatePaymentMode,
            validatePaymentNote = validatePaymentNote,
            validatePaymentType = validatePaymentType,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(payment.paymentAmount, viewModel.state.paymentAmount)
        assertEquals(payment.paymentDate, viewModel.state.paymentDate)
        assertEquals(payment.paymentType, viewModel.state.paymentType)
        assertEquals(payment.paymentMode, viewModel.state.paymentMode)
        assertEquals(payment.paymentNote, viewModel.state.paymentNote)
        viewModel.selectedEmployee.test {
            assertEquals(payment.employeeId, awaitItem().employeeId)
        }
    }

    @Test
    fun `paymentAmount changes updates state`() = runTest {
        viewModel.onEvent(AddEditPaymentEvent.PaymentAmountChanged("1000"))

        assertEquals("1000", viewModel.state.paymentAmount)
    }

    @Test
    fun `paymentDate changes updates state`() = runTest {
        val date = getDateInMilliseconds(13)
        viewModel.onEvent(AddEditPaymentEvent.PaymentDateChanged(date))

        assertEquals(date, viewModel.state.paymentDate)
    }

    @Test
    fun `paymentType changes updates state`() = runTest {
        viewModel.onEvent(AddEditPaymentEvent.PaymentTypeChanged(PaymentType.Advanced))

        assertEquals(PaymentType.Advanced, viewModel.state.paymentType)
    }

    @Test
    fun `paymentMode changes updates state`() = runTest {
        viewModel.onEvent(AddEditPaymentEvent.PaymentModeChanged(PaymentMode.Cash))

        assertEquals(PaymentMode.Cash, viewModel.state.paymentMode)
    }

    @Test
    fun `paymentNote changes updates state`() = runTest {
        viewModel.onEvent(AddEditPaymentEvent.PaymentNoteChanged("Salary"))

        assertEquals("Salary", viewModel.state.paymentNote)
    }

    @Test
    fun `onSelectEmployee updates selectedEmployee`() = runTest {
        val employee = EmployeePreviewData.employeeList.first()
        viewModel.onEvent(AddEditPaymentEvent.OnSelectEmployee(employee))

        viewModel.selectedEmployee.test {
            assertEquals(employee, awaitItem())
        }
    }

    @Test
    fun `employeeError updates when employee id is empty or zero`() = runTest {
        viewModel.employeeError.test {
            assertEquals(PAYMENT_EMPLOYEE_NAME_EMPTY, awaitItem())
        }
    }

    @Test
    fun `employeeError updates when employee id is valid`() = runTest {
        val employee = EmployeePreviewData.employeeList.first()
        viewModel.onEvent(AddEditPaymentEvent.OnSelectEmployee(employee))

        viewModel.employeeError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `amountError updates when amount is empty`() = runTest {
        viewModel.amountError.test {
            assertEquals(GIVEN_AMOUNT_EMPTY, awaitItem())
        }
    }

    @Test
    fun `amountError updates when amount is less than 2 digit`() = runTest {
        viewModel.onEvent(AddEditPaymentEvent.PaymentAmountChanged("9"))
        viewModel.amountError.test {
            assertEquals(GIVEN_AMOUNT_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `amountError updates when amount contain any letter`() = runTest {
        viewModel.onEvent(AddEditPaymentEvent.PaymentAmountChanged("9C"))
        viewModel.amountError.test {
            assertEquals(GIVEN_AMOUNT_LETTER_ERROR, awaitItem())
        }
    }

    @Test
    fun `amountError updates when amount is valid`() = runTest {
        viewModel.onEvent(AddEditPaymentEvent.PaymentAmountChanged("1000"))
        viewModel.amountError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `dateError updates when date is empty`() = runTest {
        viewModel.onEvent(AddEditPaymentEvent.PaymentDateChanged(""))

        viewModel.dateError.test {
            assertEquals(PAYMENT_GIVEN_DATE_EMPTY, awaitItem())
        }
    }

    @Test
    fun `paymentNodeError updated when payment note is empty but required`() = runTest {
        viewModel.onEvent(AddEditPaymentEvent.PaymentModeChanged(PaymentMode.Both))

        viewModel.paymentNoteError.test {
            assertEquals(PAYMENT_NOTE_EMPTY, awaitItem())
        }
    }

    @Test
    fun `paymentNodeError updated when payment note is empty but not required`() = runTest {
        viewModel.onEvent(AddEditPaymentEvent.PaymentNoteChanged(""))
        viewModel.onEvent(AddEditPaymentEvent.PaymentModeChanged(PaymentMode.Cash))

        viewModel.paymentModeError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `add new payment with valid input emits success event`() = runTest {
        val employee = EmployeePreviewData.employeeList.first()

        viewModel.onEvent(AddEditPaymentEvent.OnSelectEmployee(employee))
        viewModel.onEvent(AddEditPaymentEvent.PaymentAmountChanged("500"))
        viewModel.onEvent(AddEditPaymentEvent.PaymentDateChanged(getStartTime))
        viewModel.onEvent(AddEditPaymentEvent.PaymentModeChanged(PaymentMode.Cash))
        viewModel.onEvent(AddEditPaymentEvent.PaymentTypeChanged(PaymentType.Salary))
        viewModel.onEvent(AddEditPaymentEvent.PaymentNoteChanged("Test Payment"))
        viewModel.onEvent(AddEditPaymentEvent.CreateOrUpdatePayment)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Payment Added Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getPaymentById(0).data
        assertNotNull(data)
        assertEquals(getStartTime, data?.paymentDate)
        assertEquals("500", data?.paymentAmount)
        assertEquals("Test Payment", data?.paymentNote)
    }

    @Test
    fun `update a payment with valid input emit success event`() = runTest {
        viewModel.setPaymentId(1)
        val employee = EmployeePreviewData.employeeList.first()

        viewModel.onEvent(AddEditPaymentEvent.OnSelectEmployee(employee))
        viewModel.onEvent(AddEditPaymentEvent.PaymentAmountChanged("500"))
        viewModel.onEvent(AddEditPaymentEvent.PaymentDateChanged(getStartTime))
        viewModel.onEvent(AddEditPaymentEvent.PaymentModeChanged(PaymentMode.Cash))
        viewModel.onEvent(AddEditPaymentEvent.PaymentTypeChanged(PaymentType.Salary))
        viewModel.onEvent(AddEditPaymentEvent.PaymentNoteChanged("Test Payment"))
        viewModel.onEvent(AddEditPaymentEvent.CreateOrUpdatePayment)

        advanceUntilIdle()

        val date = getDateInMilliseconds(13)
        viewModel.onEvent(AddEditPaymentEvent.OnSelectEmployee(employee))
        viewModel.onEvent(AddEditPaymentEvent.PaymentAmountChanged("2000"))
        viewModel.onEvent(AddEditPaymentEvent.PaymentDateChanged(date))
        viewModel.onEvent(AddEditPaymentEvent.PaymentModeChanged(PaymentMode.Online))
        viewModel.onEvent(AddEditPaymentEvent.PaymentTypeChanged(PaymentType.Advanced))
        viewModel.onEvent(AddEditPaymentEvent.PaymentNoteChanged("Updated Payment"))
        viewModel.onEvent(AddEditPaymentEvent.CreateOrUpdatePayment)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Payment Updated Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getPaymentById(1).data
        assertNotNull(data)
        assertEquals(date, data?.paymentDate)
        assertEquals("2000", data?.paymentAmount)
        assertEquals(PaymentMode.Online, data?.paymentMode)
        assertEquals(PaymentType.Advanced, data?.paymentType)
        assertEquals("Updated Payment", data?.paymentNote)
    }
}
