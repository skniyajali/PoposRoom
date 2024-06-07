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

package com.niyaj.market.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.components.PoposOutlinedAssistChip
import com.niyaj.designsystem.components.StandardFilterChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.MarketListAndType
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.IconWithText

@Composable
fun MarketListItemHeader(
    marketList: MarketListAndType,
) = trace("MarketListItemHeader") {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSmall),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                CircularBox(
                    icon = PoposIcons.CalenderMonth,
                    doesSelected = false,
                )

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(SpaceMini),
                ) {
                    Text(
                        text = "Market Date".uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Text(
                        text = marketList.marketDate.toPrettyDate(),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                StandardFilterChip(
                    text = marketList.typeName.uppercase(),
                    containerColor = MaterialTheme.colorScheme.secondary,
                    icon = PoposIcons.Category,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                    ),
                    fontWeight = FontWeight.SemiBold,
                )

                PoposOutlinedAssistChip(
                    text = marketList.listType,
                    icon = PoposIcons.ListAlt,
                    borderColor = MaterialTheme.colorScheme.secondary,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Italic,
                    ),
                )
            }
        }
    }
}

@Composable
fun MarketListItemHeader(
    marketDate: Long,
) = trace("MarketListItemHeader") {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSmall),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                CircularBox(
                    icon = PoposIcons.CalenderMonth,
                    doesSelected = false,
                )
                Text(
                    text = "Market Date".uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            IconWithText(
                text = marketDate.toFormattedDate,
                icon = PoposIcons.CalenderMonth,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
