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

package com.niyaj.domain

import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_EMPLOYEE_NAME_EMPTY
import com.niyaj.domain.payment.ValidatePaymentEmployeeUseCase
import com.niyaj.testing.util.MainDispatcherRule
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ValidatePaymentEmployeeUseCaseTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    val useCase = ValidatePaymentEmployeeUseCase()

    @Test
    fun `given empty employee when invoke then return error`() {
        val result = useCase(0)
        assert(!result.successful)
        assertEquals(PAYMENT_EMPLOYEE_NAME_EMPTY, result.errorMessage)
    }

    @Test
    fun `given valid employee return success`() {
        val result = useCase(1)
        assert(result.successful)
        assertNull(result.errorMessage)
    }
}
