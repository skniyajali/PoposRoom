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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.Constants.STANDARD_FAB_BUTTON
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun StandardFAB(
    fabVisible: Boolean,
    onFabClick: () -> Unit,
    onClickScroll: () -> Unit,
    modifier: Modifier = Modifier,
    showScrollToTop: Boolean = false,
    fabText: String = Constants.FAB_TEXT,
    fabIcon: ImageVector = PoposIcons.Add,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    fabContainerColor: Color = MaterialTheme.colorScheme.primary,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            ScrollToTop(onClick = onClickScroll, containerColor = containerColor)
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        AnimatedVisibility(
            visible = fabVisible,
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
            ExtendedFloatingActionButton(
                modifier = Modifier.testTag(STANDARD_FAB_BUTTON),
                containerColor = fabContainerColor,
                onClick = onFabClick,
                expanded = !showScrollToTop,
                icon = { Icon(fabIcon, fabText) },
                text = { Text(text = fabText.uppercase()) },
            )
        }
    }
}

@Composable
fun StandardFAB(
    fabVisible: Boolean,
    onFabClick: () -> Unit,
    onClickScroll: () -> Unit,
    modifier: Modifier = Modifier,
    showScrollToTop: Boolean = false,
    fabIcon: ImageVector = PoposIcons.Add,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = slideInVertically(
                initialOffsetY = { fullHeight ->
                    fullHeight
                },
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight ->
                    fullHeight
                },
            ),
        ) {
            ScrollToTop(onClick = onClickScroll, containerColor = containerColor)
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        AnimatedVisibility(
            visible = fabVisible,
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
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = onFabClick,
                content = { Icon(fabIcon, "Fab Icon") },
            )
        }
    }
}

@Composable
fun StandardFABIcon(
    fabVisible: Boolean,
    onFabClick: () -> Unit,
    fabText: String,
    modifier: Modifier = Modifier,
    fabIcon: ImageVector = PoposIcons.Add,
    fabContainerColor: Color = MaterialTheme.colorScheme.secondary,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = fabVisible,
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
        ExtendedFloatingActionButton(
            text = {
                Text(text = fabText.uppercase())
            },
            icon = {
                Icon(
                    imageVector = fabIcon,
                    contentDescription = fabText,
                    modifier = Modifier.clearAndSetSemantics { },
                )
            },
            onClick = onFabClick,
            containerColor = fabContainerColor,
        )
    }
}

@DevicePreviews
@Composable
private fun StandardFABPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        StandardFAB(
            fabVisible = true,
            fabText = "Add New Item",
            onFabClick = {},
            onClickScroll = {},
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun StandardFABOnlyIconPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        StandardFAB(
            fabVisible = true,
            onFabClick = {},
            onClickScroll = {},
            modifier = modifier,
            fabIcon = PoposIcons.Add,
        )
    }
}

@DevicePreviews
@Composable
private fun StandardFABAndIconPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        StandardFABIcon(
            fabVisible = true,
            fabText = "Add New Item",
            onFabClick = {},
            modifier = modifier,
            fabIcon = PoposIcons.Add,
        )
    }
}
