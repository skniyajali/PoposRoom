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

import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_MEASURE_EMPTY_ERROR
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import kotlin.test.Test

class ValidateMeasureUnitUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateMeasureUnitUseCase()

    @Test
    fun `given invalid unit id when invoke then return error`() {
        val result = useCase(0)

        assert(!result.successful)
        assertEquals(MARKET_ITEM_MEASURE_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `given non-empty unit id when invoke then return success`() {
        val result = useCase(1)

        assert(result.successful)
        assertEquals(null, result.errorMessage)
    }
}
