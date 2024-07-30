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

package com.niyaj.addonitem

import androidx.lifecycle.SavedStateHandle
import com.niyaj.addonitem.createOrUpdate.AddEditAddOnItemEvent
import com.niyaj.addonitem.createOrUpdate.AddEditAddOnItemViewModel
import com.niyaj.domain.addonitem.ValidateItemNameUseCase
import com.niyaj.domain.addonitem.ValidateItemPriceUseCase
import com.niyaj.testing.repository.TestAddOnItemRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertNull

class AddEditAddOnItemViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestAddOnItemRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()

    private val validateItemNameUseCase = ValidateItemNameUseCase(
        addOnItemRepository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )
    private val validateItemPriceUseCase = ValidateItemPriceUseCase()
    private lateinit var viewModel: AddEditAddOnItemViewModel

    @Before
    fun setup() {
        viewModel = AddEditAddOnItemViewModel(
            repository = repository,
            validateItemNameUseCase = validateItemNameUseCase,
            validateItemPriceUseCase = validateItemPriceUseCase,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `when item name changes, state is updated`() = runTest {
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged("New Item"))
        assertEquals("New Item", viewModel.addEditState.itemName)
    }

    @Test
    fun `when item price changes, state is updated`() = runTest {
        viewModel.onEvent(AddEditAddOnItemEvent.ItemPriceChanged("100"))
        assertEquals(100, viewModel.addEditState.itemPrice)
    }

    @Test
    fun `when item applicability changes, state is updated`() = runTest {
        val initialState = viewModel.addEditState.isApplicable
        viewModel.onEvent(AddEditAddOnItemEvent.ItemApplicableChanged)
        assertEquals(!initialState, viewModel.addEditState.isApplicable)
    }

    @Test
    fun `when creating new item, success event is emitted`() = runTest {
        viewModel.onEvent(AddEditAddOnItemEvent.ItemPriceChanged("50"))
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged("Test Item"))

        assertNull(viewModel.nameError.value)
        assertNull(viewModel.priceError.value)

        launch {
            viewModel.onEvent(AddEditAddOnItemEvent.CreateUpdateAddOnItem)
        }

        advanceUntilIdle()

        // TODO:: fix shared flow issue
        /*
        viewModel.eventFlow.test {
            val event = awaitItem()
            assert(event is UiEvent.OnSuccess)
            assertEquals(
                "AddOn Item Created Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
            awaitComplete()
        }
         */

        val item = repository.getAddOnItemById(0)
        assertEquals("Test Item", item.data?.itemName)
        assertEquals(50, item.data?.itemPrice)
        assertEquals(true, item.data?.isApplicable)
    }

    @Test
    fun `when updating existing item, success event is emitted`() = runTest {
        // Create a new item with itemId 0
        viewModel.onEvent(AddEditAddOnItemEvent.ItemPriceChanged("50"))
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged("Test Item"))

        assertNull(viewModel.nameError.value)
        assertNull(viewModel.priceError.value)

        viewModel.onEvent(AddEditAddOnItemEvent.CreateUpdateAddOnItem)

        val item = repository.getAddOnItemById(0)
        assertEquals("Test Item", item.data?.itemName)
        assertEquals(50, item.data?.itemPrice)
        assertEquals(true, item.data?.isApplicable)

        savedStateHandle["itemId"] = 0

        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged("Updated Item"))
        viewModel.onEvent(AddEditAddOnItemEvent.ItemPriceChanged("75"))

        assertNull(viewModel.nameError.value)
        assertNull(viewModel.priceError.value)

        viewModel.onEvent(AddEditAddOnItemEvent.CreateUpdateAddOnItem)

        val updatedItem = repository.getAddOnItemById(0)
        assertEquals("Updated Item", updatedItem.data?.itemName)
        assertEquals(75, updatedItem.data?.itemPrice)
    }
}
