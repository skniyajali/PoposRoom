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

package com.niyaj.domain.address

import com.niyaj.common.tags.AddressTestTags
import com.niyaj.testing.repository.TestAddressRepository
import com.niyaj.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import kotlin.test.Test

class ValidateAddressNameUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()
    private val repository = TestAddressRepository()

    private val validateAddressNameUseCase = ValidateAddressNameUseCase(
        repository = repository,
        ioDispatcher = UnconfinedTestDispatcher(),
    )

    @Test
    fun `empty address name returns error`() = runTest {
        val result = validateAddressNameUseCase("")
        assertFalse(result.successful)
        assertEquals(AddressTestTags.ADDRESS_NAME_EMPTY_ERROR, result.errorMessage)
    }

    @Test
    fun `address name shorter than 5 characters returns error`() = runTest {
        val result = validateAddressNameUseCase("Ab")
        assertFalse(result.successful)
        assertEquals(AddressTestTags.ADDRESS_NAME_LENGTH_ERROR, result.errorMessage)
    }

    @Test
    fun `address name already exists returns error`() = runTest {
        val address = repository.createTestAddress()
        val result = validateAddressNameUseCase(address.addressName)
        assertFalse(result.successful)
        assertEquals(AddressTestTags.ADDRESS_NAME_ALREADY_EXIST_ERROR, result.errorMessage)
    }

    @Test
    fun `valid address name returns successful result`() = runTest {
        val result = validateAddressNameUseCase("Valid Address")
        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }

    @Test
    fun `valid address name with addressId returns successful result`() = runTest {
        val address = repository.createTestAddress()
        val result = validateAddressNameUseCase(address.addressName, address.addressId)
        assertTrue(result.successful)
        assertNull(result.errorMessage)
    }
}
