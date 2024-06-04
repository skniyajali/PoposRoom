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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposOutlinedButton
import com.niyaj.designsystem.components.PoposSuggestionChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.TotalOrders
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun TotalDeliveryReportCard(
    modifier: Modifier = Modifier,
    selectedDate: String,
    totalOrders: TotalOrders,
    onClickPrint: () -> Unit,
    onClickShare: () -> Unit,
    onChangeDate: () -> Unit,
    primaryBtnColor: Color = MaterialTheme.colorScheme.primary,
    secBtnColor: Color = MaterialTheme.colorScheme.secondary,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
) = trace("TotalDeliveryReportCard") {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = color,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SpaceSmall),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            elevation = CardDefaults.elevatedCardElevation(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMedium),
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularBox(
                    icon = PoposIcons.DeliveryDining,
                    doesSelected = false,
                    size = 60.dp,
                    showBorder = true,
                )

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Total Orders",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )

                        Text(
                            text = totalOrders.totalOrders.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = totalOrders.totalAmount.toRupee,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .testTag("deliveryTotalAmount"),
                        )

                        PoposSuggestionChip(
                            icon = PoposIcons.CalenderMonth,
                            text = selectedDate.toPrettyDate(),
                            onClick = onChangeDate,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceMini))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceMini))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMedium),
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PoposOutlinedButton(
                    modifier = Modifier.weight(1.4f),
                    text = "Share",
                    icon = PoposIcons.Share,
                    enabled = totalOrders.totalOrders != 0L,
                    onClick = onClickShare,
                    color = secBtnColor,
                )

                PoposButton(
                    modifier = Modifier.weight(1.4f),
                    icon = PoposIcons.Print,
                    text = "Print",
                    enabled = totalOrders.totalOrders != 0L,
                    onClick = onClickPrint,
                    color = primaryBtnColor,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}

@DevicePreviews
@Composable
private fun TotalDeliveryReportCardPreview() {
    TotalDeliveryReportCard(
        totalOrders = TotalOrders(2, 200),
        selectedDate = System.currentTimeMillis().toString(),
        onClickPrint = {},
        onClickShare = {},
        onChangeDate = {},
    )
}

@DevicePreviews
@Composable
private fun TotalDeliveryReportCardEmptyPreview() {
    TotalDeliveryReportCard(
        totalOrders = TotalOrders(),
        selectedDate = System.currentTimeMillis().toString(),
        onClickPrint = {},
        onClickShare = {},
        onChangeDate = {},
    )
}
