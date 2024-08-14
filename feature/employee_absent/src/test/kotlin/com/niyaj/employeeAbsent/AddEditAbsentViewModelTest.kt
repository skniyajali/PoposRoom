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

package com.niyaj.employeeAbsent

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_EMPTY
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_EXIST
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_EMPLOYEE_NAME_EMPTY
import com.niyaj.common.utils.getDateInMilliseconds
import com.niyaj.common.utils.getStartTime
import com.niyaj.domain.absent.ValidateAbsentDateUseCase
import com.niyaj.domain.absent.ValidateAbsentEmployeeUseCase
import com.niyaj.employeeAbsent.createOrUpdate.AddEditAbsentEvent
import com.niyaj.employeeAbsent.createOrUpdate.AddEditAbsentViewModel
import com.niyaj.testing.repository.TestAbsentRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.EmployeePreviewData
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

class AddEditAbsentViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestAbsentRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()

    private val validateAbsentDate = ValidateAbsentDateUseCase(
        absentRepository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )
    private val validateAbsentEmployee = ValidateAbsentEmployeeUseCase()

    private lateinit var viewModel: AddEditAbsentViewModel

    @Before
    fun setup() {
        viewModel = AddEditAbsentViewModel(
            absentRepository = repository,
            validateAbsentDate = validateAbsentDate,
            validateAbsentEmployee = validateAbsentEmployee,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `init with absentId loads absent`() = runTest {
        val employee = repository.createTestItem()
        val absent = repository.createTestData()

        val savedStateHandle = SavedStateHandle()
        savedStateHandle["absentId"] = absent.absentId
        savedStateHandle["employeeId"] = employee.employeeId

        viewModel = AddEditAbsentViewModel(
            absentRepository = repository,
            validateAbsentDate = validateAbsentDate,
            validateAbsentEmployee = validateAbsentEmployee,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(absent.absentDate, viewModel.state.absentDate)
        assertEquals(absent.absentReason, viewModel.state.absentReason)
        viewModel.selectedEmployee.test {
            assertEquals(absent.employeeId, awaitItem().employeeId)
        }
    }

    @Test
    fun `when AbsentDateChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditAbsentEvent.AbsentDateChanged(getStartTime))
        assertEquals(getStartTime, viewModel.state.absentDate)
    }

    @Test
    fun `when AbsentReasonChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditAbsentEvent.AbsentReasonChanged("Sick"))
        assertEquals("Sick", viewModel.state.absentReason)
    }

    @Test
    fun `when ChargesApplicableChanged event is received, state is toggled`() = runTest {
        val employee = EmployeePreviewData.employeeList.first()
        viewModel.onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))

        viewModel.selectedEmployee.test {
            assertEquals(employee, awaitItem())
        }
    }

    @Test
    fun `dateError updates when absent date is empty`() = runTest {
        viewModel.onEvent(AddEditAbsentEvent.AbsentDateChanged(""))

        viewModel.dateError.test {
            assertEquals(ABSENT_DATE_EMPTY, awaitItem())
        }
    }

    @Test
    fun `dateError updates when absent date is not unique`() = runTest {
        val absent = repository.createTestData()
        val employee = repository.createTestItem()

        viewModel.onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))
        viewModel.onEvent(AddEditAbsentEvent.AbsentDateChanged(absent.absentDate))

        viewModel.dateError.test {
            assertEquals(ABSENT_DATE_EXIST, awaitItem())
        }
    }

    @Test
    fun `dateError updates when absent date and id is same`() = runTest {
        val absent = repository.createTestData()
        val employee = repository.createTestItem()

        viewModel.setAbsentId(absent.absentId)
        viewModel.onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))
        viewModel.onEvent(AddEditAbsentEvent.AbsentDateChanged(absent.absentDate))

        viewModel.dateError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `employeeError updates when employee id is empty or zero`() = runTest {
        viewModel.employeeError.test {
            assertEquals(ABSENT_EMPLOYEE_NAME_EMPTY, awaitItem())
        }
    }

    @Test
    fun `employeeError updates when employee id is valid`() = runTest {
        val employee = EmployeePreviewData.employeeList.first()
        viewModel.onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))

        viewModel.employeeError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `add new absent with valid input emits success event`() = runTest {
        val employee = EmployeePreviewData.employeeList.first()

        viewModel.onEvent(AddEditAbsentEvent.AbsentDateChanged(getStartTime))
        viewModel.onEvent(AddEditAbsentEvent.AbsentReasonChanged("Sick Leave"))
        viewModel.onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))
        viewModel.onEvent(AddEditAbsentEvent.CreateOrUpdateAbsent)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Employee absent date created.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getAbsentById(0).data
        assertNotNull(data)
        assertEquals(getStartTime, data?.absentDate)
        assertEquals("Sick Leave", data?.absentReason)
    }

    @Test
    fun `update a absent with valid input emit success event`() = runTest {
        viewModel.setAbsentId(1)
        val employee = EmployeePreviewData.employeeList.first()

        viewModel.onEvent(AddEditAbsentEvent.AbsentDateChanged(getStartTime))
        viewModel.onEvent(AddEditAbsentEvent.AbsentReasonChanged("Sick Leave"))
        viewModel.onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))
        viewModel.onEvent(AddEditAbsentEvent.CreateOrUpdateAbsent)

        advanceUntilIdle()

        val date = getDateInMilliseconds(13)
        viewModel.onEvent(AddEditAbsentEvent.AbsentDateChanged(date))
        viewModel.onEvent(AddEditAbsentEvent.AbsentReasonChanged("Fever"))
        viewModel.onEvent(AddEditAbsentEvent.OnSelectEmployee(employee))
        viewModel.onEvent(AddEditAbsentEvent.CreateOrUpdateAbsent)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Employee absent date updated.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getAbsentById(1).data
        assertNotNull(data)
        assertEquals(data?.absentDate, data?.absentDate)
        assertEquals("Fever", data?.absentReason)
    }
}
