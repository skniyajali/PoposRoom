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
import com.niyaj.employeePayment.settings.PaymentSettingsEvent
import com.niyaj.employeePayment.settings.PaymentSettingsViewModel
import com.niyaj.model.searchEmployeeWithPayments
import com.niyaj.testing.repository.TestPaymentRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.parameterProvider.PaymentPreviewData
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class PaymentSettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val itemList = PaymentPreviewData.employeesWithPayments
    private val repository = TestPaymentRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private lateinit var viewModel: PaymentSettingsViewModel

    @Before
    fun setup() {
        viewModel = PaymentSettingsViewModel(repository, analyticsHelper)
    }

    @Test
    fun `when search text changes, charges are updated`() = runTest {
        repository.updateEmployeePayments(itemList)

        viewModel.searchTextChanged("500")
        advanceUntilIdle()

        viewModel.items.test {
            assertEquals(itemList.searchEmployeeWithPayments("500"), awaitItem())
        }
    }

    @Test
    fun `when GetExportedItems event is triggered with no selection, all items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.items.collect() }

            repository.updateEmployeePayments(itemList)

            viewModel.onEvent(PaymentSettingsEvent.GetExportedItems)
            testScheduler.advanceUntilIdle()

            viewModel.exportedItems.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when GetExportedItems event is triggered with selection, only selected items are exported`() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.items.collect() }

            repository.updateEmployeePayments(itemList)
            val selectedItems = itemList.filter { items ->
                items.payments.any {
                    it.paymentId == 1 || it.paymentId == 3
                }
            }

            viewModel.selectItem(1)
            viewModel.selectItem(3)

            viewModel.onEvent(PaymentSettingsEvent.GetExportedItems)
            advanceUntilIdle()

            assertEquals(selectedItems, viewModel.exportedItems.value)

            job.cancel()
        }

    @Test
    fun `when OnImportPaymentsFromFile event is triggered, importedItems are updated`() =
        runTest {
            viewModel.onEvent(PaymentSettingsEvent.OnImportPaymentsFromFile(itemList))
            testScheduler.advanceUntilIdle()

            assertEquals(itemList, viewModel.importedItems.value)
        }

    @Test
    fun `when ImportPaymentsToDatabase event is triggered with no selection, all imported items are added`() =
        runTest {
            val job = launch { viewModel.items.collect() }

            viewModel.onEvent(PaymentSettingsEvent.OnImportPaymentsFromFile(itemList))
            assertEquals(itemList, viewModel.importedItems.value)
            viewModel.onEvent(PaymentSettingsEvent.ImportPaymentsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
                assertEquals(
                    "${itemList.sumOf { it.payments.size }} payments has been imported successfully",
                    (event as UiEvent.OnSuccess).successMessage,
                )
            }

            advanceUntilIdle()

            viewModel.items.test {
                assertEquals(itemList, awaitItem())
            }

            job.cancel()
        }

    @Test
    fun `when ImportPaymentsToDatabase event is triggered with selection, only selected items are added`() =
        runTest {
            val job = launch { viewModel.items.collect() }

            viewModel.onEvent(PaymentSettingsEvent.OnImportPaymentsFromFile(itemList))

            assertEquals(itemList, viewModel.importedItems.value)

            viewModel.selectItem(2)
            viewModel.selectItem(4)

            assertEquals(listOf(2, 4), viewModel.selectedItems.toList())

            viewModel.onEvent(PaymentSettingsEvent.ImportPaymentsToDatabase)
            advanceUntilIdle()

            viewModel.eventFlow.test {
                val event = awaitItem()
                assertTrue(event is UiEvent.OnSuccess)
//                assertEquals(
//                    "2 payments has been imported successfully",
//                    (event as UiEvent.OnSuccess).successMessage,
//                )
            }

            advanceUntilIdle()

            val selectedItems = itemList.filter { items ->
                items.payments.any {
                    it.paymentId == 2 || it.paymentId == 4
                }
            }

            viewModel.items.test {
                assertEquals(selectedItems, awaitItem())
            }

            job.cancel()
        }
}
