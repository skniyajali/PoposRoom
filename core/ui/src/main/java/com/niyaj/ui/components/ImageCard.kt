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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import java.io.File

@Composable
fun ImageCard(
    @DrawableRes
    defaultImage: Int,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageName: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    size: DpSize = DpSize(100.dp, 100.dp),
) = trace("ImageCard") {
    val iconSize = 24.dp
    val offsetInPx = LocalDensity.current.run { (iconSize / 2).roundToPx() }

    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD),
            ),
        )
    }
    val borderWidth = if (imageName.isNotEmpty()) 4.dp else 0.dp

    val context = LocalContext.current

    val printLogoRequest = ImageRequest
        .Builder(context)
        .data(File(context.filesDir, imageName))
        .crossfade(enable = true)
        .placeholder(defaultImage)
        .error(defaultImage)
        .build()

    Box(
        modifier = modifier
            .padding((iconSize / 2)),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.size(size),
            shape = RoundedCornerShape(SpaceSmall),
            border = BorderStroke(borderWidth, rainbowColorsBrush),
        ) {
            SubcomposeAsyncImage(
                model = printLogoRequest,
                contentDescription = "Print Logo",
                loading = { CircularProgressIndicator() },
                contentScale = contentScale,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally),
            )
        }

        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .offset {
                    IntOffset(x = +offsetInPx, y = -offsetInPx)
                }
                .clip(CircleShape)
                .size(iconSize)
                .align(Alignment.TopEnd),
        ) {
            Icon(
                imageVector = PoposIcons.Edit,
                contentDescription = "Edit Icon",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(iconSize),
            )
        }
    }
}
