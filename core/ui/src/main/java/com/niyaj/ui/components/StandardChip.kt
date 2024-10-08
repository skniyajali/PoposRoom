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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.Constants.NOT_PAID
import com.niyaj.common.utils.Constants.PAID
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.IconSizeSmall
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun StandardOutlinedChip(
    text: String,
    modifier: Modifier = Modifier,
    secondaryText: String? = null,
    isToggleable: Boolean = true,
    isSelected: Boolean = false,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    dissectedColor: Color = MaterialTheme.colorScheme.onSecondary,
    onClick: () -> Unit = {},
) {
    val borderStroke =
        if (isSelected) BorderStroke(1.dp, selectedColor) else BorderStroke(0.dp, Color.Transparent)

    OutlinedCard(
        enabled = isToggleable,
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(SpaceMini),
        border = borderStroke,
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isSelected) {
                Icon(
                    imageVector = PoposIcons.Done,
                    contentDescription = "$text added",
                    tint = dissectedColor,
                    modifier = Modifier.size(IconSizeSmall),
                )

                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = if (isSelected) dissectedColor else selectedColor,
            )

            if (!secondaryText.isNullOrEmpty() && text.startsWith("Cold")) {
                Text(
                    text = " Rs. $secondaryText",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = if (isSelected) dissectedColor else selectedColor,
                )
            }
        }
    }
}

@Composable
fun PaymentStatusChip(
    modifier: Modifier = Modifier,
    isPaid: Boolean = false,
    text: String = if (isPaid) PAID else NOT_PAID,
    paidColor: Color = MaterialTheme.colorScheme.primary,
    notPaidColor: Color = MaterialTheme.colorScheme.secondary,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (isPaid) paidColor else notPaidColor),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isPaid) {
                Icon(
                    imageVector = PoposIcons.Done,
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(IconSizeSmall),
                )
            } else {
                Icon(
                    imageVector = PoposIcons.Close,
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(IconSizeSmall),
                )
            }

            Spacer(modifier = Modifier.width(SpaceSmall))

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun StandardChip(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = false,
    isClickable: Boolean = false,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (isPrimary) primaryColor else secondaryColor)
            .clickable(isClickable) {
                onClick()
            },
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = "$text icon",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(IconSizeSmall),
                )

                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun PoposOutlinedChip(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    borderColor: Color = MaterialTheme.colorScheme.tertiary,
    contentColor: Color = MaterialTheme.colorScheme.tertiary,
    borderStroke: BorderStroke = BorderStroke(1.dp, borderColor),
    shape: RoundedCornerShape = RoundedCornerShape(2.dp),
) {
    Surface(
        modifier = modifier,
        shape = shape,
        border = borderStroke,
        color = Color.Transparent,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = "$text icon",
                    modifier = Modifier.size(IconSizeSmall),
                )

                Spacer(modifier = Modifier.width(SpaceMini))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun PoposChip(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    contentColor: Color = contentColorFor(containerColor),
    shape: RoundedCornerShape = RoundedCornerShape(2.dp),
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = "$text icon",
                    modifier = Modifier.size(IconSizeSmall),
                )

                Spacer(modifier = Modifier.width(SpaceMini))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun StandardOutlinedChipPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        StandardOutlinedChip(
            text = "Standard Outlined Chip",
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun PaymentStatusChipPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        PaymentStatusChip(
            modifier = modifier,
            isPaid = true,
        )
    }
}

@DevicePreviews
@Composable
private fun StandardChipPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        StandardChip(
            text = "Standard Chip",
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun PoposOutlinedChipPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        PoposOutlinedChip(
            text = "Popos Outlined Chip",
            modifier = modifier,
            icon = PoposIcons.MergeType,
        )
    }
}

@DevicePreviews
@Composable
private fun PoposChipPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        PoposChip(
            text = "Popos Chip",
            modifier = modifier,
            icon = PoposIcons.StarHalf,
        )
    }
}
