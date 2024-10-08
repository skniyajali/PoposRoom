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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun ScrollToTop(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    contentDesc: String = "Scroll To Top",
) {
    FilledTonalIconButton(
        modifier = modifier
            .testTag(contentDesc),
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = containerColor,
        ),
    ) {
        Icon(
            imageVector = PoposIcons.KeyboardArrowUp,
            contentDescription = contentDesc,
        )
    }
}

@Composable
fun ScrollToTop(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    contentDesc: String = "Scroll To Top",
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { fullHeight ->
                fullHeight / 4
            },
        ),
        exit = fadeOut() + slideOutVertically(
            targetOffsetY = { fullHeight ->
                fullHeight / 4
            },
        ),
        label = "FloatingActionButton",
    ) {
        ScrollToTop(
            onClick = onClick,
            containerColor = containerColor,
            contentDesc = contentDesc,
        )
    }
}

@DevicePreviews
@Composable
private fun ScrollToTopPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ScrollToTop(
            onClick = {},
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun ScrollToTopVisiblePreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ScrollToTop(
            visible = true,
            onClick = {},
            modifier = modifier,
        )
    }
}
