/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.Order
import com.niyaj.model.OrderType
import java.util.Calendar
import java.util.Date

class OrderPreviewParameter : PreviewParameterProvider<Pair<List<Order>, List<Order>>> {
    override val values: Sequence<Pair<List<Order>, List<Order>>>
        get() = sequenceOf(
            Pair(
                listOf(
                    Order(
                        orderId = 1,
                        orderPrice = 2500,
                        orderDate = Date(),
                    ),
                    Order(
                        orderId = 2,
                        orderPrice = 3800,
                        orderDate = Date(),
                    ),
                    Order(
                        orderId = 3,
                        orderPrice = 4200,
                        orderDate = Date(),
                    ),
                    Order(
                        orderId = 4,
                        orderPrice = 5600,
                        orderDate = Date(),
                    ),
                    Order(
                        orderId = 5,
                        orderPrice = 7000,
                        orderDate = Date(),
                    ),
                    Order(
                        orderId = 6,
                        orderPrice = 8400,
                        orderDate = Date(),
                    ),
                    Order(
                        orderId = 7,
                        orderPrice = 9800,
                        orderDate = Date(),
                    ),
                    Order(
                        orderId = 8,
                        orderPrice = 11200,
                        orderDate = Date(),
                    ),
                    Order(
                        orderId = 9,
                        orderPrice = 12600,
                        orderDate = Date(),
                    ),
                    Order(
                        orderId = 10,
                        orderPrice = 14000,
                        orderDate = Date(),
                    ),
                ),
                listOf(
                    Order(
                        orderId = 11,
                        orderType = OrderType.DineOut,
                        customerPhone = "1234567890",
                        customerAddress = "123 Main St",
                        deliveryPartnerName = "FoodDelivery",
                        orderDate = Calendar.getInstance()
                            .apply { set(Calendar.HOUR_OF_DAY, 9); set(Calendar.MINUTE, 0) }.time,
                        orderPrice = 2000,
                    ),
                    Order(
                        orderId = 12,
                        orderType = OrderType.DineOut,
                        customerPhone = "9876543210",
                        customerAddress = "456 Oak St",
                        deliveryPartnerName = "FoodRunner",
                        orderDate = Calendar.getInstance()
                            .apply { set(Calendar.HOUR_OF_DAY, 10); set(Calendar.MINUTE, 15) }.time,
                        orderPrice = 3500,
                    ),
                    Order(
                        orderId = 13,
                        orderType = OrderType.DineOut,
                        customerPhone = "5678901234",
                        customerAddress = "789 Elm St",
                        deliveryPartnerName = "FoodExpress",
                        orderDate = Calendar.getInstance()
                            .apply { set(Calendar.HOUR_OF_DAY, 11); set(Calendar.MINUTE, 30) }.time,
                        orderPrice = 4200,
                    ),
                    Order(
                        orderId = 14,
                        orderType = OrderType.DineOut,
                        customerPhone = "1112223333",
                        customerAddress = "456 Pine St",
                        deliveryPartnerName = "FoodDelivery",
                        orderDate = Calendar.getInstance()
                            .apply { set(Calendar.HOUR_OF_DAY, 12); set(Calendar.MINUTE, 45) }.time,
                        orderPrice = 5800,
                    ),
                    Order(
                        orderId = 15,
                        orderType = OrderType.DineOut,
                        customerPhone = "4445556666",
                        customerAddress = "789 Oak St",
                        deliveryPartnerName = "FoodRunner",
                        orderDate = Calendar.getInstance()
                            .apply { set(Calendar.HOUR_OF_DAY, 13); set(Calendar.MINUTE, 0) }.time,
                        orderPrice = 6500,
                    ),
                    Order(
                        orderId = 16,
                        orderType = OrderType.DineOut,
                        customerPhone = "7778889999",
                        customerAddress = "321 Main St",
                        deliveryPartnerName = "FoodExpress",
                        orderDate = Calendar.getInstance()
                            .apply { set(Calendar.HOUR_OF_DAY, 14); set(Calendar.MINUTE, 15) }.time,
                        orderPrice = 7200,
                    ),
                    Order(
                        orderId = 17,
                        orderType = OrderType.DineOut,
                        customerPhone = "9630258741",
                        customerAddress = "456 Elm St",
                        deliveryPartnerName = "FoodDelivery",
                        orderDate = Calendar.getInstance()
                            .apply { set(Calendar.HOUR_OF_DAY, 15); set(Calendar.MINUTE, 30) }.time,
                        orderPrice = 8000,
                    ),
                    Order(
                        orderId = 18,
                        orderType = OrderType.DineOut,
                        customerPhone = "2586974135",
                        customerAddress = "789 Pine St",
                        deliveryPartnerName = "FoodRunner",
                        orderDate = Calendar.getInstance()
                            .apply { set(Calendar.HOUR_OF_DAY, 16); set(Calendar.MINUTE, 45) }.time,
                        orderPrice = 9500,
                    ),
                    Order(
                        orderId = 19,
                        orderType = OrderType.DineOut,
                        customerPhone = "5678901234",
                        customerAddress = "456 Oak St",
                        deliveryPartnerName = "FoodDelivery",
                        orderDate = Calendar.getInstance()
                            .apply { set(Calendar.HOUR_OF_DAY, 17); set(Calendar.MINUTE, 0) }.time,
                        orderPrice = 10000,
                    ),
                    Order(
                        orderId = 20,
                        orderType = OrderType.DineOut,
                        customerPhone = "9876543210",
                        customerAddress = "789 Elm St",
                        deliveryPartnerName = "FoodRunner",
                        orderDate = Calendar.getInstance()
                            .apply { set(Calendar.HOUR_OF_DAY, 18); set(Calendar.MINUTE, 15) }.time,
                        orderPrice = 11000,
                    ),
                ),
            ),
        )
}