package com.niyaj.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.SpaceMini

@Composable
fun StandardButton(
    modifier: Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(SpaceMini),
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = colors,
        modifier = modifier
            .testTag(text)
            .fillMaxWidth()
            .heightIn(ButtonSize),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = iconModifier
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
fun StandardElevatedButton(
    modifier: Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(SpaceMini),
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
    onClick: () -> Unit,
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
                modifier = iconModifier
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
fun StandardOutlinedButton(
    modifier: Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(SpaceMini),
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    border: BorderStroke = ButtonDefaults.outlinedButtonBorder,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = colors,
        border = border,
        modifier = modifier
            .testTag(text)
            .heightIn(ButtonSize),
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = iconModifier
            )
            Spacer(modifier = Modifier.width(SpaceMini))
        }
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}