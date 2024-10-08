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

package com.niyaj.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.utils.drawAnimatedBorder

@Composable
fun PoposTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.textShape,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    val colors = ButtonDefaults.textButtonColors(
        contentColor = contentColor,
    )

    TextButton(
        onClick = onClick,
        modifier = modifier.testTag(text),
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
    ) {
        Text(
            text = text,
            style = textStyle,
            fontWeight = fontWeight,
        )

        icon?.let {
            Spacer(modifier = Modifier.width(SpaceMini))

            Icon(
                imageVector = icon,
                contentDescription = text,
            )
        }
    }
}

@Composable
fun PoposButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    btnHeight: Dp = ButtonSize,
    shape: Shape = RoundedCornerShape(SpaceMini),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    style: TextStyle = MaterialTheme.typography.labelLarge,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier
            .testTag(text)
            .heightIn(btnHeight),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }

        Text(
            text = text.uppercase(),
            style = style,
        )
    }
}

@Composable
fun PoposButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    btnHeight: Dp = ButtonSize,
    shape: Shape = RoundedCornerShape(SpaceMini),
    style: TextStyle = MaterialTheme.typography.labelLarge,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = color,
        ),
        modifier = modifier
            .testTag(text)
            .heightIn(btnHeight),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }

        Text(
            text = text.uppercase(),
            style = style,
        )
    }
}

@Composable
fun PoposElevatedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(SpaceMini),
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
) {
    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = colors,
        modifier = modifier
            .testTag(text)
            .heightIn(ButtonSize),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text.plus("button"),
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
fun PoposIconTextButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    contentDescription: String? = icon.name,
    enabled: Boolean = true,
    btnHeight: Dp = ButtonSize,
    shape: Shape = RoundedCornerShape(SpaceMini),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    style: TextStyle = MaterialTheme.typography.labelLarge,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = colors,
        contentPadding = contentPadding,
        modifier = modifier
            .heightIn(btnHeight),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
        )

        text?.let {
            Spacer(modifier = Modifier.width(SpaceMini))

            Text(
                text = text.uppercase(),
                style = style,
            )
        }
    }
}

@Composable
fun PoposOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    btnHeight: Dp = ButtonSize,
    shape: Shape = RoundedCornerShape(SpaceMini),
    color: Color = MaterialTheme.colorScheme.secondary,
    style: TextStyle = MaterialTheme.typography.labelLarge,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color,
            disabledContentColor = color.copy(0.38f),
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = if (enabled) SolidColor(color) else SolidColor(color.copy(0.38f)),
        ),
        modifier = modifier
            .testTag(text)
            .heightIn(btnHeight),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }
        Text(
            text = text.uppercase(),
            style = style,
        )
    }
}

@Composable
fun PoposOutlinedDropdownButton(
    text: String,
    leadingIcon: ImageVector,
    trailingIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    btnHeight: Dp = ButtonSize,
    showAnimatedBorder: Boolean = false,
    shape: Shape = RoundedCornerShape(SpaceMini),
    color: Color = MaterialTheme.colorScheme.secondary,
    style: TextStyle = MaterialTheme.typography.labelLarge,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = color,
            disabledContentColor = color.copy(0.38f),
        ),
        border = if (showAnimatedBorder) {
            null
        } else {
            ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp,
                brush = if (enabled) SolidColor(color) else SolidColor(color.copy(0.38f)),
            )
        },
        modifier = modifier
            .testTag(text)
            .fillMaxWidth()
            .heightIn(btnHeight)
            .then(
                if (showAnimatedBorder) {
                    Modifier.drawAnimatedBorder(1.dp, durationMillis = 200, shape = shape)
                } else {
                    Modifier
                },
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = text,
                )
                Text(
                    text = text.uppercase(),
                    style = style,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            Icon(
                imageVector = trailingIcon,
                contentDescription = text,
            )
        }
    }
}

@Composable
fun PoposIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = icon.name,
    enabled: Boolean = true,
    btnHeight: Dp = 40.dp,
    shape: Shape = RoundedCornerShape(SpaceMini),
    containerColor: Color = IconButtonDefaults.iconButtonColors().containerColor,
    contentColor: Color = contentColorFor(backgroundColor = containerColor),
) {
    IconButton(
        modifier = modifier
            .heightIn(btnHeight)
            .background(if (enabled) containerColor else containerColor.copy(0.4f), shape),
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        enabled = enabled,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun PoposTonalIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.outlineVariant,
    contentColor: Color = contentColorFor(backgroundColor = containerColor),
    shape: Shape = RoundedCornerShape(SpaceMini),
    contentDescription: String = "iconDesc",
) {
    FilledTonalIconButton(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        enabled = enabled,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun PoposOutlinedIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(
        contentColor = borderColor,
    ),
    shape: Shape = RoundedCornerShape(SpaceMini),
    contentDescription: String = "iconDesc",
) {
    OutlinedIconButton(
        modifier = modifier,
        onClick = onClick,
        shape = shape,
        enabled = enabled,
        colors = colors,
        border = BorderStroke(1.dp, borderColor),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
        )
    }
}

@Preview
@Composable
private fun PoposIconButtonEnabledPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        Surface {
            PoposIconButton(
                icon = PoposIcons.Share,
                onClick = {},
                modifier = modifier,
            )
        }
    }
}

@Preview
@Composable
private fun PoposIconButtonDisabledPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        Surface {
            PoposIconButton(
                modifier = modifier,
                icon = PoposIcons.Share,
                onClick = {},
                enabled = false,
            )
        }
    }
}
