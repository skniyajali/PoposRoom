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

package com.niyaj.feature.market.measureUnit

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_DIGIT_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_EMPTY_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_LENGTH_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_VALUE_EMPTY_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_VALUE_INVALID
import com.niyaj.domain.market.ValidateUnitNameUseCase
import com.niyaj.domain.market.ValidateUnitValueUseCase
import com.niyaj.feature.market.measureUnit.createOrUpdate.AddEditMeasureUnitEvent
import com.niyaj.feature.market.measureUnit.createOrUpdate.AddEditMeasureUnitViewModel
import com.niyaj.testing.repository.TestMeasureUnitRepository
import com.niyaj.testing.util.MainDispatcherRule
import com.niyaj.testing.util.TestAnalyticsHelper
import com.niyaj.ui.utils.UiEvent
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AddEditMeasureUnitViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AddEditMeasureUnitViewModel

    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()
    private val repository = TestMeasureUnitRepository()

    private val validateUnitName = ValidateUnitNameUseCase(repository, UnconfinedTestDispatcher())
    private val validateUnitValue = ValidateUnitValueUseCase()

    @Before
    fun setup() {
        viewModel = AddEditMeasureUnitViewModel(
            repository = repository,
            validateUnitName = validateUnitName,
            validateUnitValue = validateUnitValue,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun initWithUnitId_populateMeasureUnit() = runTest {
        val item = repository.createTestItem()

        val savedStateHandle = SavedStateHandle()
        savedStateHandle["unitId"] = item.unitId

        viewModel = AddEditMeasureUnitViewModel(
            repository = repository,
            validateUnitName = validateUnitName,
            validateUnitValue = validateUnitValue,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(item.unitValue.toString(), viewModel.state.unitValue)
        assertEquals(item.unitName, viewModel.state.unitName)
    }

    @Test
    fun initWithUnitName_populateUnitName() = runTest {
        val unitName = "unitName"
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["unitName"] = unitName

        viewModel = AddEditMeasureUnitViewModel(
            repository = repository,
            validateUnitName = validateUnitName,
            validateUnitValue = validateUnitValue,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(unitName, viewModel.state.unitName)
    }

    @Test
    fun nameError_updated_whenUnitNameIsEmpty() = runTest {
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged(""))

        viewModel.nameError.test {
            assertEquals(UNIT_NAME_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenUnitNameContainAnyDigit() = runTest {
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged("kg2"))

        viewModel.nameError.test {
            assertEquals(UNIT_NAME_DIGIT_ERROR, awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenUnitNameLessThanTwoCharacter() = runTest {
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged("k"))

        viewModel.nameError.test {
            assertEquals(UNIT_NAME_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenUnitNameAlreadyExists() = runTest {
        val item = repository.createTestItem()
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged(item.unitName))

        viewModel.nameError.test {
            assertEquals(UNIT_NAME_ALREADY_EXIST_ERROR, awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenUnitNameAlreadyExistsWithValidId() = runTest {
        val item = repository.createTestItem()
        viewModel.setUnitId(item.unitId)
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged(item.unitName))

        viewModel.nameError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun nameError_updated_whenUnitNameIsValid() = runTest {
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged("kg"))

        viewModel.nameError.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun valueError_updated_whenUnitValueIsEmpty() = runTest {
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitValueChanged(""))

        viewModel.valueError.test {
            assertEquals(UNIT_VALUE_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun valueError_updated_whenUnitValueIsInvalid() = runTest {
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitValueChanged("-2.0"))

        viewModel.valueError.test {
            assertEquals(UNIT_VALUE_INVALID, awaitItem())
        }
    }

    @Test
    fun valueError_updated_whenUnitValueIsValid() = runTest {
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitValueChanged("0.5"))

        viewModel.valueError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun onEvent_MeasureUnitNameChanged_updateState() = runTest {
        val unitName = "kg"
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged(unitName))

        assertEquals(unitName, viewModel.state.unitName)
    }

    @Test
    fun onEvent_MeasureUnitValueChanged_updateState() = runTest {
        val unitValue = "0.5"
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitValueChanged(unitValue))

        assertEquals(unitValue, viewModel.state.unitValue)
    }

    @Test
    fun onEvent_SaveOrUpdateMeasureUnit_doNothingWhenError() = runTest {
        viewModel.onEvent(AddEditMeasureUnitEvent.SaveOrUpdateMeasureUnit)
    }

    @Test
    fun onEvent_SaveOrUpdateMeasureUnit_addMeasureUnit() = runTest {
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged("kg"))
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitValueChanged("0.5"))
        viewModel.onEvent(AddEditMeasureUnitEvent.SaveOrUpdateMeasureUnit)

        viewModel.eventFlow.test {
            assertEquals(
                UiEvent.OnSuccess("Item Created successfully."),
                awaitItem(),
            )
        }

        val updatedItem = repository.getMeasureUnitById(0)
        assertNotNull(updatedItem.data)
        assertEquals("kg", updatedItem.data!!.unitName)
        assertEquals(0.5, updatedItem.data!!.unitValue)
    }

    @Test
    fun onEvent_SaveOrUpdateMeasureUnit_updateMeasureUnit() = runTest {
        viewModel.setUnitId(1)
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged("kg"))
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitValueChanged("0.5"))
        viewModel.onEvent(AddEditMeasureUnitEvent.SaveOrUpdateMeasureUnit)

        val createdItem = repository.getMeasureUnitById(1)
        assertNotNull(createdItem.data)
        assertEquals("kg", createdItem.data!!.unitName)
        assertEquals(0.5, createdItem.data!!.unitValue)

        viewModel.setUnitId(1)
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged("li"))
        viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitValueChanged("1.0"))
        viewModel.onEvent(AddEditMeasureUnitEvent.SaveOrUpdateMeasureUnit)

        viewModel.eventFlow.test {
            assertEquals(
                UiEvent.OnSuccess("Item Updated successfully."),
                awaitItem(),
            )
        }

        val updatedItem = repository.getMeasureUnitById(1)
        assertNotNull(updatedItem.data)
        assertEquals("li", updatedItem.data!!.unitName)
        assertEquals(1.0, updatedItem.data!!.unitValue)
    }
}
