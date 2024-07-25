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

import com.niyaj.model.DeliveryReport
import com.niyaj.model.TotalDeliveryPartnerOrder
import java.util.Date

object DeliveryPartnerPreviewData {
    val partnerOrders = List(10) {
        TotalDeliveryPartnerOrder(
            partnerId = it,
            totalOrders = (10..20).random(),
            totalAmount = (4000L..5000).random(),
            partnerName = "Partner $it",
        )
    }

    val deliveryReports = List(20) {
        DeliveryReport(
            orderId = it + 1,
            customerAddress = "Address $it",
            customerPhone = "${(1000000000..9999999999).random()}",
            orderPrice = (100L..500).random(),
            partnerId = (1..8).random(),
            partnerName = "Partner $it",
            orderDate = Date(),
        )
    }
}
