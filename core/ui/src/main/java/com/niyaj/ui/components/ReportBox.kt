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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun ReportBox(
    title: String,
    amount: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    elevation: Dp = 1.dp,
    minusWidth: Dp = 15.dp,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    boxColor: Color = Color.White,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .width(screenWidth.div(2F).minus(minusWidth)),
        elevation = CardDefaults.cardElevation(elevation, disabledElevation = elevation),
        enabled = enabled,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            disabledContainerColor = CardDefaults.elevatedCardColors().containerColor,
            disabledContentColor = CardDefaults.elevatedCardColors().contentColor,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmallMax),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                Text(
                    text = amount.toRupee,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(boxColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

@Composable
fun ReportCardBox(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    elevation: Dp = 1.dp,
    minusWidth: Dp = 20.dp,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    boxColor: Color = Color.White,
    iconTint: Color = MaterialTheme.colorScheme.secondary,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .width(
                screenWidth
                    .div(2f)
                    .minus(minusWidth),
            )
            .height(IntrinsicSize.Min),
        elevation = CardDefaults.elevatedCardElevation(elevation, disabledElevation = elevation),
        enabled = enabled,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            disabledContainerColor = CardDefaults.elevatedCardColors().containerColor,
            disabledContentColor = CardDefaults.elevatedCardColors().contentColor,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmallMax),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(boxColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                )
            }

            Spacer(modifier = Modifier.width(SpaceSmall))

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ReportBoxPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ReportBox(
            title = "Total Earnings",
            amount = "₹ 1,00,000",
            icon = PoposIcons.Money,
            onClick = {},
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun ReportCardBoxPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ReportCardBox(
            title = "Total Earnings",
            subtitle = "₹ 1,00,000",
            icon = PoposIcons.Money,
            onClick = {},
            modifier = modifier,
        )
    }
}
