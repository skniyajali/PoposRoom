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
import com.niyaj.model.TotalDeliveryPartnerOrder

class TotalDeliveryPartnerOrderPreviewParameter :
    PreviewParameterProvider<List<TotalDeliveryPartnerOrder>> {
    override val values: Sequence<List<TotalDeliveryPartnerOrder>>
        get() = sequenceOf(
            listOf(
                TotalDeliveryPartnerOrder(
                    partnerId = 1,
                    totalOrders = 25,
                    totalAmount = 75000,
                    partnerName = "Partner A",
                ),
                TotalDeliveryPartnerOrder(
                    partnerId = 2,
                    totalOrders = 18,
                    totalAmount = 60000,
                    partnerName = "Partner B",
                ),
                TotalDeliveryPartnerOrder(
                    partnerId = 3,
                    totalOrders = 32,
                    totalAmount = 90000,
                    partnerName = "Partner C",
                ),
                TotalDeliveryPartnerOrder(
                    partnerId = 4,
                    totalOrders = 20,
                    totalAmount = 65000,
                    partnerName = "Partner D",
                ),
                TotalDeliveryPartnerOrder(
                    partnerId = 5,
                    totalOrders = 28,
                    totalAmount = 80000,
                    partnerName = "Partner E",
                ),
                TotalDeliveryPartnerOrder(
                    partnerId = 0,
                    totalOrders = 15,
                    totalAmount = 40000,
                    partnerName = null,
                ),
                TotalDeliveryPartnerOrder(
                    partnerId = 6,
                    totalOrders = 22,
                    totalAmount = 70000,
                    partnerName = "Partner F",
                ),
                TotalDeliveryPartnerOrder(
                    partnerId = 7,
                    totalOrders = 30,
                    totalAmount = 85000,
                    partnerName = "Partner G",
                ),
                TotalDeliveryPartnerOrder(
                    partnerId = 8,
                    totalOrders = 24,
                    totalAmount = 75000,
                    partnerName = "Partner H",
                ),
                TotalDeliveryPartnerOrder(
                    partnerId = 9,
                    totalOrders = 26,
                    totalAmount = 80000,
                    partnerName = "Partner I",
                ),
            ),
        )
}