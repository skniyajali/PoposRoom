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
import com.niyaj.model.Address

class AddressPreviewParameter : PreviewParameterProvider<List<Address>> {
    override val values: Sequence<List<Address>>
        get() = sequenceOf(
            listOf(
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
            ),
        )
}
