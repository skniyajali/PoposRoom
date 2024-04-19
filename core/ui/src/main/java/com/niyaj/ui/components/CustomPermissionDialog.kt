package com.niyaj.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.IconSizeExtraLarge
import com.niyaj.designsystem.theme.ProfilePictureSizeExtraLarge
import com.niyaj.designsystem.theme.ProfilePictureSizeMedium
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPermissionDialog(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    icon: ImageVector,
    shouldShowRationale: Boolean,
    onClickRequestPermission: () -> Unit,
    onClickOpenSettings: () -> Unit,
    onDismissRequest: () -> Unit,
    image: ImageVector? = null,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) {
    val tint = if (shouldShowRationale)
        MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
    val btnText = if (shouldShowRationale) "Grant Permission" else "Open Settings"
    val btnAction = if (shouldShowRationale) onClickRequestPermission else onClickOpenSettings

    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
            tonalElevation = tonalElevation,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (image == null) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .size(ProfilePictureSizeMedium)
                            .align(Alignment.CenterHorizontally)
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)

                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = tint,
                            modifier = Modifier
                                .size(IconSizeExtraLarge)
                                .align(Alignment.Center)
                        )
                    }
                } else {
                    Image(
                        imageVector = image,
                        contentDescription = title,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .size(ProfilePictureSizeExtraLarge)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(bottom = SpaceSmallMax, start = SpaceSmall, end = SpaceSmall)
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = SpaceMedium, start = SpaceSmall, end = SpaceSmall)
                )

                if (shouldShowRationale) {
                    TextButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                    ) {
                        Text(
                            text = "Maybe Later",
                            color = tint,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(tint)
                        .clickable { btnAction() }
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = btnText,
                        color = contentColorFor(backgroundColor = tint),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}