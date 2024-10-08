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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun StandardExpandable(
    expanded: Boolean,
    onExpandChanged: (Boolean) -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dividerModifier: Modifier = Modifier,
    rowClickable: Boolean = true,
    showExpandIcon: Boolean = true,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    leading: @Composable (RowScope.() -> Unit)? = null,
    title: @Composable (RowScope.() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null,
    expand: @Composable (RowScope.(Modifier) -> Unit)? = null,
    contentDesc: String = "Item",
    contentAnimation: FiniteAnimationSpec<IntSize> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessVeryLow,
    ),
    expandAnimation: State<Float> = animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "expandAnimation",
    ),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .animateContentSize(animationSpec = contentAnimation),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = rowClickable,
                ) {
                    onExpandChanged(!expanded)
                }
                .padding(horizontal = SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                leading?.let {
                    leading()
                }
                title?.let {
                    title()
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                trailing?.let {
                    trailing()
                }

                expand?.let { expand ->
                    expand(Modifier.rotate(expandAnimation.value))
                } ?: run {
                    AnimatedVisibility(showExpandIcon) {
                        IconButton(
                            modifier = Modifier
                                .rotate(expandAnimation.value),
                            onClick = {
                                onExpandChanged(!expanded)
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.ArrowDropDown,
                                contentDescription = contentDesc.plus("Expand Less"),
                                tint = iconTint,
                            )
                        }
                    }
                }
            }
        }

        if (expanded) {
            HorizontalDivider(modifier = dividerModifier.fillMaxWidth())

            content()
        }
    }
}

@DevicePreviews
@Composable
private fun StandardExpandablePreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        StandardExpandable(
            expanded = true,
            modifier = modifier,
            title = {
                Text(
                    text = "Standard Expandable",
                    style = MaterialTheme.typography.labelLarge,
                )
            },
            leading = {
                Icon(
                    imageVector = PoposIcons.Add,
                    contentDescription = "Close icon",
                )
            },
            onExpandChanged = {},
            content = {
                Text(text = "Standard Expandable Content")
            },
        )
    }
}
