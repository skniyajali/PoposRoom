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

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.Customer
import com.niyaj.ui.parameterProvider.CustomerPreviewData.customerList

class CustomerPreviewParameter : PreviewParameterProvider<List<Customer>> {
    override val values: Sequence<List<Customer>>
        get() = sequenceOf(
            customerList
        )
}


object CustomerPreviewData {
    val customerList = listOf(
        Customer(
            customerId = 1,
            customerPhone = "1234567890",
            customerName = "John Doe",
            customerEmail = "john.doe@example.com",
        ),
        Customer(
            customerId = 2,
            customerPhone = "9876543210",
            customerName = "Jane Smith",
            customerEmail = "jane.smith@example.com",
        ),
        Customer(
            customerId = 3,
            customerPhone = "5555555555",
            customerName = "Michael Johnson",
            customerEmail = "michael.johnson@example.com",
        ),
        Customer(
            customerId = 4,
            customerPhone = "1112223333",
            customerName = "Emily Davis",
            customerEmail = "emily.davis@example.com",
        ),
        Customer(
            customerId = 5,
            customerPhone = "4445556666",
            customerName = "David Wilson",
            customerEmail = "david.wilson@example.com",
        ),
        Customer(
            customerId = 6,
            customerPhone = "7778889999",
            customerName = "Sophia Martinez",
            customerEmail = "sophia.martinez@example.com",
        ),
        Customer(
            customerId = 7,
            customerPhone = "0101010101",
            customerName = "William Thompson",
            customerEmail = "william.thompson@example.com",
        ),
        Customer(
            customerId = 8,
            customerPhone = "2222222222",
            customerName = "Olivia Anderson",
            customerEmail = "olivia.anderson@example.com",
        ),
        Customer(
            customerId = 9,
            customerPhone = "3333333333",
            customerName = "Jacob Brown",
            customerEmail = "jacob.brown@example.com",
        ),
        Customer(
            customerId = 10,
            customerPhone = "4444444444",
            customerName = "Avery Taylor",
            customerEmail = "avery.taylor@example.com",
        ),
    )
}