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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.components.PoposTonalIconButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.TotalDeliveryPartnerOrder
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun DeliveryPartnerCard(
    order: TotalDeliveryPartnerOrder,
    modifier: Modifier = Modifier,
    showPrintBtn: Boolean = true,
    height: Dp = 70.dp,
    onClickPrint: (Int) -> Unit = {},
    onClickViewDetails: (Int) -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("DeliveryPartnerCard") {
    ListItem(
        headlineContent = {
            Text(
                text = order.partnerName ?: "Unmanaged",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        supportingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                Text(
                    text = "${order.totalOrders} Orders |",
                )

                Text(
                    text = order.totalAmount.toRupee,
                )
            }
        },
        leadingContent = {
            CircularBox(
                icon = PoposIcons.DeliveryDining,
                selected = false,
                text = order.partnerName ?: "Unmanaged",
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                AnimatedVisibility(
                    visible = showPrintBtn,
                ) {
                    PoposTonalIconButton(
                        icon = PoposIcons.Print,
                        onClick = {
                            onClickPrint(order.partnerId)
                        },
                    )
                }

                Icon(
                    imageVector = PoposIcons.ArrowRightAlt,
                    contentDescription = "viewDetails",
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = height)
            .clip(RoundedCornerShape(SpaceSmall))
            .clickable {
                onClickViewDetails(order.partnerId)
            }
            .shadow(1.dp, RoundedCornerShape(SpaceSmall)),
    )
}

@DevicePreviews
@Composable
private fun DeliveryPartnerCardPreview() {
    DeliveryPartnerCard(
        order = TotalDeliveryPartnerOrder(
            partnerId = 3,
            totalOrders = 10,
            totalAmount = 8674,
            partnerName = null,
        ),
        onClickPrint = {},
        onClickViewDetails = {},
    )
}
