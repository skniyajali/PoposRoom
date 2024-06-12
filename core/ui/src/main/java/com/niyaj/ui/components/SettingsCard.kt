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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    leadingColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    leadingTint: Color = MaterialTheme.colorScheme.tertiary,
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SpaceMini))
            .clickable {
                onClick()
            },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
            )
        },
        supportingContent = if (subtitle.isEmpty()) {
            null
        } else {
            (
                {
                    Text(
                        text = subtitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                )
        },
        leadingContent = {
            CircularBox(
                icon = icon,
                doesSelected = false,
                showBorder = false,
                backgroundColor = leadingColor,
                unselectedTint = leadingTint,
            )
        },
        trailingContent = {
            Icon(
                imageVector = PoposIcons.ArrowRightAlt,
                contentDescription = "Arrow right icon",
            )
        },
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
            headlineColor = contentColorFor(backgroundColor = containerColor),
            supportingColor = contentColorFor(backgroundColor = containerColor),
            overlineColor = contentColorFor(backgroundColor = containerColor),
            trailingIconColor = contentColorFor(backgroundColor = containerColor),
        ),
    )
}

@DevicePreviews
@Composable
private fun SettingsCardPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        SettingsCard(
            modifier = modifier,
            title = "Import Items",
            subtitle = "Click Here to Import Items",
            icon = PoposIcons.Import,
            onClick = {},
        )
    }
}