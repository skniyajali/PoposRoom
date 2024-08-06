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

package com.niyaj.customer

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_VALID_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_LENGTH_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_EMPTY_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_LENGTH_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_LETTER_ERROR
import com.niyaj.customer.createOrUpdate.AddEditCustomerEvent
import com.niyaj.customer.createOrUpdate.AddEditCustomerViewModel
import com.niyaj.domain.customer.ValidateCustomerEmailUseCase
import com.niyaj.domain.customer.ValidateCustomerNameUseCase
import com.niyaj.domain.customer.ValidateCustomerPhoneUseCase
import com.niyaj.testing.repository.TestCustomerRepository
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

class AddEditCustomerViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestCustomerRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()

    private val phoneUseCase = ValidateCustomerPhoneUseCase(repository, UnconfinedTestDispatcher())
    private val nameUseCase = ValidateCustomerNameUseCase()
    private val emailUseCase = ValidateCustomerEmailUseCase()

    private lateinit var viewModel: AddEditCustomerViewModel

    @Before
    fun setup() {
        viewModel = AddEditCustomerViewModel(
            customerRepository = repository,
            validateCustomerName = nameUseCase,
            validateCustomerEmail = emailUseCase,
            validateCustomerPhone = phoneUseCase,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `init with customerId loads customer`() = runTest {
        val customer = repository.createTestItem()

        val savedStateHandle = SavedStateHandle()
        savedStateHandle["customerId"] = customer.customerId

        viewModel = AddEditCustomerViewModel(
            customerRepository = repository,
            validateCustomerName = nameUseCase,
            validateCustomerEmail = emailUseCase,
            validateCustomerPhone = phoneUseCase,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(customer.customerPhone, viewModel.state.customerPhone)
        assertEquals(customer.customerName, viewModel.state.customerName)
        assertEquals(customer.customerEmail, viewModel.state.customerEmail)
    }

    @Test
    fun `when CustomerPhoneChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged("9078563421"))
        assertEquals("9078563421", viewModel.state.customerPhone)
    }

    @Test
    fun `when CustomerNameChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditCustomerEvent.CustomerNameChanged("New Customer"))
        assertEquals("New Customer", viewModel.state.customerName)
    }

    @Test
    fun `when CustomerEmailChanged event is received, state is toggled`() {
        viewModel.onEvent(AddEditCustomerEvent.CustomerEmailChanged("new@gmail.com"))

        assertEquals("new@gmail.com", viewModel.state.customerEmail)
    }

    @Test
    fun `phoneError updates when phone number is empty`() = runTest {
        viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged(""))

        viewModel.phoneError.test {
            assertEquals(CUSTOMER_PHONE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `phoneError updates when phone number is less than 10 digit`() = runTest {
        viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged("9078"))

        viewModel.phoneError.test {
            assertEquals(CUSTOMER_PHONE_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `phoneError updates when phone number is greater than 10 digit`() = runTest {
        viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged("907890785634"))

        viewModel.phoneError.test {
            assertEquals(CUSTOMER_PHONE_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `phoneError updates when phone number contains any letter`() = runTest {
        viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged("TEST123456"))

        viewModel.phoneError.test {
            assertEquals(CUSTOMER_PHONE_LETTER_ERROR, awaitItem())
        }
    }

    @Test
    fun `phoneError updates when phone number is not unique`() = runTest {
        val charges = repository.createTestItem()
        viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged(charges.customerPhone))

        viewModel.phoneError.test {
            assertEquals(CUSTOMER_PHONE_ALREADY_EXIST_ERROR, awaitItem())
        }
    }

    @Test
    fun `phoneError updates when phone number and id is same`() = runTest {
        val charges = repository.createTestItem()
        viewModel.setCustomerId(charges.customerId)

        viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged(charges.customerPhone))

        viewModel.phoneError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `nameError updates when name is not empty, and length is shorter`() = runTest {
        viewModel.onEvent(AddEditCustomerEvent.CustomerNameChanged("TE"))

        viewModel.nameError.test {
            assertEquals(CUSTOMER_NAME_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `emailError updates when email is not empty nor valid`() = runTest {
        viewModel.onEvent(AddEditCustomerEvent.CustomerEmailChanged("test.com"))

        viewModel.emailError.test {
            assertEquals(CUSTOMER_EMAIL_VALID_ERROR, awaitItem())
        }
    }

    @Test
    fun `create a new customer with valid input emits success event`() = runTest {
        viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged("9078563421"))
        viewModel.onEvent(AddEditCustomerEvent.CustomerNameChanged("Test Customer"))
        viewModel.onEvent(AddEditCustomerEvent.CustomerEmailChanged("test@gmail.com"))
        viewModel.onEvent(AddEditCustomerEvent.CreateOrUpdateCustomer)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Customer Created Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getCustomerById(0).data
        assertNotNull(data)
        assertEquals("9078563421", data?.customerPhone)
        assertEquals("Test Customer", data?.customerName)
        assertEquals("test@gmail.com", data?.customerEmail)
    }

    @Test
    fun `update a customer with valid input emit success event`() = runTest {
        viewModel.setCustomerId(1)
        viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged("9078563421"))
        viewModel.onEvent(AddEditCustomerEvent.CustomerNameChanged("Test Customer"))
        viewModel.onEvent(AddEditCustomerEvent.CustomerEmailChanged("test@gmail.com"))
        viewModel.onEvent(AddEditCustomerEvent.CreateOrUpdateCustomer)

        advanceUntilIdle()

        viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged("1234567890"))
        viewModel.onEvent(AddEditCustomerEvent.CustomerNameChanged("Updated Customer"))
        viewModel.onEvent(AddEditCustomerEvent.CustomerEmailChanged("updated@gmail.com"))
        viewModel.onEvent(AddEditCustomerEvent.CreateOrUpdateCustomer)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Customer Updated Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getCustomerById(1).data
        assertNotNull(data)
        assertEquals("1234567890", data?.customerPhone)
        assertEquals("Updated Customer", data?.customerName)
        assertEquals("updated@gmail.com", data?.customerEmail)
    }
}
