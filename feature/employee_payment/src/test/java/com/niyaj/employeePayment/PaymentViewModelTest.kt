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

import app.cash.turbine.test
import com.niyaj.model.searchEmployeeWithPayments
import com.niyaj.testing.repository.TestPaymentRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.PaymentPreviewData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class PaymentViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = PaymentPreviewData.employeesWithPayments
    private val repository = TestPaymentRepository()
    private val analyticsHelper = TestAnalyticsHelper()

    private lateinit var viewModel: PaymentViewModel

    @Before
    fun setup() {
        viewModel = PaymentViewModel(repository, analyticsHelper)
    }

    @Test
    fun paymentState_initially_Loading() = runTest {
        assertEquals(UiState.Loading, viewModel.payments.value)
    }

    @Test
    fun paymentsState_isEmpty_whenDataIsEmpty() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.payments.collect() }

        assertEquals(UiState.Empty, viewModel.payments.value)

        job.cancel()
    }

    @Test
    fun paymentsState_isSuccess_whenDataIsAvailable() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.payments.collect() }
        repository.updateEmployeePayments(itemList)

        assertEquals(UiState.Success(itemList), viewModel.payments.value)

        job.cancel()
    }

    @Test
    fun search_withInvalidData_returnEmptyResult() = runTest {
        repository.updateEmployeePayments(itemList)
        viewModel.searchTextChanged("Invalid")

        advanceUntilIdle()

        viewModel.payments.test {
            assertEquals(UiState.Empty, awaitItem())
        }
    }

    @Test
    fun search_withValidData_returnResult() = runTest {
        repository.updateEmployeePayments(itemList)
        viewModel.searchTextChanged("2000")

        advanceUntilIdle()

        viewModel.payments.test {
            assertEquals(
                UiState.Success(itemList.searchEmployeeWithPayments("2000")),
                awaitItem(),
            )
        }
    }

    @Test
    fun search_onClose_returnAllData() = runTest {
        repository.updateEmployeePayments(itemList)
        viewModel.searchTextChanged("Extra")

        advanceUntilIdle()

        viewModel.payments.test {
            assertEquals(UiState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.closeSearchBar()
        advanceUntilIdle()

        viewModel.payments.test {
            assertEquals(UiState.Success(itemList), awaitItem())
        }
    }

    @Test
    fun deleteCharges_shouldRemoveFromList() = runTest {
        repository.updateEmployeePayments(itemList)
        val paymentId = itemList.first().payments.first().paymentId
        val deletedItemList =
            itemList.filter { list -> list.payments.any { it.paymentId != paymentId } }

        viewModel.selectItem(paymentId)
        viewModel.deleteItems()

        advanceUntilIdle()

        viewModel.payments.test {
            assertEquals(UiState.Success(deletedItemList), awaitItem())
        }

        assert(paymentId !in viewModel.selectedItems.toList())
    }

    @Test
    fun selectAbsentDate_onSelectItem_shouldUpdateSelectedAbsentDate() = runTest {
        repository.updateEmployeePayments(itemList)

        val paymentId = itemList.first().payments.first().paymentId
        viewModel.selectItem(paymentId)

        advanceUntilIdle()

        assertContains(viewModel.selectedItems.toList(), paymentId)
    }

    @Test
    fun selectAllItems_onSelectAllItems_shouldUpdateSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.payments.collect() }
        val allAbsentId = itemList.flatMap { item -> item.payments.map { it.paymentId } }

        repository.updateEmployeePayments(itemList)
        viewModel.selectAllItems()

        advanceUntilIdle()

        assertEquals(allAbsentId, viewModel.selectedItems.toList())

        job.cancel()
    }

    @Test
    fun deselectItems_onDeselectItems_shouldClearSelectedItems() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.payments.collect() }
        repository.updateEmployeePayments(itemList)
        val allAbsentId = itemList.flatMap { item -> item.payments.map { it.paymentId } }

        viewModel.selectAllItems()
        assertEquals(allAbsentId, viewModel.selectedItems.toList())

        advanceUntilIdle()

        viewModel.deselectItems()

        advanceUntilIdle()

        assertEquals(emptyList(), viewModel.selectedItems.toList())

        job.cancel()
    }
}
