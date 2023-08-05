package com.niyaj.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall

@Composable
fun TextWithIcon(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: String = "",
    icon: ImageVector? = null,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
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
                style = textStyle,
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
fun IconWithText(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    style : TextStyle = MaterialTheme.typography.labelLarge,
    fontWeight: FontWeight = FontWeight.SemiBold,
    textColor : Color = MaterialTheme.colorScheme.onSurface,
    tintColor: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SpaceMini)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = tintColor,
            modifier = iconModifier,
        )

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
fun NoteText(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier.size(SpaceMedium),
    text: String = "",
    icon: ImageVector = Icons.Default.ErrorOutline,
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

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier.size(SpaceMedium),
    text: String,
    icon: ImageVector = Icons.Default.ErrorOutline,
    backgroundColor: Color = MaterialTheme.colorScheme.errorContainer,
    textColor : Color = MaterialTheme.colorScheme.error,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(SpaceMini),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = textColor,
                modifier = iconModifier,
            )

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = fontWeight,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = textColor,
            )
        }
    }
}