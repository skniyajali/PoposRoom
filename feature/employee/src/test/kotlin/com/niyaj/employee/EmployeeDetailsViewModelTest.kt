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
import com.niyaj.employee.details.EmployeeDetailsViewModel
import com.niyaj.testing.repository.TestEmployeeRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.EmployeePreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class EmployeeDetailsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestEmployeeRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()

    private val employeeDates = EmployeePreviewData.employeeMonthlyDates
    private val employeePayments = EmployeePreviewData.employeePayments
    private val employeeAbsentDates = EmployeePreviewData.employeeAbsentDates

    private lateinit var viewModel: EmployeeDetailsViewModel

    @Before
    fun setup() {
        viewModel = EmployeeDetailsViewModel(
            employeeRepository = repository,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `customerDetails is loading, initially`() = runTest {
        assertEquals(UiState.Loading, viewModel.employeeDetails.value)
    }

    @Test
    fun `employeeDetails is empty, when no customer found`() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.employeeDetails.collect() }

        assertEquals(UiState.Empty, viewModel.employeeDetails.value)

        job.cancel()
    }

    @Test
    fun `init with customerId loads customer details`() = runTest {
        val data = repository.createTestItem()
        viewModel.setEmployeeId(data.employeeId)

        val job = launch(UnconfinedTestDispatcher()) { viewModel.employeeDetails.collect() }

        assertEquals(UiState.Success(data), viewModel.employeeDetails.value)

        job.cancel()
    }

    @Test
    fun `salaryDates is empty, initially`() = runTest {
        assertEquals(emptyList(), viewModel.salaryDates.value)
    }

    @Test
    fun `salaryDates is populated, when salaryDates found`() = runTest {
        repository.updateMonthlyDate(employeeDates)

        val job = launch(UnconfinedTestDispatcher()) { viewModel.salaryDates.collect() }

        assertEquals(employeeDates, viewModel.salaryDates.value)

        job.cancel()
    }

    @Test
    fun `employeeAbsentDates is empty, when no employeeAbsentDates found`() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.employeeAbsentDates.collect() }

        assertEquals(UiState.Empty, viewModel.employeeAbsentDates.value)

        job.cancel()
    }

    @Test
    fun `employeeAbsentDates populated, when employeeAbsentDates found`() = runTest {
        repository.updateAbsentDates(employeeAbsentDates)

        val job = launch(UnconfinedTestDispatcher()) { viewModel.employeeAbsentDates.collect() }

        assertEquals(UiState.Success(employeeAbsentDates), viewModel.employeeAbsentDates.value)

        job.cancel()
    }

    @Test
    fun `salaryEstimation populated, when salaryEstimation found`() = runTest {
        val salaryEstimation = EmployeePreviewData.employeeSalaryEstimations.first()
        repository.updateSalaryEstimation(salaryEstimation)

        val job = launch(UnconfinedTestDispatcher()) { viewModel.salaryEstimation.collect() }

        assertEquals(UiState.Success(salaryEstimation), viewModel.salaryEstimation.value)

        job.cancel()
    }

    @Test
    fun `payments is empty, when no payments found`() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.payments.collect() }

        assertEquals(UiState.Empty, viewModel.payments.value)

        job.cancel()
    }

    @Test
    fun `payments populated, when payments found`() = runTest {
        repository.updateEmployeePayments(employeePayments)

        val job = launch(UnconfinedTestDispatcher()) { viewModel.payments.collect() }

        assertEquals(UiState.Success(employeePayments), viewModel.payments.value)

        job.cancel()
    }
}
