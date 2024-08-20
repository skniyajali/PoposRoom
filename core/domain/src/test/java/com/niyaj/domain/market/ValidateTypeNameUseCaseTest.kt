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

import com.niyaj.common.tags.MarketTypeTags.TYPE_NAME_EXISTS
import com.niyaj.common.tags.MarketTypeTags.TYPE_NAME_IS_REQUIRED
import com.niyaj.common.tags.MarketTypeTags.TYPE_NAME_LEAST
import com.niyaj.testing.repository.TestMarketTypeRepository
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import kotlin.test.Test

class ValidateTypeNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = TestMarketTypeRepository()
    private val useCase = ValidateTypeNameUseCase(
        repository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `validate with empty type name return error`() = runTest {
        val result = useCase("")

        assert(result.successful.not())
        assertEquals(TYPE_NAME_IS_REQUIRED, result.errorMessage)
    }

    @Test
    fun `validate with type name less than 3 characters return error`() = runTest {
        val result = useCase("Te")

        assert(result.successful.not())
        assertEquals(TYPE_NAME_LEAST, result.errorMessage)
    }

    @Test
    fun `validate with valid type name return success`() = runTest {
        val result = useCase("Test")

        assert(result.successful)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun `validate with existing type name return error`() = runTest {
        val type = repository.createTestItem()
        val result = useCase(type.typeName)

        assert(result.successful.not())
        assertEquals(TYPE_NAME_EXISTS, result.errorMessage)
    }

    @Test
    fun `validate with existing type name and typeId return success`() = runTest {
        val type = repository.createTestItem()
        val result = useCase(type.typeName, type.typeId)

        assert(result.successful)
        assertEquals(null, result.errorMessage)
    }
}
