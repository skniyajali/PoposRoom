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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.core.ui.R
import com.niyaj.designsystem.components.PoposElevatedButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun ItemNotAvailable(
    modifier: Modifier = Modifier,
    btnModifier: Modifier = Modifier,
    text: String = "",
    buttonText: String = "",
    showImage: Boolean = true,
    icon: ImageVector = if (buttonText.contains("CREATE", true) ||
        buttonText.contains("ADD", true)
    ) {
        PoposIcons.Add
    } else {
        PoposIcons.Edit
    },
    image: Painter = painterResource(id = R.drawable.emptystate),
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (showImage) {
                Image(
                    painter = image,
                    contentDescription = "No data available",
                    modifier = Modifier
                        .weight(2f, true),
                )
                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(SpaceMedium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = SpaceLarge),
                )

                if (buttonText.isNotEmpty()) {
                    PoposElevatedButton(
                        modifier = btnModifier
                            .testTag(buttonText),
                        text = buttonText,
                        icon = icon,
                        onClick = onClick,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                        shape = CutCornerShape(4.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ItemNotAvailableHalf(
    modifier: Modifier = Modifier,
    btnModifier: Modifier = Modifier,
    text: String = "",
    buttonText: String = "",
    showImage: Boolean = true,
    icon: ImageVector = if (buttonText.contains("CREATE", true) ||
        buttonText.contains("ADD", true)
    ) {
        PoposIcons.Add
    } else {
        PoposIcons.Edit
    },
    image: Painter = painterResource(id = R.drawable.emptystate),
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .testTag("ItemNotAvailableHalf")
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showImage) {
            Image(
                painter = image,
                contentDescription = "No data available",
                modifier = Modifier
                    .weight(1.5f, true),
            )
        }

        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = SpaceLarge),
                )

                if (buttonText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(SpaceMedium))
                    PoposElevatedButton(
                        modifier = btnModifier,
                        text = buttonText,
                        icon = icon,
                        onClick = onClick,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                        ),
                        shape = CutCornerShape(4.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyImportScreen(
    modifier: Modifier = Modifier,
    btnModifier: Modifier = Modifier,
    text: String,
    buttonText: String,
    note: String? = null,
    showImage: Boolean = true,
    icon: ImageVector = PoposIcons.FileOpen,
    image: Painter = painterResource(id = R.drawable.openfile),
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (showImage) {
                Image(
                    painter = image,
                    contentDescription = "No data available",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.size(300.dp),
                )
            }

            Text(
                text = text,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error,
            )

            note?.let {
                InfoText(text = it)
            }

            if (buttonText.isNotEmpty()) {
                PoposElevatedButton(
                    modifier = btnModifier,
                    text = buttonText,
                    icon = icon,
                    onClick = onClick,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                    shape = CutCornerShape(4.dp),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ItemNotAvailableHalfPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        Surface {
            ItemNotAvailableHalf(
                modifier = modifier,
                text = "Item not available Half",
                buttonText = "Create New Item",
            )
        }
    }
}

@DevicePreviews
@Composable
private fun ItemNotAvailablePreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        Surface {
            ItemNotAvailable(
                modifier = modifier,
                text = "Item not available",
                buttonText = "Create New Item",
            )
        }
    }
}

@DevicePreviews
@Composable
private fun EmptyImportScreenPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        Surface {
            EmptyImportScreen(
                modifier = modifier,
                text = "Make sure to open item.json file",
                buttonText = "Open File",
                note = "Make sure to import category before importing products",
            )
        }
    }
}
