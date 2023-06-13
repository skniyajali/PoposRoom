package com.niyaj.poposroom.features.common.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.niyaj.poposroom.features.common.ui.theme.SpaceMedium
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector? = null,
    textColor : Color = MaterialTheme.colorScheme.onSurface,
    tintColor: Color = MaterialTheme.colorScheme.onSurface,
    isTitle: Boolean = false,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if(text.isNotEmpty()) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = tintColor,
                    modifier = iconModifier,
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            }

            Text(
                text = text,
                fontFamily = if(text.startsWith("Email") || text.startsWith("Password")) FontFamily.Monospace else null,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if(isTitle) FontWeight.SemiBold else fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )
        }
    }
}


@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: AnnotatedString,
    icon: ImageVector? = null,
    isTitle: Boolean = false,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if(text.isNotEmpty()) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text.text,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = iconModifier,
                )
                Spacer(modifier = Modifier.width(SpaceMini))
            }
            Text(
                text = text,
                fontFamily = if(text.startsWith("Email") || text.startsWith("Password")) FontFamily.Monospace else null,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if(isTitle) FontWeight.SemiBold else fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


@Composable
fun TextWithTitle(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector? = null,
    style : TextStyle = MaterialTheme.typography.labelMedium,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textColor : Color = MaterialTheme.colorScheme.onSurface,
    tintColor: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if(text.isNotEmpty()) {
            Text(
                text = text,
                style = style,
                fontWeight = fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )
            icon?.let {
                Spacer(modifier = Modifier.width(SpaceMini))

                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = tintColor,
                    modifier = iconModifier,
                )
            }
        }
    }
}

@Composable
fun TopBarTitle(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    style : TextStyle = MaterialTheme.typography.displayMedium,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textColor : Color = MaterialTheme.colorScheme.onPrimary,
    tintColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Row(
        modifier = modifier,
    ) {
        icon?.let {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = tintColor,
                modifier = iconModifier,
            )

            Spacer(modifier = Modifier.width(SpaceMini))
        }

        Text(
            text = text,
            style = style,
            fontWeight = fontWeight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = textColor,
        )
    }
}

@Composable
fun NoteText(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier.size(SpaceMedium),
    text: String = "",
    icon: ImageVector = Icons.Default.Note,
    color : Color = MaterialTheme.colorScheme.secondary,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if(text.isNotEmpty()) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = iconModifier,
            )
            Spacer(modifier = Modifier.width(SpaceMini))

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = color,
            )
        }
    }
}