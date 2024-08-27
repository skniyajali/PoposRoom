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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun TextWithCount(
    text: String,
    count: Int,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    onClick()
                },
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconWithText(
            text = text,
            icon = leadingIcon,
            fontWeight = FontWeight.Bold,
        )

        CountBox(count = count.toString())
    }
}

@Composable
fun TextWithCount(
    text: String,
    count: Int,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    onClick()
                },
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconWithText(
            text = text,
            icon = leadingIcon,
            fontWeight = FontWeight.Bold,
        )

        Row {
            trailingText?.let {
                CountBox(count = it)
                Spacer(modifier = Modifier.width(SpaceMini))
            }

            CountBox(
                count = count.toString(),
                backgroundColor = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
fun CountBox(
    count: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = contentColorFor(backgroundColor = backgroundColor),
    style: TextStyle = MaterialTheme.typography.labelSmall,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(backgroundColor)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = count,
            style = style,
            color = textColor,
            modifier = Modifier,
        )
    }
}

@Composable
fun TextWithBorderCount(
    text: String,
    count: Int,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    shape: Shape = RectangleShape,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    tintColor: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    contentColor: Color = contentColorFor(backgroundColor = backgroundColor),
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, shape)
            .padding(SpaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconWithText(
            text = text,
            icon = leadingIcon,
            textStyle = textStyle,
            textColor = contentColor,
            tintColor = tintColor,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            text = count.toString(),
            style = textStyle,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun TextWithCountPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        TextWithCount(
            text = "Text With Count",
            count = 1,
            leadingIcon = PoposIcons.Add,
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun TextWithCountWithIconPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        TextWithCount(
            text = "Text With Count",
            count = 1,
            leadingIcon = PoposIcons.Add,
            trailingText = "1",
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun CountBoxPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CountBox(
            count = "10",
            modifier = modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun TextWithBorderCountPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        TextWithBorderCount(
            text = "Text With Border Count",
            count = 1,
            leadingIcon = PoposIcons.Add,
            modifier = modifier,
        )
    }
}
