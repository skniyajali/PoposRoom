/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall

@Composable
fun StandardFAB(
    fabVisible: Boolean,
    onFabClick: () -> Unit,
    onClickScroll: () -> Unit,
    showScrollToTop: Boolean = false,
    fabText: String = Constants.FAB_TEXT,
    fabIcon: ImageVector = PoposIcons.Add,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    fabContainerColor: Color = MaterialTheme.colorScheme.primary,
) {
    Column(
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
    showScrollToTop: Boolean = false,
    fabIcon: ImageVector = PoposIcons.Add,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    onFabClick: () -> Unit,
    onClickScroll: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = slideInVertically(
                initialOffsetY = { fullHeight ->
                    fullHeight
                }
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight ->
                    fullHeight
                }
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
                }
            ),
            exit = fadeOut() + slideOutVertically(
                targetOffsetY = { fullHeight ->
                    fullHeight / 4
                }
            ),
            label = "FloatingActionButton"
        ) {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = onFabClick,
                content = { Icon(fabIcon, "Fab Icon") }
            )
        }
    }
}

@Composable
fun StandardFABIcon(
    fabVisible: Boolean,
    showScrollToTop: Boolean = false,
    onFabClick: () -> Unit,
    onClickScroll: () -> Unit,
    scrollText: String,
    fabText: String,
    fabIcon: ImageVector = PoposIcons.Add,
    scrollIcon: ImageVector = PoposIcons.ArrowUpward,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    fabContainerColor: Color = MaterialTheme.colorScheme.secondary,
) {
    val updatedState = rememberUpdatedState(showScrollToTop)
    val animatedColor = animateColorAsState(
        targetValue = if (showScrollToTop) containerColor else fabContainerColor,
        label = "",
    )
    val heightDp = animateDpAsState(
        targetValue = if (showScrollToTop) 40.dp else 56.dp,
        label = "",
    )

    val widthDp = animateDpAsState(
        targetValue = if (showScrollToTop) 30.dp else 56.dp,
        label = "",
    )

    AnimatedVisibility(
        visible = fabVisible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { fullHeight ->
                fullHeight * 2
            },
        ),
        exit = fadeOut(
            animationSpec = keyframes {
                this.durationMillis = 120
            },
        ),
        label = "FloatingActionButton",
    ) {
        ElevatedButton(
            onClick = if (updatedState.value) onClickScroll else onFabClick,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = animatedColor.value,
                contentColor = contentColorFor(animatedColor.value)
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        stiffness = Spring.StiffnessVeryLow,
                        dampingRatio = Spring.DampingRatioNoBouncy,
                    )
                )
                .defaultMinSize(
                    minWidth = widthDp.value,
                    minHeight = heightDp.value,
                ),
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = if (updatedState.value) scrollIcon else fabIcon,
                    contentDescription = null,
                    modifier = Modifier.clearAndSetSemantics { }
                )

                Spacer(Modifier.width(ExtendedFabEndIconPadding))
                Text(text = (if (updatedState.value) scrollText else fabText).uppercase())
            }
        }
    }
}

private val ExtendedFabEndIconPadding = 12.dp