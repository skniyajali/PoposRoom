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
import com.niyaj.model.DeliveryReport
import java.util.Date

class DeliveryReportPreviewParameter : PreviewParameterProvider<List<DeliveryReport>> {
    override val values: Sequence<List<DeliveryReport>>
        get() = sequenceOf(
            listOf(
                DeliveryReport(
                    orderId = 1,
                    customerAddress = "Sima Tower",
                    customerPhone = "1234567890",
                    orderPrice = 2500,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 2,
                    customerAddress = "Sima Tower",
                    customerPhone = "9876543210",
                    orderPrice = 3000,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 3,
                    customerAddress = "Sima Tower",
                    customerPhone = "5555555555",
                    orderPrice = 1800,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 4,
                    customerAddress = "Sima Tower",
                    customerPhone = "1112223333",
                    orderPrice = 4200,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 5,
                    customerAddress = "Sima Tower",
                    customerPhone = "4445556666",
                    orderPrice = 2100,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 6,
                    customerAddress = "Ashok Next",
                    customerPhone = "7778889999",
                    orderPrice = 3400,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 7,
                    customerAddress = "Ashok Next",
                    customerPhone = "2223334444",
                    orderPrice = 1900,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 8,
                    customerAddress = "Ashok Next",
                    customerPhone = "6667777888",
                    orderPrice = 2700,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 9,
                    customerAddress = "Ashok Next",
                    customerPhone = "9990009999",
                    orderPrice = 3100,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 10,
                    customerAddress = "Ashok Next",
                    customerPhone = "1112229999",
                    orderPrice = 2600,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),

                DeliveryReport(
                    orderId = 11,
                    customerAddress = "Mbbs Boys",
                    customerPhone = "5556667777",
                    orderPrice = 2900,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 12,
                    customerAddress = "Mbbs Boys",
                    customerPhone = "8889990000",
                    orderPrice = 3200,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 13,
                    customerAddress = "Mbbs Boys",
                    customerPhone = "2223334444",
                    orderPrice = 1700,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 14,
                    customerAddress = "Mbbs Boys",
                    customerPhone = "6667778888",
                    orderPrice = 4100,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 15,
                    customerAddress = "Mbbs Boys",
                    customerPhone = "9990001111",
                    orderPrice = 2400,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 16,
                    customerAddress = "New Ladies",
                    customerPhone = "1112223344",
                    orderPrice = 3600,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 17,
                    customerAddress = "New Ladies",
                    customerPhone = "4445556677",
                    orderPrice = 2200,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 18,
                    customerAddress = "New Ladies",
                    customerPhone = "7778889900",
                    orderPrice = 3800,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 19,
                    customerAddress = "New Ladies",
                    customerPhone = "2223330000",
                    orderPrice = 2000,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
                DeliveryReport(
                    orderId = 20,
                    customerAddress = "New Ladies",
                    customerPhone = "6669997777",
                    orderPrice = 2800,
                    partnerId = 1,
                    partnerName = "Partner A",
                    orderDate = Date(),
                ),
            ),
        )
}