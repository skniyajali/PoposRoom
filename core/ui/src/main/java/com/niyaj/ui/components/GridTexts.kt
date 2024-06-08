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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TwoGridTexts(
    modifier: Modifier = Modifier,
    textOne: String,
    textTwo: String,
    isTitle: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier.weight(2.5f, true),
            text = textOne,
            style = textStyle,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            modifier = Modifier.weight(0.5f, true),
            text = textTwo,
            style = textStyle,
            textAlign = TextAlign.End,
            fontWeight = if (isTitle) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
fun TwoGridText(
    modifier: Modifier = Modifier,
    textOne: String,
    textTwo: String,
    textColor: Color = Color.Black,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier,
            text = textOne,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
        )

        Text(
            modifier = Modifier,
            text = textTwo,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Normal,
            color = textColor,
        )
    }
}

@Composable
fun ThreeGridTexts(
    modifier: Modifier = Modifier,
    textOne: String,
    textTwo: String,
    textThree: String,
    isTitle: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier.weight(2f),
            text = textOne,
            style = textStyle,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            modifier = Modifier.weight(0.5f, true),
            text = textTwo,
            style = textStyle,
            textAlign = TextAlign.Start,
            fontWeight = if (isTitle) FontWeight.SemiBold else FontWeight.Normal,
        )

        Text(
            modifier = Modifier.weight(0.5f, true),
            text = textThree,
            style = textStyle,
            textAlign = TextAlign.End,
            fontWeight = if (isTitle) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
fun FourGridTexts(
    modifier: Modifier = Modifier,
    textOne: String,
    textTwo: String,
    textThree: String,
    textFour: String,
    isTitle: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            modifier = Modifier.weight(0.5f),
            text = textOne,
            style = textStyle,
            fontWeight = FontWeight.Bold,
        )

        Text(
            modifier = Modifier.weight(0.5f, true),
            text = textTwo,
            style = textStyle,
            textAlign = TextAlign.Start,
            fontWeight = if (isTitle) FontWeight.Bold else FontWeight.Normal,
        )

        Text(
            modifier = Modifier.weight(1f, true),
            text = textThree,
            style = textStyle,
            textAlign = TextAlign.Start,
            fontWeight = if (isTitle) FontWeight.Bold else FontWeight.Normal,
        )

        Text(
            modifier = Modifier.weight(0.5f, true),
            text = textFour,
            style = textStyle,
            textAlign = TextAlign.End,
            fontWeight = if (isTitle) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
fun TextDivider(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    fontWeight: FontWeight = FontWeight.SemiBold,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f, true),
        )

        Text(
            text = text,
            style = textStyle,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )

        HorizontalDivider(
            modifier = Modifier
                .weight(1f, true),
        )
    }
}

@Composable
fun AnimatedTextDivider(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    fontWeight: FontWeight = FontWeight.SemiBold,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f, true)
                .drawRainbowBorder(1.dp, durationMillis = 5000),
        )

        Text(
            text = text,
            style = textStyle,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )

        HorizontalDivider(
            modifier = Modifier
                .weight(1f, true)
                .drawRainbowBorder(1.dp, durationMillis = 5000),
        )
    }
}

@Composable
fun AnimatedTextDividerDashed(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    fontWeight: FontWeight = FontWeight.SemiBold,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DashedDivider(
            modifier = Modifier
                .weight(1f, true),
            gapWidth = 4.dp,
        )

        Text(
            text = text,
            style = textStyle,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )

        DashedDivider(
            modifier = Modifier
                .weight(1f, true),
            gapWidth = 4.dp,
        )
    }
}

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    dashWidth: Dp = 4.dp,
    dashHeight: Dp = 1.dp,
    gapWidth: Dp = 2.dp,
    color: Color = Color.Gray,
) {
    Canvas(modifier) {
        val pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashWidth.toPx(), gapWidth.toPx()),
            phase = 0f,
        )

        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = dashHeight.toPx(),
        )
    }
}
