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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.Olive
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun PhoneNoCountBox(
    count: Int,
    modifier: Modifier = Modifier,
    totalCount: Int = 10,
    backgroundColor: Color = Color.Transparent,
    color: Color = Color.Gray,
    errorColor: Color = Olive,
) {
    val countColor = if (count <= 10) color else errorColor
    val textColor = if (count >= 10) color else errorColor

    AnimatedVisibility(
        visible = count != 0,
        enter = fadeIn(),
        exit = fadeOut(),
        label = "Phone No Count Box",
    ) {
        Card(
            modifier = modifier.background(backgroundColor),
            shape = RoundedCornerShape(2.dp),
        ) {
            Row(
                modifier = Modifier.padding(2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = countColor,
                )
                Text(
                    text = "/",
                    fontFamily = FontFamily.Cursive,
                    color = color,
                )
                Text(
                    text = totalCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun PhoneNoCountBoxPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        PhoneNoCountBox(
            count = 4,
            modifier = modifier,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    }
}
