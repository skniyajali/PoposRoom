package com.niyaj.poposroom.features.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.niyaj.poposroom.features.common.ui.theme.IconSizeSmall

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
                modifier = Modifier.size(IconSizeSmall)
            )
        },
        colors = AssistChipDefaults.elevatedAssistChipColors(
            containerColor = containerColor
        )
    )
}


@Composable
fun StandardOutlinedAssistChip(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    borderColor: Color = MaterialTheme.colorScheme.primary,
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
                modifier = Modifier.size(IconSizeSmall)
            )
        },
        border = AssistChipDefaults.assistChipBorder(
            borderColor = borderColor
        )
    )
}