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

package com.niyaj.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.isSameDay
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.TotalOrderDetails

@Composable
fun TotalOrderDetailsCard(
    details: TotalOrderDetails,
) {
    ElevatedCard(
        modifier = Modifier
            .testTag("CalculateSalary")
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
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Total Orders",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(SpaceSmall))
            Spacer(modifier = Modifier.height(SpaceSmall))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

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
                    modifier = Modifier.testTag(REMAINING_AMOUNT_TEXT),
                )

                val startDate = details.datePeriod.first
                val endDate = details.datePeriod.second

                if (startDate.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
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
                                        imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
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

            Spacer(modifier = Modifier.height(SpaceSmall))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    ),
                    modifier = Modifier.testTag("TotalOrders"),
                ) {
                    val string = AnnotatedString.Builder()
                    string.append("Total ")
                    string.withStyle(
                        SpanStyle(fontWeight = FontWeight.SemiBold),
                    ) {
                        append("${details.totalOrder}")
                    }
                    string.append(" Order")

                    Text(
                        text = string.toAnnotatedString(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(SpaceSmall),
                    )
                }

                Spacer(modifier = Modifier.width(SpaceSmall))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                    modifier = Modifier.testTag("RepeatedCustomer"),
                ) {
                    val string = AnnotatedString.Builder()
                    string.withStyle(
                        SpanStyle(fontWeight = FontWeight.SemiBold),
                    ) {
                        append("${details.repeatedOrder}")
                    }
                    string.append(" Repeated Customer")

                    Text(
                        text = string.toAnnotatedString(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(SpaceSmall),
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}

const val REMAINING_AMOUNT_TEXT = "Remaining Amount"
