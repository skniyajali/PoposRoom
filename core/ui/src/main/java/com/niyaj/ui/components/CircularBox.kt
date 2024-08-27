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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.getAllCapitalizedLetters
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.IconSizeMedium
import com.niyaj.designsystem.theme.IconSizeSmall
import com.niyaj.designsystem.theme.SpaceMini

@Composable
fun CircularBox(
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    text: String? = null,
    showBorder: Boolean = false,
    size: Dp = 40.dp,
    selectedIcon: ImageVector = PoposIcons.Check,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    selectedTint: Color = MaterialTheme.colorScheme.primary,
    unselectedTint: Color = MaterialTheme.colorScheme.secondary,
    borderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
) {
    val availBorder = if (showBorder) borderStroke else null

    val textStyle =
        if (size < 40.dp) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium
    val iconSize = if (size < 40.dp) IconSizeSmall else IconSizeMedium

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                availBorder?.let {
                    Modifier.border(it, CircleShape)
                } ?: Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (text.isNullOrEmpty()) {
            Icon(
                imageVector = if (selected) selectedIcon else icon,
                contentDescription = "",
                tint = if (selected) selectedTint else unselectedTint,
                modifier = Modifier.size(iconSize),
            )
        } else {
            if (selected) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = "",
                    tint = selectedTint,
                    modifier = Modifier.size(iconSize),
                )
            } else {
                Text(
                    text = getAllCapitalizedLetters(text).take(2),
                    style = textStyle,
                )
            }
        }
    }
}

@Composable
fun CircularBoxWithQty(
    text: String,
    qty: Int,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showBorder: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.secondary,
) {
    val quantity = rememberUpdatedState(newValue = qty)

    val textStyle = if (qty == 0) {
        MaterialTheme.typography.labelSmall
    } else {
        MaterialTheme.typography.labelLarge
    }

    val availBorder = if (showBorder && qty != 0) BorderStroke(1.dp, borderColor) else null

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                availBorder?.let {
                    Modifier.border(it, CircleShape)
                } ?: Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (quantity.value == 0) {
            Text(
                text = getAllCapitalizedLetters(text).take(2),
                style = textStyle,
            )
        } else {
            Text(
                text = qty.toString(),
                style = textStyle,
            )

//            AnimatedContent(
//                targetState = quantity.value,
//                label = "",
//            ) {
//                Text(
//                    text = it.toString(),
//                    style = textStyle,
//                )
//            }
        }
    }
}

@Composable
fun CircularBoxWithIcon(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    showBorder: Boolean = false,
    size: Dp = 40.dp,
    selectedIcon: ImageVector = PoposIcons.Check,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    selectedTint: Color = MaterialTheme.colorScheme.primary,
    unselectedTint: Color = MaterialTheme.colorScheme.surfaceTint,
) {
    val availBorder = if (showBorder) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null

    val textStyle =
        if (size <= 40.dp) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium
    val iconSize = if (size <= 40.dp) IconSizeSmall else IconSizeMedium

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                availBorder?.let {
                    Modifier.border(it, CircleShape)
                } ?: Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceMini, Alignment.CenterHorizontally),
        ) {
            if (selected) {
                Icon(
                    imageVector = selectedIcon,
                    contentDescription = "",
                    tint = selectedTint,
                    modifier = Modifier.size(iconSize),
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = "",
                    tint = unselectedTint,
                    modifier = Modifier.size(iconSize),
                )

                Text(text = text, style = textStyle)
            }
        }
    }
}
