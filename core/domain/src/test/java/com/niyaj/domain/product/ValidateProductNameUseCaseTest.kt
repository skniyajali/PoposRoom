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

package com.niyaj.domain.product

import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_LENGTH_ERROR
import com.niyaj.testing.repository.TestProductRepository
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidateProductNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    private val repository = TestProductRepository()

    private val useCase = ValidateProductNameUseCase(
        repository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `when product name is empty then return error`() = runTest {
        val result = useCase("")
        assert(result.successful.not())
        assertEquals(PRODUCT_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `when product name is not empty then return success`() = runTest {
        val result = useCase("Product Name")
        assert(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `when product name is less than 4 then return error`() = runTest {
        val result = useCase("Pro")
        assert(result.successful.not())
        assertEquals(PRODUCT_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `when product name already exist return error`() = runTest {
        val product = repository.createTestProduct()
        val result = useCase(product.productName)
        assert(result.successful.not())
        assertEquals(PRODUCT_NAME_ALREADY_EXIST_ERROR, result.errorMessage)
    }

    @Test
    fun `when product name already exist with valid id return success`() = runTest {
        val product = repository.createTestProduct()
        val result = useCase(product.productName, product.productId)
        assert(result.successful)
        assertNull(result.errorMessage)
    }
}
