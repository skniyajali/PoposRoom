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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.AssistChipDefaults.assistChipBorder
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.SuggestionChipDefaults.suggestionChipBorder
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.IconSizeMini

@Composable
fun StandardAssistChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    borderColor: Color = MaterialTheme.colorScheme.secondary,
    onClick: () -> Unit = {},
) {
    AssistChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = borderColor,
            )
        },
        border = assistChipBorder(
            enabled = true,
            borderColor,
        ),
    )
}

@Composable
fun StandardFilterChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    selected: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    fontWeight: FontWeight = FontWeight.Normal,
    onClick: () -> Unit = {},
) {
    ElevatedFilterChip(
        modifier = modifier,
        onClick = onClick,
        selected = selected,
        label = {
            Text(
                text = text,
                style = textStyle,
                fontWeight = fontWeight,
            )
        },
        leadingIcon = {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                )
            }
        },
        colors = FilterChipDefaults.elevatedFilterChipColors(
            containerColor = containerColor,
            selectedContainerColor = selectedColor,
            labelColor = contentColorFor(backgroundColor = containerColor),
            iconColor = contentColorFor(backgroundColor = containerColor),
        ),
    )
}

@Composable
fun StandardRoundedFilterChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    selected: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    onClick: () -> Unit = {},
) {
    ElevatedFilterChip(
        modifier = modifier,
        onClick = onClick,
        selected = selected,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        },
        leadingIcon = {
            icon?.let {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text.plus("icon"),
                        modifier = Modifier
                            .size(IconSizeMini)
                            .align(Alignment.Center),
                    )
                }
            }
        },
        colors = FilterChipDefaults.elevatedFilterChipColors(
            containerColor = containerColor,
            selectedContainerColor = selectedColor,
            selectedLabelColor = contentColorFor(backgroundColor = selectedColor),
        ),
    )
}

@Composable
fun StandardOutlinedAssistChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    trailingIcon: ImageVector? = null,
    borderColor: Color = MaterialTheme.colorScheme.tertiary,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
    onClick: () -> Unit = {},
) {
    AssistChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = textStyle,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "$text Leading Icon",
            )
        },
        trailingIcon = {
            trailingIcon?.let {
                Icon(imageVector = it, contentDescription = "chip icon")
            }
        },
        border = assistChipBorder(
            enabled = true,
            borderColor = borderColor,
        ),
        colors = AssistChipDefaults.assistChipColors(
            leadingIconContentColor = borderColor,
        ),
    )
}

@Composable
fun StandardSuggestionChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    borderColor: Color = MaterialTheme.colorScheme.tertiary,
    labelColor: Color = borderColor,
    chipBorder: BorderStroke = suggestionChipBorder(
        enabled = true,
        borderColor = borderColor,
    ),
    colors: ChipColors = SuggestionChipDefaults.suggestionChipColors(
        labelColor = labelColor,
        iconContentColor = labelColor,
    ),
    onClick: () -> Unit = {},
) {
    SuggestionChip(
        modifier = modifier,
        onClick = onClick,
        border = chipBorder,
        colors = colors,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = "Order Type",
            )
        },
        label = {
            Text(text = text)
        },
    )
}
