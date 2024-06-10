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
import com.niyaj.common.utils.getDateInMilliseconds
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

class CustomerWiseOrderPreviewParameter: PreviewParameterProvider<UiState<List<CustomerWiseOrder>>> {
    override val values: Sequence<UiState<List<CustomerWiseOrder>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(customerWiseOrders)
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

    val customerWiseOrders = listOf(
        CustomerWiseOrder(
            orderId = 1,
            totalPrice = 5000,
            updatedAt = "1685856000000",
            customerAddress = "123 Main St, Anytown USA"
        ),
        CustomerWiseOrder(
            orderId = 2,
            totalPrice = 7500,
            updatedAt = "1685942400000",
            customerAddress = "456 Oak Rd, Someville USA"
        ),
        CustomerWiseOrder(
            orderId = 3,
            totalPrice = 2000,
            updatedAt = "1686028800000",
            customerAddress = "789 Maple Ave, Otherville USA"
        ),
        CustomerWiseOrder(
            orderId = 4,
            totalPrice = 10000,
            updatedAt = "1686115200000",
            customerAddress = "321 Pine St, Newtown USA"
        ),
        CustomerWiseOrder(
            orderId = 5,
            totalPrice = 3500,
            updatedAt = "1686201600000",
            customerAddress = "567 Elm St, Mytown USA"
        ),
        CustomerWiseOrder(
            orderId = 6,
            totalPrice = 8000,
            updatedAt = "1686288000000",
            customerAddress = "890 Cedar Rd, Yourville USA"
        ),
        CustomerWiseOrder(
            orderId = 7,
            totalPrice = 6000,
            updatedAt = "1686374400000",
            customerAddress = "246 Birch Ave, Theirtown USA"
        ),
        CustomerWiseOrder(
            orderId = 8,
            totalPrice = 4000,
            updatedAt = "1686460800000",
            customerAddress = "135 Oak Ln, Ourtown USA"
        ),
        CustomerWiseOrder(
            orderId = 9,
            totalPrice = 9000,
            updatedAt = "1686547200000",
            customerAddress = "679 Maple Blvd, Heretown USA"
        ),
        CustomerWiseOrder(
            orderId = 10,
            totalPrice = 7000,
            updatedAt = "1686633600000",
            customerAddress = "864 Pine Dr, Theretown USA"
        )
    )

    val sampleTotalOrder = TotalOrderDetails(
        totalAmount = 9792,
        totalOrder = 7419,
        repeatedOrder = 8110,
        datePeriod = Pair(
            getDateInMilliseconds(13),
            getDateInMilliseconds(17),
        ),
    )
}