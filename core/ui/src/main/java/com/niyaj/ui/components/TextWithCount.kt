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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall

@Composable
fun TextWithCount(
    modifier: Modifier = Modifier,
    text: String,
    leadingIcon: ImageVector? = null,
    count: Int,
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
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconWithText(
            text = text,
            icon = leadingIcon,
            fontWeight = FontWeight.Bold
        )

        CountBox(count = count.toString())
    }
}


@Composable
fun TextWithCount(
    modifier: Modifier = Modifier,
    text: String,
    count: Int,
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
                }
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconWithText(
            text = text,
            icon = leadingIcon,
            fontWeight = FontWeight.Bold
        )

        Row {
            trailingText?.let {
                CountBox(count = it)
                Spacer(modifier = Modifier.width(SpaceMini))
            }

            CountBox(count = count.toString())
        }
    }
}


@Composable
fun CountBox(
    modifier: Modifier = Modifier,
    count: String,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    backgroundColor: Color = MaterialTheme.colorScheme.primary
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
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier
        )
    }
}

@Composable
fun TextWithBorderCount(
    modifier: Modifier = Modifier,
    text: String,
    leadingIcon: ImageVector? = null,
    count: Int,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(SpaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconWithText(
            text = text,
            icon = leadingIcon,
            fontWeight = FontWeight.SemiBold,
            tintColor = MaterialTheme.colorScheme.onTertiaryContainer,
        )

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
        )
    }
}