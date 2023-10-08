package com.niyaj.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.IconSizeSmall
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconBox(
    text: String,
    icon: ImageVector? = null,
    selected: Boolean = false,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = {},
) {
    val borderStroke =
        if (selected) BorderStroke(1.dp, borderColor) else BorderStroke(0.dp, Color.Transparent)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(2.dp),
        border = borderStroke,
        colors = CardDefaults.cardColors(
            containerColor = LightColor8
        ),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(IconSizeSmall)
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}