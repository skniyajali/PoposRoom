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

package com.niyaj.domain.market

import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_DIGIT_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_EMPTY_ERROR
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_LENGTH_ERROR
import com.niyaj.testing.repository.TestMarketItemRepository
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertNull

class ValidateMarketItemNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestMarketItemRepository()
    private val useCase = ValidateMarketItemNameUseCase(
        repository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `validate with empty item name return error`() = runTest {
        val result = useCase("", null)

        assert(result.successful.not())
        assertEquals(MARKET_ITEM_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `validate with item name shorter than 3 characters return error`() = runTest {
        val result = useCase("Ab", null)

        assert(result.successful.not())
        assertEquals(MARKET_ITEM_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `validate with invalid item name return error`() = runTest {
        val result = useCase("Test123", null)

        assert(result.successful.not())
        assertEquals(MARKET_ITEM_NAME_DIGIT_ERROR, result.errorMessage)
    }

    @Test
    fun `validate with item name already exists return error`() = runTest {
        val item = repository.createTestItem()
        val result = useCase(item.itemName, null)

        assert(result.successful.not())
        assertEquals(MARKET_ITEM_NAME_ALREADY_EXIST_ERROR, result.errorMessage)
    }

    @Test
    fun `validate with item name with itemId already exists return success`() = runTest {
        val item = repository.createTestItem()
        val result = useCase(item.itemName, item.itemId)

        assert(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validate with valid name return success`() = runTest {
        val result = useCase("Test Item", null)

        assert(result.successful)
        assertNull(result.errorMessage)
    }
}
