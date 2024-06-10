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
import com.niyaj.model.Address
import com.niyaj.model.AddressWiseOrder
import com.niyaj.model.TotalOrderDetails
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddressPreviewData.addressList
import com.niyaj.ui.parameterProvider.AddressPreviewData.groupedAddressWiseOrder
import com.niyaj.ui.parameterProvider.AddressPreviewData.sampleAddressWiseOrders

class AddressPreviewParameter : PreviewParameterProvider<UiState<List<Address>>> {
    override val values: Sequence<UiState<List<Address>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(addressList),
        )
}

class AddressWiseOrderPreviewParameter : PreviewParameterProvider<UiState<List<AddressWiseOrder>>> {
    override val values: Sequence<UiState<List<AddressWiseOrder>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(
                groupedAddressWiseOrder +
                    sampleAddressWiseOrders,
            ),
        )
}

class AddressDetailsPreviewParameter : PreviewParameterProvider<UiState<Address>> {
    override val values: Sequence<UiState<Address>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(
                addressList.last(),
            ),
        )
}

object AddressPreviewData {
    val addressList = listOf(
        Address(
            addressId = 1,
            addressName = "123 Main Street",
            shortName = "Main St",
        ),
        Address(
            addressId = 2,
            addressName = "456 Oak Avenue",
            shortName = "Oak Ave",
        ),
        Address(
            addressId = 3,
            addressName = "789 Maple Lane",
            shortName = "Maple Ln",
        ),
        Address(
            addressId = 4,
            addressName = "321 Pine Road",
            shortName = "Pine Rd",
        ),
        Address(
            addressId = 5,
            addressName = "567 Cedar Boulevard",
            shortName = "Cedar Blvd",
        ),
        Address(
            addressId = 6,
            addressName = "890 Elm Street",
            shortName = "Elm St",
        ),
        Address(
            addressId = 7,
            addressName = "246 Birch Avenue",
            shortName = "Birch Ave",
        ),
        Address(
            addressId = 8,
            addressName = "135 Oak Drive",
            shortName = "Oak Dr",
        ),
        Address(
            addressId = 9,
            addressName = "789 Maple Court",
            shortName = "Maple Ct",
        ),
        Address(
            addressId = 10,
            addressName = "456 Pine Lane",
            shortName = "Pine Ln",
        ),
    )

    val sampleAddressWiseOrders = listOf(
        AddressWiseOrder(
            orderId = 1,
            customerPhone = "9876543210",
            totalPrice = 2999,
            updatedAt = "1683056096000",
            customerName = "Rahul Sharma",
        ),
        AddressWiseOrder(
            orderId = 2,
            customerPhone = "8765432109",
            totalPrice = 4599,
            updatedAt = "1682925320000",
            customerName = "Priya Singh",
        ),
        AddressWiseOrder(
            orderId = 3,
            customerPhone = "7654321098",
            totalPrice = 1899,
            updatedAt = "1683142730000",
            customerName = "Aditya Gupta",
        ),
        AddressWiseOrder(
            orderId = 4,
            customerPhone = "6543210987",
            totalPrice = 3499,
            updatedAt = "1683084610000",
            customerName = "Neha Patel",
        ),
        AddressWiseOrder(
            orderId = 5,
            customerPhone = "9876543210",
            totalPrice = 5699,
            updatedAt = "1683171600000",
            customerName = "Rajesh Kumar",
        ),
        AddressWiseOrder(
            orderId = 6,
            customerPhone = "8765432109",
            totalPrice = 2199,
            updatedAt = "1683258645000",
            customerName = "Isha Desai",
        ),
        AddressWiseOrder(
            orderId = 7,
            customerPhone = "7654321098",
            totalPrice = 3999,
            updatedAt = "1683242125000",
            customerName = "Arjun Mehta",
        ),
        AddressWiseOrder(
            orderId = 8,
            customerPhone = "6543210987",
            totalPrice = 4299,
            updatedAt = "1683328710000",
            customerName = "Shreya Malhotra",
        ),
        AddressWiseOrder(
            orderId = 9,
            customerPhone = "9876543210",
            totalPrice = 1599,
            updatedAt = "1683415235000",
            customerName = "Vikram Kapoor",
        ),
        AddressWiseOrder(
            orderId = 10,
            customerPhone = "8765432109",
            totalPrice = 5099,
            updatedAt = "1683502800000",
            customerName = "Nisha Verma",
        ),
    )

    val groupedAddressWiseOrder = listOf(
        AddressWiseOrder(
            orderId = 1,
            customerPhone = "9876543210",
            totalPrice = 2999,
            updatedAt = "1683056096000",
            customerName = "Rahul Sharma",
        ),
        AddressWiseOrder(
            orderId = 2,
            customerPhone = "8765432109",
            totalPrice = 4599,
            updatedAt = "1682925320000",
            customerName = "Priya Singh",
        ),
        AddressWiseOrder(
            orderId = 3,
            customerPhone = "9876543210",
            totalPrice = 2999,
            updatedAt = "1683056096000",
            customerName = "Rahul Sharma",
        ),
        AddressWiseOrder(
            orderId = 4,
            customerPhone = "7654321098",
            totalPrice = 1899,
            updatedAt = "1683142730000",
            customerName = "Aditya Gupta",
        ),
        AddressWiseOrder(
            orderId = 5,
            customerPhone = "8765432109",
            totalPrice = 4599,
            updatedAt = "1682925320000",
            customerName = "Priya Singh",
        ),
        AddressWiseOrder(
            orderId = 6,
            customerPhone = "9876543210",
            totalPrice = 2999,
            updatedAt = "1683056096000",
            customerName = "Rahul Sharma",
        ),
        AddressWiseOrder(
            orderId = 9,
            customerPhone = "7654321098",
            totalPrice = 1899,
            updatedAt = "1683142730000",
            customerName = "Aditya Gupta",
        ),

        AddressWiseOrder(
            orderId = 10,
            customerPhone = "7654321098",
            totalPrice = 1899,
            updatedAt = "1683142730000",
            customerName = "Aditya Gupta",
        ),
        AddressWiseOrder(
            orderId = 10,
            customerPhone = "7654321098",
            totalPrice = 1899,
            updatedAt = "1683142730000",
            customerName = "Aditya Gupta",
        ),
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
