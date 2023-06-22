package com.niyaj.poposroom.features.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.niyaj.poposroom.features.common.ui.theme.IconSizeMedium
import com.niyaj.poposroom.features.common.ui.theme.IconSizeSmall
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
    val availBorder = if (showBorder) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null

    val textStyle =
        if (size < 40.dp) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium
    val iconSize = if (size < 40.dp) IconSizeSmall else IconSizeMedium

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
                modifier = Modifier.size(iconSize)
            )
        } else {
            if (doesSelected) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = "",
                    tint = selectedTint,
                    modifier = Modifier.size(iconSize)
                )
            } else {
                Text(
                    text = getAllCapitalizedLetters(text).take(2),
                    style = textStyle
                )
            }
        }
    }
}

@Composable
fun CircularBoxWithQty(
    text: String,
    qty: Int,
    size: Dp = 40.dp,
    showBorder: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    borderColor: Color = MaterialTheme.colorScheme.secondary,
) {
    val textStyle = if (qty == 0) MaterialTheme.typography.labelSmall
    else MaterialTheme.typography.labelLarge

    val availBorder = if (showBorder && qty != 0) BorderStroke(1.dp, borderColor) else null

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
        if (qty == 0) {
            Text(
                text = getAllCapitalizedLetters(text).take(2),
                style = textStyle
            )
        } else {
            Text(
                text = qty.toString(),
                style = textStyle
            )
        }
    }
}


@Composable
fun CircularBoxWithIcon(
    text: String,
    icon: ImageVector,
    doesSelected: Boolean,
    showBorder: Boolean = false,
    size: Dp = 40.dp,
    selectedIcon: ImageVector = Icons.Default.Check,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    selectedTint: Color = MaterialTheme.colorScheme.primary,
    unselectedTint: Color = MaterialTheme.colorScheme.surfaceTint,
) {
    val availBorder = if (showBorder) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null

    val textStyle =
        if (size < 40.dp) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium
    val iconSize = if (size < 40.dp) IconSizeSmall else IconSizeMedium

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (doesSelected) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = "",
                    tint = selectedTint,
                    modifier = Modifier.size(iconSize)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    tint = unselectedTint,
                    modifier = Modifier.size(iconSize)
                )

                Text(text = text, style = textStyle)
            }

        }
    }
}