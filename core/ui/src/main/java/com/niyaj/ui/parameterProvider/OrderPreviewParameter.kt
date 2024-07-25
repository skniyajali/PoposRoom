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
import com.niyaj.model.Order
import com.niyaj.model.OrderType
import java.util.Calendar
import java.util.Date

class OrderTypePreviewParameter : PreviewParameterProvider<OrderType> {
    override val values: Sequence<OrderType>
        get() = sequenceOf(OrderType.DineIn, OrderType.DineOut)
}

object OrderPreviewData {
    val dineInOrders = List(10) {
        Order(
            orderId = it + 1,
            orderPrice = (1000L..2000L).random(),
            orderDate = Date(),
        )
    }

    val dineOutOrders = List(10) {
        Order(
            orderId = it + 11,
            orderType = OrderType.DineOut,
            customerPhone = (1000000000L..9999999999L).random().toString(),
            customerAddress = "${(100..999).random()} ${
                listOf(
                    "Main",
                    "Oak",
                    "Elm",
                    "Pine",
                ).random()
            } St",
            deliveryPartnerName = listOf("FoodDelivery", "FoodRunner", "FoodExpress").random(),
            deliveryPartnerId = (1..3).random(),
            orderDate = Calendar.getInstance()
                .apply {
                    set(Calendar.HOUR_OF_DAY, (9..18).random())
                    set(Calendar.MINUTE, (0..59).random())
                }.time,
            orderPrice = (2000L..3000L).random(),
        )
    }
}
