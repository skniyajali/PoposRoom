package com.niyaj.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.niyaj.designsystem.theme.SpaceSmall

@Composable
fun StandardExpandable(
    modifier: Modifier = Modifier,
    dividerModifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandChanged: (Boolean) -> Unit,
    rowClickable: Boolean = true,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable () -> Unit,
    leading: @Composable (RowScope.() -> Unit)? = null,
    title: @Composable (RowScope.() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null,
    expand: @Composable (RowScope.(Modifier) -> Unit)? = null,
    contentAnimation: FiniteAnimationSpec<IntSize> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessVeryLow
    ),
    expandAnimation: State<Float> = animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "expandAnimation"
    )
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .animateContentSize(animationSpec = contentAnimation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
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
                    IconButton(
                        modifier = Modifier
//                            .alpha(ContentAlpha.medium)
                            .rotate(expandAnimation.value),
                        onClick = {
                            onExpandChanged(!expanded)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Expand Less",
                            tint = iconTint
                        )
                    }
                }
            }
        }

        if (expanded) {
            Divider(modifier = dividerModifier.fillMaxWidth())

            content()
        }
    }

}