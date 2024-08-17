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

package com.niyaj.domain.payment

import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_GIVEN_DATE_EMPTY
import com.niyaj.common.utils.getStartTime
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import kotlin.test.Test

class ValidateGivenDateUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidateGivenDateUseCase()

    @Test
    fun `given empty date when invoke then return error`() {
        val result = useCase("")
        assertFalse(result.successful)
        assertEquals(PAYMENT_GIVEN_DATE_EMPTY, result.errorMessage)
    }

    @Test
    fun `given valid date return success`() {
        val result = useCase(getStartTime)
        assert(result.successful)
    }
}
