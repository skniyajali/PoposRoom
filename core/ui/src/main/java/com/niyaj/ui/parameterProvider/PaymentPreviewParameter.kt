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

import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.model.PaymentType


object PaymentPreviewData {

    val payments = listOf(
        Payment(
            paymentId = 1,
            employeeId = 1,
            paymentAmount = "2000",
            paymentDate = "1673994000000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Cash,
            paymentNote = "Advance payment",
            createdAt = 1673994000000,
            updatedAt = null
        ),
        Payment(
            paymentId = 2,
            employeeId = 1,
            paymentAmount = "2500",
            paymentDate = "1675404000000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Online,
            paymentNote = "Advance payment",
            createdAt = 1675404000000,
            updatedAt = null
        ),
        Payment(
            paymentId = 3,
            employeeId = 2,
            paymentAmount = "1800",
            paymentDate = "1676672400000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Cash,
            paymentNote = "Advance payment",
            createdAt = 1676672400000,
            updatedAt = null
        ),
        Payment(
            paymentId = 4,
            employeeId = 2,
            paymentAmount = "2200",
            paymentDate = "1678080000000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Online,
            paymentNote = "Advance payment",
            createdAt = 1678080000000,
            updatedAt = null
        ),
        Payment(
            paymentId = 5,
            employeeId = 3,
            paymentAmount = "3000",
            paymentDate = "1679350800000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Cash,
            paymentNote = "Advance payment",
            createdAt = 1679350800000,
            updatedAt = null
        ),
        Payment(
            paymentId = 6,
            employeeId = 3,
            paymentAmount = "2500",
            paymentDate = "1680758400000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Online,
            paymentNote = "Advance payment",
            createdAt = 1680758400000,
            updatedAt = null
        ),
        Payment(
            paymentId = 7,
            employeeId = 4,
            paymentAmount = "4000",
            paymentDate = "1681944000000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Cash,
            paymentNote = "Advance payment",
            createdAt = 1681944000000,
            updatedAt = null
        ),
        Payment(
            paymentId = 8,
            employeeId = 4,
            paymentAmount = "3500",
            paymentDate = "1683354000000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Online,
            paymentNote = "Advance payment",
            createdAt = 1683354000000,
            updatedAt = null
        ),
        Payment(
            paymentId = 9,
            employeeId = 5,
            paymentAmount = "2000",
            paymentDate = "1684537200000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Cash,
            paymentNote = "Advance payment",
            createdAt = 1684537200000,
            updatedAt = null
        ),
        Payment(
            paymentId = 10,
            employeeId = 5,
            paymentAmount = "2500",
            paymentDate = "1685949600000",
            paymentType = PaymentType.Advanced,
            paymentMode = PaymentMode.Online,
            paymentNote = "Advance payment",
            createdAt = 1685949600000,
            updatedAt = null
        )
    )
}