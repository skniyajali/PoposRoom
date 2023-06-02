package com.niyaj.poposroom.features.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niyaj.poposroom.features.common.utils.getAllCapitalizedLetters

@Composable
fun CircularBox(
    icon: ImageVector,
    doesSelected: Boolean,
    text: String? = null,
    showBorder: Boolean = false,
    size: Dp = 40.dp,
    selectedIcon: ImageVector = Icons.Default.Check,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    selectedTint: Color = MaterialTheme.colorScheme.primary,
    unselectedTint: Color = MaterialTheme.colorScheme.surfaceTint,
) {
    val availBorder = if (showBorder) BorderStroke(1.dp, MaterialTheme.colorScheme.inversePrimary) else null

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(availBorder?.let {
                    Modifier.border(it, CircleShape)
                } ?: Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (text.isNullOrEmpty()) {
            Icon(
                imageVector = if (doesSelected) selectedIcon else icon,
                contentDescription = "",
                tint = if (doesSelected) selectedTint else unselectedTint,
            )
        }else {
            if (doesSelected) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = "",
                    tint = selectedTint,
                )
            }else {
                Text(
                    text = getAllCapitalizedLetters(text).take(2),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}