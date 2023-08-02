package com.niyaj.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.niyaj.designsystem.theme.IconSizeMini
import com.niyaj.designsystem.theme.LightColor8

@Composable
fun StandardAssistChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    onClick: () -> Unit = {},
) {
    ElevatedAssistChip(
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
                contentDescription = null,
                modifier = Modifier.size(IconSizeMini)
            )
        },
        colors = AssistChipDefaults.elevatedAssistChipColors(
            containerColor = containerColor
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
                    contentDescription = null,
                    modifier = Modifier.size(IconSizeMini)
                )
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
                contentDescription = null,
                modifier = Modifier.size(IconSizeMini)
            )
        },
        trailingIcon = {
            trailingIcon?.let {
                Icon(imageVector = it, contentDescription = null)
            }
        },
        border = AssistChipDefaults.assistChipBorder(
            borderColor = borderColor
        )
    )
}