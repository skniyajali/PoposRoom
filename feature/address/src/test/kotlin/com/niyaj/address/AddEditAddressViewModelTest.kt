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

package com.niyaj.address

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.address.createOrUpdate.AddEditAddressEvent
import com.niyaj.address.createOrUpdate.AddEditAddressViewModel
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NAME_EMPTY_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NAME_LENGTH_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SHORT_NAME_EMPTY_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_S_NAME_LESS_THAN_TWO_ERROR
import com.niyaj.domain.address.ValidateAddressNameUseCase
import com.niyaj.domain.address.ValidateAddressShortNameUseCase
import com.niyaj.model.Address
import com.niyaj.testing.repository.TestAddressRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

class AddEditAddressViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestAddressRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()

    private val validateAddressNameUseCase = ValidateAddressNameUseCase(
        repository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )
    private val shortNameUseCase = ValidateAddressShortNameUseCase()
    private lateinit var viewModel: AddEditAddressViewModel

    @Before
    fun setup() {
        viewModel = AddEditAddressViewModel(
            addressRepository = repository,
            validateAddressNameUseCase = validateAddressNameUseCase,
            validateAddressShortNameUseCase = shortNameUseCase,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `init with addressId loads address`() = runTest {
        val address = Address(1, "Test Address", "TA")
        repository.upsertAddress(address)
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["addressId"] = 1

        viewModel = AddEditAddressViewModel(
            addressRepository = repository,
            validateAddressNameUseCase = validateAddressNameUseCase,
            validateAddressShortNameUseCase = shortNameUseCase,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals("Test Address", viewModel.state.addressName)
        assertEquals("TA", viewModel.state.shortName)
    }

    @Test
    fun `onEvent AddressNameChanged updates state and shortName`() = runTest {
        viewModel.onEvent(AddEditAddressEvent.AddressNameChanged("New Address"))

        assertEquals("New Address", viewModel.state.addressName)
        assertEquals("NA", viewModel.state.shortName)
    }

    @Test
    fun `onEvent ShortNameChanged updates state`() = runTest {
        viewModel.onEvent(AddEditAddressEvent.ShortNameChanged("NS"))

        assertEquals("NS", viewModel.state.shortName)
    }

    @Test
    fun `nameError updates when address name changes`() = runTest {
        viewModel.onEvent(AddEditAddressEvent.AddressNameChanged(""))
        viewModel.nameError.test {
            assertEquals(ADDRESS_NAME_EMPTY_ERROR, awaitItem())
        }

        viewModel.onEvent(AddEditAddressEvent.AddressNameChanged("A"))
        advanceUntilIdle()
        viewModel.nameError.test {
            assertEquals(ADDRESS_NAME_LENGTH_ERROR, awaitItem())
        }

        advanceUntilIdle()

        viewModel.setAddressId(1)
        viewModel.onEvent(AddEditAddressEvent.AddressNameChanged("New Address"))
        viewModel.onEvent(AddEditAddressEvent.ShortNameChanged("NA"))

        viewModel.nameError.test {
            assertEquals(null, awaitItem())
        }
        viewModel.onEvent(AddEditAddressEvent.CreateOrUpdateAddress)

        advanceUntilIdle()
        val result = repository.getAddressById(1)
        assertEquals("New Address", result.data?.addressName)
        assertEquals(1, result.data?.addressId)

        advanceUntilIdle()

        viewModel.setAddressId(0)
        viewModel.onEvent(AddEditAddressEvent.AddressNameChanged("New Address"))
        advanceUntilIdle()
        viewModel.nameError.test {
            assertEquals(ADDRESS_NAME_ALREADY_EXIST_ERROR, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        advanceUntilIdle()

        viewModel.setAddressId(1)
        viewModel.onEvent(AddEditAddressEvent.AddressNameChanged("New Address"))

        advanceUntilIdle()

        viewModel.nameError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `shortNameError updates when short name changes`() = runTest {
        viewModel.onEvent(AddEditAddressEvent.ShortNameChanged(""))
        advanceUntilIdle()
        viewModel.shortNameError.test {
            assertEquals(ADDRESS_SHORT_NAME_EMPTY_ERROR, awaitItem())
        }

        viewModel.onEvent(AddEditAddressEvent.ShortNameChanged("N"))
        advanceUntilIdle()
        viewModel.shortNameError.test {
            assertEquals(ADDRESS_S_NAME_LESS_THAN_TWO_ERROR, awaitItem())
        }
    }

    @Test
    fun `createOrUpdateAddress with valid input emits success event`() = runTest {
        viewModel.onEvent(AddEditAddressEvent.AddressNameChanged("New Address"))
        viewModel.onEvent(AddEditAddressEvent.ShortNameChanged("NA"))
        viewModel.onEvent(AddEditAddressEvent.CreateOrUpdateAddress)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Address Created Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }
    }

    @Test
    fun `createOrUpdateAddress with invalid input does not emit success event`() = runTest {
        viewModel.setAddressId(1)
        viewModel.onEvent(AddEditAddressEvent.AddressNameChanged("New Address"))
        viewModel.onEvent(AddEditAddressEvent.ShortNameChanged("NA"))
        viewModel.onEvent(AddEditAddressEvent.CreateOrUpdateAddress)

        advanceUntilIdle()

        viewModel.setAddressId(1)
        viewModel.onEvent(AddEditAddressEvent.AddressNameChanged("Updated Address"))
        viewModel.onEvent(AddEditAddressEvent.ShortNameChanged("UA"))
        viewModel.onEvent(AddEditAddressEvent.CreateOrUpdateAddress)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Address Updated Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }
    }
}
