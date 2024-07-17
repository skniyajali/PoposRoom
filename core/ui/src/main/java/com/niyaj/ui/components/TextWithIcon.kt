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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun IconWithText(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    tintColor: Color = MaterialTheme.colorScheme.onSurface,
    isTitle: Boolean = false,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = text,
                tint = tintColor,
                modifier = iconModifier,
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }

        Text(
            text = text,
            style = textStyle,
            fontWeight = if (isTitle) FontWeight.SemiBold else fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = textColor,
        )
    }
}

@Composable
fun IconWithText(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: AnnotatedString,
    icon: ImageVector? = null,
    isTitle: Boolean = false,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (text.isNotEmpty()) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text.text,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = iconModifier,
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            }
            Text(
                text = text,
                fontFamily = if (text.startsWith("Email") || text.startsWith("Password")) FontFamily.Monospace else null,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isTitle) FontWeight.SemiBold else fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun IconWithText(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    secondaryText: String? = null,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    tintColor: Color = MaterialTheme.colorScheme.secondary,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceMini),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = tintColor,
            modifier = iconModifier,
        )
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(SpaceMini),
        ) {
            Text(
                text = text,
                style = style,
                fontWeight = fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )

            secondaryText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                )
            }
        }
    }
}

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    secondaryText: String? = null,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    tintColor: Color = MaterialTheme.colorScheme.secondary,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceMini),
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(SpaceMini),
        ) {
            Text(
                text = text,
                style = style,
                fontWeight = fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )

            secondaryText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                )
            }
        }

        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = tintColor,
            modifier = iconModifier,
        )
    }
}

@Composable
fun NoteText(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier.size(SpaceMedium),
    text: String,
    icon: ImageVector = PoposIcons.ErrorOutline,
    style: TextStyle = MaterialTheme.typography.labelSmall,
    color: Color = MaterialTheme.colorScheme.secondary,
    fontWeight: FontWeight = FontWeight.Normal,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = iconModifier,
        )

        Spacer(modifier = Modifier.width(SpaceMini))

        Text(
            text = text,
            style = style,
            fontWeight = fontWeight,
            maxLines = maxLines,
            overflow = overflow,
            color = color,
        )
    }
}

@Composable
fun InfoText(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier.size(SpaceMedium),
    text: String,
    icon: ImageVector = PoposIcons.ErrorOutline,
    backgroundColor: Color = MaterialTheme.colorScheme.errorContainer,
    textColor: Color = MaterialTheme.colorScheme.error,
    fontWeight: FontWeight = FontWeight.Normal,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                onClick()
            },
        shape = RoundedCornerShape(SpaceMini),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textColor,
                modifier = iconModifier,
            )

            Text(
                text = text,
                style = textStyle,
                fontWeight = fontWeight,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )
        }
    }
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector = PoposIcons.Info,
    height: Dp = 48.dp,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }

    ListItem(
        modifier = modifier
            .heightIn(height)
            .fillMaxWidth()
            .clip(RoundedCornerShape(SpaceMini))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                onClick()
            },
        headlineContent = {
            Text(
                text = text,
                style = textStyle,
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = "info",
            )
        },
        colors = ListItemDefaults.colors(containerColor = containerColor),
    )
}

@Composable
fun InfoText(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector = PoposIcons.Info,
    style: TextStyle = LocalTextStyle.current,
    maxLines: Int = 3,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    textAlign: TextAlign = TextAlign.Start,
) {
    Box(
        modifier = modifier
            .width(IntrinsicSize.Max),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center),
            color = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.error,
        ) {
            Row(
                modifier = Modifier
                    .padding(SpaceSmall),
                horizontalArrangement = Arrangement.spacedBy(
                    SpaceSmall,
                    Alignment.CenterHorizontally,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "infoIcon",
                )

                Text(
                    text = text,
                    textAlign = textAlign,
                    maxLines = maxLines,
                    overflow = overflow,
                    style = style,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun InfoTextPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
        ) {
            InfoText(
                modifier = modifier,
                text = "This is info text and test for max lines and overflow",
            )
        }
    }
}
