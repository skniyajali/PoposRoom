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

package com.niyaj.charges

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.charges.createOrUpdate.AddEditChargesEvent
import com.niyaj.charges.createOrUpdate.AddEditChargesViewModel
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_DIGIT_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_LENGTH_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_PRICE_LESS_THAN_TEN_ERROR
import com.niyaj.domain.charges.ValidateChargesNameUseCase
import com.niyaj.domain.charges.ValidateChargesPriceUseCase
import com.niyaj.testing.repository.TestChargesRepository
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

class AddEditChargesViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestChargesRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()

    private val useCase = ValidateChargesNameUseCase(
        chargesRepository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )
    private val priceUseCase = ValidateChargesPriceUseCase()

    private lateinit var viewModel: AddEditChargesViewModel

    @Before
    fun setup() {
        viewModel = AddEditChargesViewModel(
            chargesRepository = repository,
            validateChargesName = useCase,
            validateChargesPrice = priceUseCase,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `init with chargesId loads charges`() = runTest {
        val charge = repository.createTestCharges()

        val savedStateHandle = SavedStateHandle()
        savedStateHandle["chargesId"] = charge.chargesId

        viewModel = AddEditChargesViewModel(
            chargesRepository = repository,
            validateChargesName = useCase,
            validateChargesPrice = priceUseCase,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(charge.chargesName, viewModel.state.chargesName)
        assertEquals(charge.chargesPrice, viewModel.state.chargesPrice)
        assertEquals(charge.isApplicable, viewModel.state.chargesApplicable)
    }

    @Test
    fun `when ChargesNameChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged("New Charge"))
        assertEquals("New Charge", viewModel.state.chargesName)
    }

    @Test
    fun `when ChargesPriceChanged event is received, state is updated`() {
        viewModel.onEvent(AddEditChargesEvent.ChargesPriceChanged("150"))
        assertEquals(150, viewModel.state.chargesPrice)
    }

    @Test
    fun `when ChargesApplicableChanged event is received, state is toggled`() {
        val initialState = viewModel.state.chargesApplicable
        viewModel.onEvent(AddEditChargesEvent.ChargesApplicableChanged)
        assertEquals(!initialState, viewModel.state.chargesApplicable)
    }

    @Test
    fun `nameError updates when charges name is empty`() = runTest {
        viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged(""))

        viewModel.nameError.test {
            assertEquals(CHARGES_NAME_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when charges name is shorter`() = runTest {
        viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged("TC"))

        viewModel.nameError.test {
            assertEquals(CHARGES_NAME_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when charges name contains any digit`() = runTest {
        viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged("TEST3"))

        viewModel.nameError.test {
            assertEquals(CHARGES_NAME_DIGIT_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when charges name is not unique`() = runTest {
        val charges = repository.createTestCharges()
        viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged(charges.chargesName))

        viewModel.nameError.test {
            assertEquals(CHARGES_NAME_ALREADY_EXIST_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when charges name and id is same`() = runTest {
        val charges = repository.createTestCharges()
        viewModel.setChargesId(charges.chargesId)

        viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged(charges.chargesName))

        viewModel.nameError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `priceError updates when charges price is empty or zero`() = runTest {
        viewModel.onEvent(AddEditChargesEvent.ChargesPriceChanged(""))

        viewModel.priceError.test {
            assertEquals(CHARGES_PRICE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `priceError updates when charges price is less than ten`() = runTest {
        viewModel.onEvent(AddEditChargesEvent.ChargesPriceChanged("8"))

        viewModel.priceError.test {
            assertEquals(CHARGES_PRICE_LESS_THAN_TEN_ERROR, awaitItem())
        }
    }

    @Test
    fun `priceError updates when charges price is valid`() = runTest {
        viewModel.onEvent(AddEditChargesEvent.ChargesPriceChanged("10"))

        viewModel.priceError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `create a new charges with valid input emits success event`() = runTest {
        viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged("New Charges"))
        viewModel.onEvent(AddEditChargesEvent.ChargesPriceChanged("10"))
        viewModel.onEvent(AddEditChargesEvent.ChargesApplicableChanged)
        viewModel.onEvent(AddEditChargesEvent.CreateOrUpdateCharges)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Charges Created Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getChargesById(0).data
        assertNotNull(data)
        assertEquals("New Charges", data?.chargesName)
        assertEquals(10, data?.chargesPrice)
        assertTrue(data?.isApplicable!!)
    }

    @Test
    fun `update a charges with valid input emit success event`() = runTest {
        viewModel.setChargesId(1)
        viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged("New Charges"))
        viewModel.onEvent(AddEditChargesEvent.ChargesPriceChanged("10"))
        viewModel.onEvent(AddEditChargesEvent.ChargesApplicableChanged)
        viewModel.onEvent(AddEditChargesEvent.CreateOrUpdateCharges)

        advanceUntilIdle()

        viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged("Updated Charges"))
        viewModel.onEvent(AddEditChargesEvent.ChargesPriceChanged("20"))
        viewModel.onEvent(AddEditChargesEvent.ChargesApplicableChanged)
        viewModel.onEvent(AddEditChargesEvent.CreateOrUpdateCharges)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Charges Updated Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getChargesById(1).data
        assertNotNull(data)
        assertEquals("Updated Charges", data?.chargesName)
        assertEquals(20, data?.chargesPrice)
        assertTrue(data?.isApplicable!!)
    }
}
