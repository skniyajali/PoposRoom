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
import com.niyaj.common.utils.getStartDateLong
import com.niyaj.model.Charges
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ChargesPreviewData.chargesList

class ChargesPreviewParameterProvider : PreviewParameterProvider<UiState<List<Charges>>> {
    override val values: Sequence<UiState<List<Charges>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(chargesList),
        )
}

object ChargesPreviewData {
    val chargesList = listOf(
        Charges(
            chargesId = 1,
            chargesName = "Service Charge",
            chargesPrice = 200,
            isApplicable = true,
            createdAt = getStartDateLong,
        ),
        Charges(
            chargesId = 2,
            chargesName = "Tax Charge",
            chargesPrice = 100,
            isApplicable = true,
            createdAt = getStartDateLong,
        ),
        Charges(
            chargesId = 3,
            chargesName = "Delivery Charge",
            chargesPrice = 50,
            isApplicable = true,
            createdAt = getStartDateLong,
        ),
        Charges(
            chargesId = 4,
            chargesName = "Discount",
            chargesPrice = 50,
            isApplicable = true,
            createdAt = getStartDateLong,
        ),
        Charges(
            chargesId = 5,
            chargesName = "Packaging Charge",
            chargesPrice = 30,
            isApplicable = true,
            createdAt = getStartDateLong,
        ),
        Charges(
            chargesId = 6,
            chargesName = "Handling Charge",
            chargesPrice = 20,
            isApplicable = true,
            createdAt = getStartDateLong,
        ),
        Charges(
            chargesId = 7,
            chargesName = "Additional Fee",
            chargesPrice = 80,
            isApplicable = true,
            createdAt = getStartDateLong,
        ),
        Charges(
            chargesId = 8,
            chargesName = "Insurance",
            chargesPrice = 150,
            isApplicable = true,
            createdAt = getStartDateLong,
        ),
        Charges(
            chargesId = 9,
            chargesName = "VAT Charge",
            chargesPrice = 120,
            isApplicable = true,
            createdAt = getStartDateLong,
        ),
        Charges(
            chargesId = 10,
            chargesName = "Surcharge",
            chargesPrice = 50,
            isApplicable = true,
            createdAt = getStartDateLong,
        ),
    )
}
