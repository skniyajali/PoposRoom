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

import com.niyaj.common.utils.getStartDate
import com.niyaj.model.DeliveryReport
import com.niyaj.model.TotalDeliveryPartnerOrder

object DeliveryPartnerPreviewData {
    val partnerOrders = List(10) {
        TotalDeliveryPartnerOrder(
            partnerId = it,
            totalOrders = it.plus(10),
            totalAmount = it.plus(30).toLong(),
            partnerName = "Partner $it",
        )
    }

    val deliveryReports = List(20) {
        DeliveryReport(
            orderId = it + 1,
            customerAddress = "Address $it",
            customerPhone = "${9876543210 + it}",
            orderPrice = it.plus(100).toLong(),
            partnerId = it % 10,
            partnerName = "Partner $it",
            orderDate = getStartDate,
        )
    }
}
