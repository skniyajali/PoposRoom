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
import com.niyaj.model.CustomerWiseOrder
import com.niyaj.model.TotalOrderDetails
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CustomerPreviewData.customerList
import com.niyaj.ui.parameterProvider.CustomerPreviewData.customerWiseOrders

class CustomersPreviewParameter : PreviewParameterProvider<UiState<List<Customer>>> {
    override val values: Sequence<UiState<List<Customer>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(customerList),
        )
}

class CustomerPreviewParameter : PreviewParameterProvider<UiState<Customer>> {
    override val values: Sequence<UiState<Customer>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(customerList.last()),
        )
}

class CustomerWiseOrderPreviewParameter : PreviewParameterProvider<UiState<List<CustomerWiseOrder>>> {
    override val values: Sequence<UiState<List<CustomerWiseOrder>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(customerWiseOrders),
        )
}

object CustomerPreviewData {

    val customerList = listOf(
        Customer(
            customerId = 1,
            customerPhone = "1234567890",
            customerName = "John Doe",
            customerEmail = "john@example.com",
        ),
        Customer(
            customerId = 2,
            customerPhone = "9876543210",
            customerName = "Jane Smith",
            customerEmail = "jane@example.com",
        ),
        Customer(
            customerId = 3,
            customerPhone = "5555555555",
            customerName = "Michael Johnson",
            customerEmail = "michael@example.com",
        ),
        Customer(
            customerId = 4,
            customerPhone = "1112223333",
            customerName = "Emily Davis",
            customerEmail = "emily@example.com",
        ),
        Customer(
            customerId = 5,
            customerPhone = "4445556666",
            customerName = "David Wilson",
            customerEmail = "wilson@example.com",
        ),
        Customer(
            customerId = 6,
            customerPhone = "7778889999",
            customerName = "Sophia Martinez",
            customerEmail = "martinez@example.com",
        ),
        Customer(
            customerId = 7,
            customerPhone = "0101010101",
            customerName = "William Thompson",
            customerEmail = "thompson@example.com",
        ),
        Customer(
            customerId = 8,
            customerPhone = "2222222222",
            customerName = "Olivia Anderson",
            customerEmail = "anderson@example.com",
        ),
        Customer(
            customerId = 9,
            customerPhone = "3333333333",
            customerName = "Jacob Brown",
            customerEmail = "brown@example.com",
        ),
        Customer(
            customerId = 10,
            customerPhone = "4444444444",
            customerName = "Avery Taylor",
            customerEmail = "taylor@example.com",
        ),
    )

    val customerWiseOrders = listOf(
        CustomerWiseOrder(
            orderId = 1,
            totalPrice = 5000,
            updatedAt = "1685856000000",
            customerAddress = "Anytown USA",
        ),
        CustomerWiseOrder(
            orderId = 2,
            totalPrice = 7500,
            updatedAt = "1685942400000",
            customerAddress = "Someville USA",
        ),
        CustomerWiseOrder(
            orderId = 3,
            totalPrice = 2000,
            updatedAt = "1686028800000",
            customerAddress = "Otherville USA",
        ),
        CustomerWiseOrder(
            orderId = 4,
            totalPrice = 10000,
            updatedAt = "1686115200000",
            customerAddress = "Newtown USA",
        ),
        CustomerWiseOrder(
            orderId = 5,
            totalPrice = 3500,
            updatedAt = "1686201600000",
            customerAddress = "Mytown USA",
        ),
        CustomerWiseOrder(
            orderId = 6,
            totalPrice = 8000,
            updatedAt = "1686288000000",
            customerAddress = "Yourville USA",
        ),
        CustomerWiseOrder(
            orderId = 7,
            totalPrice = 6000,
            updatedAt = "1686374400000",
            customerAddress = "Theirtown USA",
        ),
        CustomerWiseOrder(
            orderId = 8,
            totalPrice = 4000,
            updatedAt = "1686460800000",
            customerAddress = "Oak Ln",
        ),
        CustomerWiseOrder(
            orderId = 9,
            totalPrice = 9000,
            updatedAt = "1686547200000",
            customerAddress = "Heretown USA",
        ),
        CustomerWiseOrder(
            orderId = 10,
            totalPrice = 7000,
            updatedAt = "1686633600000",
            customerAddress = "Theretown USA",
        ),
    )

    val sampleTotalOrder = TotalOrderDetails(
        totalAmount = 9792,
        totalOrder = 7419,
        repeatedOrder = 8110,
        datePeriod = Pair(
            "1720722600000",
            "1720837800000",
        ),
    )
}
