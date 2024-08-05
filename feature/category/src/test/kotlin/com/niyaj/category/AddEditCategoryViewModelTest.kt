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

package com.niyaj.category

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.niyaj.category.createOrUpdate.AddEditCategoryEvent
import com.niyaj.category.createOrUpdate.AddEditCategoryViewModel
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_EMPTY_ERROR
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_LENGTH_ERROR
import com.niyaj.domain.category.ValidateCategoryNameUseCase
import com.niyaj.testing.repository.TestCategoryRepository
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
import kotlin.test.assertFalse
import kotlin.test.assertNull

class AddEditCategoryViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestCategoryRepository()
    private val analyticsHelper = TestAnalyticsHelper()
    private val savedStateHandle = SavedStateHandle()

    private val useCase = ValidateCategoryNameUseCase(
        repository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )
    private lateinit var viewModel: AddEditCategoryViewModel

    @Before
    fun setup() {
        viewModel = AddEditCategoryViewModel(
            categoryRepository = repository,
            validateCategoryName = useCase,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `init with categoryId loads address`() = runTest {
        val category = repository.createTestCategory()

        val savedStateHandle = SavedStateHandle()
        savedStateHandle["categoryId"] = category.categoryId

        viewModel = AddEditCategoryViewModel(
            categoryRepository = repository,
            validateCategoryName = useCase,
            analyticsHelper = analyticsHelper,
            savedStateHandle = savedStateHandle,
        )

        assertEquals(category.categoryName, viewModel.state.categoryName)
        assertEquals(category.isAvailable, viewModel.state.isAvailable)
    }

    @Test
    fun `onEvent CategoryNameChanged updates state`() = runTest {
        viewModel.onEvent(AddEditCategoryEvent.CategoryNameChanged("New Category"))

        assertEquals("New Category", viewModel.state.categoryName)
    }

    @Test
    fun `onEvent CategoryAvailabilityChanged updates state`() = runTest {
        viewModel.onEvent(AddEditCategoryEvent.CategoryAvailabilityChanged)
        assert(!viewModel.state.isAvailable)

        viewModel.onEvent(AddEditCategoryEvent.CategoryAvailabilityChanged)
        assert(viewModel.state.isAvailable)
    }

    @Test
    fun `nameError updates when category name is empty`() = runTest {
        viewModel.onEvent(AddEditCategoryEvent.CategoryNameChanged(""))

        viewModel.nameError.test {
            assertEquals(CATEGORY_NAME_EMPTY_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when category name is shorter`() = runTest {
        viewModel.onEvent(AddEditCategoryEvent.CategoryNameChanged("TC"))

        viewModel.nameError.test {
            assertEquals(CATEGORY_NAME_LENGTH_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when category name is not unique`() = runTest {
        val category = repository.createTestCategory()
        viewModel.onEvent(AddEditCategoryEvent.CategoryNameChanged(category.categoryName))

        viewModel.nameError.test {
            assertEquals(CATEGORY_NAME_ALREADY_EXIST_ERROR, awaitItem())
        }
    }

    @Test
    fun `nameError updates when category name and id is same`() = runTest {
        val category = repository.createTestCategory()
        viewModel.setCategoryId(category.categoryId)

        viewModel.onEvent(AddEditCategoryEvent.CategoryNameChanged(category.categoryName))

        viewModel.nameError.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `create a new category with valid input emits success event`() = runTest {
        viewModel.onEvent(AddEditCategoryEvent.CategoryNameChanged("New Category"))
        viewModel.onEvent(AddEditCategoryEvent.CategoryAvailabilityChanged)
        viewModel.onEvent(AddEditCategoryEvent.CreateUpdateAddEditCategory)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Category Created Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getCategoryById(0).data
        assertNotNull(data)
        assertEquals("New Category", data?.categoryName)
        assertFalse(data?.isAvailable!!)
    }

    @Test
    fun `update a category with valid input emit success event`() = runTest {
        viewModel.setCategoryId(1)
        viewModel.onEvent(AddEditCategoryEvent.CategoryNameChanged("New Category"))
        viewModel.onEvent(AddEditCategoryEvent.CategoryAvailabilityChanged)
        viewModel.onEvent(AddEditCategoryEvent.CreateUpdateAddEditCategory)

        advanceUntilIdle()

        viewModel.onEvent(AddEditCategoryEvent.CategoryNameChanged("Updated Category"))
        viewModel.onEvent(AddEditCategoryEvent.CategoryAvailabilityChanged)
        viewModel.onEvent(AddEditCategoryEvent.CreateUpdateAddEditCategory)

        advanceUntilIdle()

        viewModel.eventFlow.test {
            val event = awaitItem()
            assertTrue(event is UiEvent.OnSuccess)
            assertEquals(
                "Category Updated Successfully.",
                (event as UiEvent.OnSuccess).successMessage,
            )
        }

        val data = repository.getCategoryById(1).data
        assertNotNull(data)
        assertEquals("Updated Category", data?.categoryName)
        assertFalse(data?.isAvailable!!)
    }
}
