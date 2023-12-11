package com.niyaj.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
                style = MaterialTheme.typography.labelSmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = borderColor
            )
        },
        border = assistChipBorder(
            enabled = true,
            borderColor
        )
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
    onClick: () -> Unit = {},
) {
    ElevatedFilterChip(
        modifier = modifier,
        onClick = onClick,
        selected = selected,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall
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
            selectedContainerColor = selectedColor
        )
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
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text.plus("icon"),
                        modifier = Modifier
                            .size(IconSizeMini)
                            .align(Alignment.Center)
                    )
                }
            }
        },
        colors = FilterChipDefaults.elevatedFilterChipColors(
            containerColor = containerColor,
            selectedContainerColor = selectedColor,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}


@Composable
fun StandardOutlinedAssistChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    trailingIcon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    AssistChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "$text Leading Icon",
                modifier = Modifier.size(IconSizeMini)
            )
        },
        trailingIcon = {
            trailingIcon?.let {
                Icon(imageVector = it, contentDescription = "chip icon")
            }
        }
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
        iconContentColor = labelColor
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
                contentDescription = "Order Type"
            )
        },
        label = {
            Text(text = text)
        },
    )
}