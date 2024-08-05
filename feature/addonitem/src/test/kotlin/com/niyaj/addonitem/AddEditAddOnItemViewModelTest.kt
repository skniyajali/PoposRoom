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
import app.cash.turbine.test
import com.niyaj.addonitem.createOrUpdate.AddEditAddOnItemEvent
import com.niyaj.addonitem.createOrUpdate.AddEditAddOnItemViewModel
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_DIGIT_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_EMPTY_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_LENGTH_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_LESS_THAN_FIVE_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_WHITELIST_ITEM
import com.niyaj.domain.addonitem.ValidateItemNameUseCase
import com.niyaj.domain.addonitem.ValidateItemPriceUseCase
import com.niyaj.model.AddOnItem
import com.niyaj.testing.repository.TestAddOnItemRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.utils.UiEvent
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
    fun `init with itemId loads addOnItem`() = runTest {
        val item = AddOnItem(
            itemId = 1,
            itemName = "Test Item",
            itemPrice = 10,
            isApplicable = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = null,
        )
        repository.upsertAddOnItem(item)

        val savedStateHandle = SavedStateHandle()
        savedStateHandle["itemId"] = 1

        viewModel = AddEditAddOnItemViewModel(
            repository = repository,
            validateItemNameUseCase = validateItemNameUseCase,
            validateItemPriceUseCase = validateItemPriceUseCase,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(item.itemName, viewModel.addEditState.itemName)
        assertEquals(item.itemPrice, viewModel.addEditState.itemPrice)
        assertEquals(item.isApplicable, viewModel.addEditState.isApplicable)
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
    fun `when item name is empty, name error is set`() = runTest {
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged(""))

        viewModel.nameError.test {
            assertEquals(ADDON_NAME_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `when item name is less than 5 characters, name error is set`() = runTest {
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged("Test"))

        viewModel.nameError.test {
            assertEquals(ADDON_NAME_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `when item name contains digit, name error is set`() = runTest {
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged("Test1"))

        viewModel.nameError.test {
            assertEquals(ADDON_NAME_DIGIT_ERROR, awaitItem())
        }
    }

    @Test
    fun `when item name contains whitelist items with digit, name error is null`() = runTest {
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged(ADDON_WHITELIST_ITEM + "1"))

        viewModel.nameError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `when item name already exists, name error is set`() = runTest {
        val item = AddOnItem(
            itemId = 1,
            itemName = "Test Item",
            itemPrice = 10,
            isApplicable = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = null,
        )
        repository.upsertAddOnItem(item)

        viewModel.setAddOnItemId(0)
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged("Test Item"))

        advanceUntilIdle()

        viewModel.nameError.test {
            assertEquals(ADDON_NAME_ALREADY_EXIST_ERROR, awaitItem())
        }
    }

    @Test
    fun `when item name already exists but updating, name error is null`() = runTest {
        val item = AddOnItem(
            itemId = 1,
            itemName = "Test Item",
            itemPrice = 10,
            isApplicable = true,
            createdAt = System.currentTimeMillis(),
            updatedAt = null,
        )
        repository.upsertAddOnItem(item)

        viewModel.setAddOnItemId(1)
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged("Test Item"))

        advanceUntilIdle()

        viewModel.nameError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `when item price is empty, price error is set`() = runTest {
        viewModel.onEvent(AddEditAddOnItemEvent.ItemPriceChanged(""))

        viewModel.priceError.test {
            assertEquals(ADDON_PRICE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `when item price is less than 5, price error is set`() = runTest {
        viewModel.onEvent(AddEditAddOnItemEvent.ItemPriceChanged("4"))

        viewModel.priceError.test {
            assertEquals(ADDON_PRICE_LESS_THAN_FIVE_ERROR, awaitItem())
        }
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

        viewModel.eventFlow.test {
            val event = awaitItem()
            assert(event is UiEvent.OnSuccess)
            assertEquals(
                "AddOn Item Created Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val item = repository.getAddOnItemById(0)
        assertEquals("Test Item", item.data?.itemName)
        assertEquals(50, item.data?.itemPrice)
        assertEquals(true, item.data?.isApplicable)
    }

    @Test
    fun `when updating existing item, success event is emitted`() = runTest {
        // Create a new item with itemId 1
        viewModel.setAddOnItemId(1)
        viewModel.onEvent(AddEditAddOnItemEvent.ItemPriceChanged("50"))
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged("Test Item"))

        assertNull(viewModel.nameError.value)
        assertNull(viewModel.priceError.value)

        viewModel.onEvent(AddEditAddOnItemEvent.CreateUpdateAddOnItem)

        val item = repository.getAddOnItemById(1)
        assertEquals("Test Item", item.data?.itemName)
        assertEquals(50, item.data?.itemPrice)
        assertEquals(true, item.data?.isApplicable)

        viewModel.setAddOnItemId(1)
        viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged("Updated Item"))
        viewModel.onEvent(AddEditAddOnItemEvent.ItemPriceChanged("75"))

        assertNull(viewModel.nameError.value)
        assertNull(viewModel.priceError.value)

        viewModel.onEvent(AddEditAddOnItemEvent.CreateUpdateAddOnItem)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assert(event is UiEvent.OnSuccess)
            assertEquals(
                "AddOn Item Updated Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val updatedItem = repository.getAddOnItemById(1)
        assertEquals("Updated Item", updatedItem.data?.itemName)
        assertEquals(75, updatedItem.data?.itemPrice)
    }
}
