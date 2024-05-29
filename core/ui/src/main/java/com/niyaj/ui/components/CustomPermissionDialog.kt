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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.niyaj.common.utils.Constants.BLUETOOTH_PER_DENY_TEXT
import com.niyaj.common.utils.Constants.BLUETOOTH_PER_RATIONAL_TEXT
import com.niyaj.core.ui.R
import com.niyaj.designsystem.components.PoposTextButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.IconSizeExtraLarge
import com.niyaj.designsystem.theme.ProfilePictureSizeExtraLarge
import com.niyaj.designsystem.theme.ProfilePictureSizeMedium
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.designsystem.theme.gradient2

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HandleBluetoothPermissionState(
    multiplePermissionsState: MultiplePermissionsState,
    onSuccessful: @Composable () -> Unit,
    onError: @Composable (Boolean) -> Unit,
) {
    if (multiplePermissionsState.allPermissionsGranted) {
        onSuccessful.invoke()
    } else {
        onError.invoke(multiplePermissionsState.shouldShowRationale)
    }
}

@Composable
fun BluetoothPermissionDialog(
    modifier: Modifier = Modifier,
    shouldShowRationale: Boolean,
    onClickRequestPermission: () -> Unit,
    onClickOpenSettings: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val text = if (shouldShowRationale) BLUETOOTH_PER_RATIONAL_TEXT else BLUETOOTH_PER_DENY_TEXT

    CustomPermissionDialog(
        modifier = modifier,
        title = "Nearby Devices",
        text = text,
        icon = PoposIcons.NearbyOff,
        image = ImageVector.vectorResource(R.drawable.bluetooth_icon),
        shouldShowRationale = shouldShowRationale,
        onClickRequestPermission = onClickRequestPermission,
        onClickOpenSettings = onClickOpenSettings,
        onDismissRequest = onDismissRequest,
    )
}

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
    val tint = if (shouldShowRationale) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.error
    }
    val btnText = if (shouldShowRationale) "Grant Permission" else "Open Settings"
    val btnAction = if (shouldShowRationale) onClickRequestPermission else onClickOpenSettings

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
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
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                                CircleShape,
                            ),
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = tint,
                            modifier = Modifier
                                .size(IconSizeExtraLarge)
                                .align(Alignment.Center),
                        )
                    }
                } else {
                    Image(
                        imageVector = image,
                        contentDescription = title,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .size(ProfilePictureSizeExtraLarge),
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .padding(bottom = SpaceSmallMax, start = SpaceSmall, end = SpaceSmall),
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = SpaceMedium, start = SpaceSmall, end = SpaceSmall),
                )

                if (shouldShowRationale) {
                    PoposTextButton(
                        text = "Maybe Later",
                        onClick = onDismissRequest,
                        fontWeight = FontWeight.SemiBold,
                        contentColor = tint,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(tint)
                        .clickable { btnAction() }
                        .padding(18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = btnText,
                        color = contentColorFor(backgroundColor = tint),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onConfirmText: String = "Delete",
    onDismissText: String = "Maybe Later",
    boxColor: Brush = gradient2,
    tint: Color = MaterialTheme.colorScheme.error,
    shape: Shape = AlertDialogDefaults.shape,
    containerColor: Color = AlertDialogDefaults.containerColor,
    tonalElevation: Dp = AlertDialogDefaults.TonalElevation,
) {
    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.delete_animation_two),
    )

    BasicAlertDialog(
        onDismissRequest = onDismiss,
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
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(boxColor),
                )

                Box(
                    modifier = Modifier
                        .offset(y = (-45).dp)
                        .size(90.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            CircleShape,
                        ),
                ) {
                    LottieAnimation(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(150.dp),
                        composition = lottieComposition,
                        iterations = LottieConstants.IterateForever,
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .offset(y = (-20).dp),
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = SpaceSmall, start = SpaceSmall, end = SpaceSmall),
                )

                PoposTextButton(
                    modifier = Modifier
                        .padding(bottom = SpaceSmall),
                    text = onDismissText,
                    onClick = onDismiss,
                    fontWeight = FontWeight.SemiBold,
                    contentColor = MaterialTheme.colorScheme.secondary,
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(tint)
                        .clickable { onConfirm() }
                        .padding(18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = onConfirmText,
                        color = contentColorFor(backgroundColor = tint),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}
