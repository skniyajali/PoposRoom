package com.niyaj.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.ProfilePictureSizeExtraLarge
import com.niyaj.designsystem.theme.SpaceMedium

@Composable
fun ItemNotAvailable(
    modifier: Modifier = Modifier,
    btnModifier: Modifier = Modifier,
    text: String = "",
    buttonText: String = "",
    showImage: Boolean = true,
    icon: ImageVector = if (buttonText.contains("CREATE", true)
        || buttonText.contains("ADD", true)
    ) Icons.Default.Add else Icons.Default.Edit,
    image: Painter = painterResource(id = R.drawable.emptystate),
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showImage) {
            Image(
                painter = image,
                contentDescription = "No data available",
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
        }

        Text(
            text = text,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        if (buttonText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(SpaceMedium))
            StandardElevatedButton(
                modifier = btnModifier,
                text = buttonText,
                icon = icon,
                onClick = onClick,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = CutCornerShape(4.dp),
            )
        }
    }
}


@Composable
fun ImportScreen(
    modifier: Modifier = Modifier,
    btnModifier: Modifier = Modifier,
    text: String,
    buttonText: String,
    showImage: Boolean = true,
    icon: ImageVector = Icons.Default.FileOpen,
    image: Painter = painterResource(id = R.drawable.openfile),
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showImage) {
            Image(
                painter = image,
                contentDescription = "No data available",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.size(300.dp),
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
        }

        Text(
            text = text,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        if (buttonText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(SpaceMedium))
            StandardElevatedButton(
                modifier = btnModifier,
                text = buttonText,
                icon = icon,
                onClick = onClick,
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = CutCornerShape(4.dp),
            )
        }
    }
}