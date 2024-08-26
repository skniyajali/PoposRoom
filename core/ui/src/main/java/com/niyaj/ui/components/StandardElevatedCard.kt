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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.ui.utils.DevicePreviews

@OptIn(ExperimentalFoundationApi::class)
@DevicePreviews
@Composable
fun StandardElevatedCard(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    testTag: String = "testTag",
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    borderStroke: BorderStroke? = if (selected) border else null,
    containerColor: Color = MaterialTheme.colorScheme.background,
    elevation: Dp = SpaceMini,
    content: @Composable () -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier
            .testTag(testTag)
            .semantics {
                this.selected = selected
            }
            .then(
                borderStroke?.let {
                    Modifier.border(it, CardDefaults.elevatedShape)
                } ?: Modifier,
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = containerColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = elevation,
        ),
    ) {
        content()
    }
}

@DevicePreviews
@Composable
private fun StandardElevatedCardPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        StandardElevatedCard(
            modifier = modifier,
            selected = true,
            content = {
                Text(text = "Standard Elevated Card")
            },
        )
    }
}
