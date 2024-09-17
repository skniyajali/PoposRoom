/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.market.marketType

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.common.tags.MarketTypeTags.LIST_NAME_LEAST
import com.niyaj.common.tags.MarketTypeTags.LIST_TYPES_ERROR
import com.niyaj.common.tags.MarketTypeTags.TYPE_NAME_EXISTS
import com.niyaj.common.tags.MarketTypeTags.TYPE_NAME_IS_REQUIRED
import com.niyaj.common.tags.MarketTypeTags.TYPE_NAME_LEAST
import com.niyaj.domain.market.ValidateListTypeUseCase
import com.niyaj.domain.market.ValidateListTypesUseCase
import com.niyaj.domain.market.ValidateTypeNameUseCase
import com.niyaj.market.marketType.createOrUpdate.AddEditMarketTypeEvent
import com.niyaj.market.marketType.createOrUpdate.AddEditMarketTypeViewModel
import com.niyaj.testing.repository.TestMarketTypeRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AddEditMarketTypeViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestMarketTypeRepository()
    private val analyticsHelper = TestAnalyticsHelper()

    private val validateTypeName = ValidateTypeNameUseCase(repository, UnconfinedTestDispatcher())
    private val validateListType = ValidateListTypeUseCase()
    private val validateListTypes = ValidateListTypesUseCase()
    private val savedStateHandle = SavedStateHandle()

    private lateinit var viewModel: AddEditMarketTypeViewModel

    @Before
    fun setup() {
        viewModel = AddEditMarketTypeViewModel(
            repository = repository,
            validateTypeName = validateTypeName,
            validateListType = validateListType,
            validateListTypes = validateListTypes,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun initWithTypeId_shouldPopulate_marketType() {
        val item = repository.createTestItem()
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["typeId"] = item.typeId

        viewModel = AddEditMarketTypeViewModel(
            repository = repository,
            validateTypeName = validateTypeName,
            validateListType = validateListType,
            validateListTypes = validateListTypes,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(item.typeName, viewModel.state.typeName)
        assertEquals(item.typeDesc, viewModel.state.typeDesc)
        assertEquals(item.supplierId, viewModel.state.supplierId)
        assertEquals(item.listTypes, viewModel.selectedTypes)
    }

    @Test
    fun initWithTypeName_shouldPopulate_marketTypeName() {
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["typeName"] = "Test"

        viewModel = AddEditMarketTypeViewModel(
            repository = repository,
            validateTypeName = validateTypeName,
            validateListType = validateListType,
            validateListTypes = validateListTypes,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals("Test", viewModel.state.typeName)
    }

    @Test
    fun typeNameError_updated_whenTypeNameIsEmpty() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.TypeNameChanged(""))

        viewModel.typeNameError.test {
            assertEquals(TYPE_NAME_IS_REQUIRED, awaitItem())
        }
    }

    @Test
    fun typeNameError_updated_whenTypeNameLengthIsLessThan3() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.TypeNameChanged("te"))

        viewModel.typeNameError.test {
            assertEquals(TYPE_NAME_LEAST, awaitItem())
        }
    }

    @Test
    fun typeNameError_updated_whenTypeNameAlreadyExists() = runTest {
        val item = repository.createTestItem()
        viewModel.onEvent(AddEditMarketTypeEvent.TypeNameChanged(item.typeName))

        viewModel.typeNameError.test {
            assertEquals(TYPE_NAME_EXISTS, awaitItem())
        }
    }

    @Test
    fun typeNameError_updated_whenTypeNameAlreadyExistsWithId() = runTest {
        val item = repository.createTestItem()

        viewModel.setTypeId(item.typeId)
        viewModel.onEvent(AddEditMarketTypeEvent.TypeNameChanged(item.typeName))

        viewModel.typeNameError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun typeNameError_updated_whenTypeNameIsValid() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.TypeNameChanged("Test"))

        viewModel.typeNameError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun listNameError_updated_whenListNameIsNotEmptyAndLengthIsLessThan4() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.ListTypeChanged("Lis"))

        viewModel.listNameError.test {
            assertEquals(LIST_NAME_LEAST, awaitItem())
        }
    }

    @Test
    fun listNameError_updated_whenListNameIsEmpty() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.ListTypeChanged(""))

        viewModel.listNameError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun listNameError_updated_whenListNameIsValid() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.ListTypeChanged("List"))

        viewModel.listNameError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun listTypesError_updated_whenListTypesIsEmpty() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.OnSelectListType("NEEDED"))

        viewModel.listTypesError.test {
            assertEquals(LIST_TYPES_ERROR, awaitItem())
        }
    }

    @Test
    fun listTypesError_updated_whenListTypesIsNotEmpty() = runTest {
        viewModel.listTypesError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun onEvent_TypeNameChanged_shouldUpdate_state() {
        viewModel.onEvent(AddEditMarketTypeEvent.TypeNameChanged("Test"))

        assertEquals("Test", viewModel.state.typeName)
    }

    @Test
    fun onEvent_TypeDescChanged_shouldUpdate_state() {
        viewModel.onEvent(AddEditMarketTypeEvent.TypeDescChanged("Test"))

        assertEquals("Test", viewModel.state.typeDesc)
    }

    @Test
    fun onEvent_TypeDescChanged_shouldUpdateNullState() {
        viewModel.onEvent(AddEditMarketTypeEvent.TypeDescChanged(""))

        assertEquals(null, viewModel.state.typeDesc)
    }

    @Test
    fun onEvent_SupplierIdChanged_shouldUpdate_state() {
        viewModel.onEvent(AddEditMarketTypeEvent.SupplierIdChanged(1))

        assertEquals(1, viewModel.state.supplierId)
    }

    @Test
    fun onEvent_ListTypeChanged_shouldUpdate_state() {
        viewModel.onEvent(AddEditMarketTypeEvent.ListTypeChanged("Test"))

        assertEquals("TEST", viewModel.state.listType)
    }

    @Test
    fun onEvent_OnSelectListType_shouldUpdate_selectedTypes() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.OnSelectListType("TEST"))

        assertEquals(listOf("NEEDED", "TEST"), viewModel.selectedTypes)
        assert(viewModel.listTypes.contains("TEST"))
    }

    @Test
    fun onEvent_OnSelectListType_shouldDeselectSelectedTypes() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.OnSelectListType("NEEDED"))

        assert(viewModel.selectedTypes.isEmpty())
    }

    @Test
    fun onEvent_OnSelectListType_shouldClearListType() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.ListTypeChanged("TEST"))
        viewModel.onEvent(AddEditMarketTypeEvent.OnSelectListType("TEST"))

        assertEquals(listOf("NEEDED", "TEST"), viewModel.selectedTypes)
        assertEquals("", viewModel.state.listType)
        assert(viewModel.listTypes.contains("TEST"))
    }

    @Test
    fun onEvent_SaveMarketType_shouldCreateMarketType() = runTest {
        viewModel.onEvent(AddEditMarketTypeEvent.TypeNameChanged("Test"))
        viewModel.onEvent(AddEditMarketTypeEvent.TypeDescChanged("Info"))
        viewModel.onEvent(AddEditMarketTypeEvent.SaveMarketType)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            assertEquals(
                UiEvent.OnSuccess("Market type created successfully"),
                awaitItem(),
            )
        }

        val result = repository.getMarketTypeById(0)
        assertNotNull(result)
        assertEquals("Test", result.typeName)
        assertEquals("Info", result.typeDesc)
        assertEquals(listOf("NEEDED"), result.listTypes)
        assertEquals(0, result.supplierId)
    }

    @Test
    fun onEvent_SaveMarketType_shouldUpdateMarketType() = runTest {
        viewModel.setTypeId(1)
        viewModel.onEvent(AddEditMarketTypeEvent.TypeNameChanged("Test"))
        viewModel.onEvent(AddEditMarketTypeEvent.TypeDescChanged("Info"))
        viewModel.onEvent(AddEditMarketTypeEvent.SaveMarketType)

        advanceUntilIdle()

        viewModel.onEvent(AddEditMarketTypeEvent.TypeDescChanged("Updated"))
        viewModel.onEvent(AddEditMarketTypeEvent.OnSelectListType("IN-STOCK"))
        viewModel.onEvent(AddEditMarketTypeEvent.SupplierIdChanged(1))
        viewModel.onEvent(AddEditMarketTypeEvent.SaveMarketType)

        val result = repository.getMarketTypeById(1)
        assertNotNull(result)
        assertEquals("Test", result.typeName)
        assertEquals("Updated", result.typeDesc)
        assertEquals(listOf("NEEDED", "IN-STOCK"), result.listTypes)
        assertEquals(1, result.supplierId)

        viewModel.eventFlow.test {
            assertEquals(
                UiEvent.OnSuccess("Market type updated successfully"),
                awaitItem(),
            )
        }
    }
}