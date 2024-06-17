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

package com.niyaj.order.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Customer
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.utils.DevicePreviews

/**
 * This composable displays the customer details
 */
@Composable
internal fun CustomerDetails(
    modifier: Modifier = Modifier,
    customer: Customer,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onClickViewDetails: (Int) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("CustomerDetails") {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            title = {
                IconWithText(
                    text = "Customer Details",
                    icon = PoposIcons.Person,
                    isTitle = true,
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    onClick = {
                        onClickViewDetails(customer.customerId)
                    },
                ) {
                    Icon(
                        imageVector = PoposIcons.OpenInNew,
                        contentDescription = "View Address Details",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    },
                ) {
                    Icon(
                        imageVector = PoposIcons.ArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    customer.customerName?.let {
                        IconWithText(
                            text = "Name: $it",
                            icon = PoposIcons.Person,
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    IconWithText(
                        text = "Phone: ${customer.customerPhone}",
                        icon = PoposIcons.PhoneAndroid,
                    )

                    customer.customerName?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = "Name: $it",
                            icon = PoposIcons.Email,
                        )
                    }

                    customer.customerEmail?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = "Email: $it",
                            icon = PoposIcons.Email,
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    IconWithText(
                        text = "Created At : ${customer.createdAt.toPrettyDate()}",
                        icon = PoposIcons.AccessTime,
                    )

                    customer.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = "Updated At : ${it.toPrettyDate()}",
                            icon = PoposIcons.Update,
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    PoposButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onClickViewDetails(customer.customerId)
                        },
                        text = "View Customer Details".uppercase(),
                        icon = PoposIcons.Person,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                        ),
                    )
                }
            },
        )
    }
}

@DevicePreviews
@Composable
private fun CustomerDetailsPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CustomerDetails(
            modifier = Modifier,
            customer = Customer(
                customerId = 1,
                customerPhone = "9876543211",
                customerName = "Niyaj",
                customerEmail = "niyaj@gmail.com",
                createdAt = System.currentTimeMillis(),
                updatedAt = null,
            ),
            doesExpanded = true,
            onExpandChanged = {},
            onClickViewDetails = {},
        )
    }
}
