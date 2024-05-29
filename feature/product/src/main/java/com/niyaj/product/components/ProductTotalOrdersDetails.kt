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

package com.niyaj.product.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.isSameDay
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor1
import com.niyaj.designsystem.theme.LightColor2
import com.niyaj.designsystem.theme.LightColor3
import com.niyaj.designsystem.theme.LightColor4
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.product.details.ProductTotalOrderDetails
import com.niyaj.ui.components.ReportCardBox

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductTotalOrdersDetails(
    modifier: Modifier = Modifier,
    details: ProductTotalOrderDetails,
) = trace("ProductTotalOrdersDetails") {
    ElevatedCard(
        modifier = modifier
            .testTag("Calculate TotalOrder")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
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
                    text = (details.dineInQty + details.dineOutQty).toString(),
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
                    text = details.totalAmount.toRupee,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("ProductTotalAmount"),
                )

                val startDate = details.datePeriod.first
                val endDate = details.datePeriod.second

                if (startDate.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        ),
                        modifier = Modifier.testTag("DatePeriod"),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(SpaceMini),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = startDate.toBarDate,
                                style = MaterialTheme.typography.labelMedium,
                            )

                            if (endDate.isNotEmpty()) {
                                if (!details.datePeriod.isSameDay) {
                                    Spacer(modifier = Modifier.width(SpaceMini))
                                    Icon(
                                        imageVector = PoposIcons.ArrowRightAlt,
                                        contentDescription = "DatePeriod",
                                    )
                                    Spacer(modifier = Modifier.width(SpaceMini))
                                    Text(
                                        text = endDate.toBarDate,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceMini))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceMini))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(SpaceMedium),
                maxItemsInEachRow = 2,
            ) {
                ReportCardBox(
                    modifier = Modifier,
                    title = "DineIn Sales",
                    subtitle = details.dineInAmount.toRupee,
                    icon = PoposIcons.DinnerDining,
                    minusWidth = 30.dp,
                    iconTint = MaterialTheme.colorScheme.error,
                    containerColor = LightColor1,
                    onClick = {},
                )

                ReportCardBox(
                    modifier = Modifier,
                    title = "DineOut Sales",
                    subtitle = details.dineOutAmount.toRupee,
                    icon = PoposIcons.DeliveryDining,
                    iconTint = MaterialTheme.colorScheme.tertiary,
                    containerColor = LightColor2,
                    minusWidth = 30.dp,
                    onClick = {},
                )

                if (details.mostOrderQtyDate.isNotEmpty()) {
                    ReportCardBox(
                        modifier = Modifier,
                        title = "Most Sales",
                        subtitle = details.mostOrderQtyDate,
                        icon = PoposIcons.AutoGraph,
                        minusWidth = 30.dp,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        containerColor = LightColor3,
                        onClick = {},
                    )
                }

                if (details.mostOrderItemDate.isNotEmpty()) {
                    ReportCardBox(
                        modifier = Modifier,
                        title = "Most Orders",
                        subtitle = details.mostOrderQtyDate,
                        icon = PoposIcons.Order,
                        minusWidth = 30.dp,
                        iconTint = MaterialTheme.colorScheme.primary,
                        containerColor = LightColor4,
                        boxColor = Color.White,
                        onClick = {},
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}
