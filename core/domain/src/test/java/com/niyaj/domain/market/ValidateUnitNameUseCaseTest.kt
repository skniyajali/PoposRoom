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

import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_DIGIT_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_EMPTY_ERROR
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_LENGTH_ERROR
import com.niyaj.testing.repository.TestMeasureUnitRepository
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import kotlin.test.Test

class ValidateUnitNameUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    private val repository = TestMeasureUnitRepository()
    private val useCase = ValidateUnitNameUseCase(
        repository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `validate with empty unit name return error`() = runTest {
        val result = useCase("")

        assert(result.successful.not())
        assertEquals(UNIT_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `validate with unit name shorter than 2 characters return error`() = runTest {
        val result = useCase("a")

        assert(result.successful.not())
        assertEquals(UNIT_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `validate with unit name with digit return error`() = runTest {
        val result = useCase("kg1")

        assert(result.successful.not())
        assertEquals(UNIT_NAME_DIGIT_ERROR, result.errorMessage)
    }

    @Test
    fun `validate with unit name already exists return error`() = runTest {
        val unit = repository.createTestItem()
        val result = useCase(unit.unitName)

        assert(result.successful.not())
    }

    @Test
    fun `validate with unit name and id already exists return success`() = runTest {
        val unit = repository.createTestItem()
        val result = useCase(unit.unitName, unitId = unit.unitId)

        assert(result.successful)
        assertEquals(null, result.errorMessage)
    }

    @Test
    fun `validate with valid unit name return success`() = runTest {
        val result = useCase("kg")

        assert(result.successful)
        assertEquals(null, result.errorMessage)
    }
}
