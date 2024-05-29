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

package com.niyaj.feature.chart.bar.common.component

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

internal fun DrawScope.drawBarLabel(
    xValue: Any,
    barWidth: Float,
    barHeight: Float,
    topLeft: Offset,
    count: Int,
    labelTextColor: Color,
) {
    val heightDisplacement = if (count < 7) barWidth.div(4F) else barWidth.div(2)
    val divisibleFactor = if (count > 10) count else 1
    val textSizeFactor = if (count > 10) 3 else 28

    drawIntoCanvas {
        it.nativeCanvas.apply {
            drawText(
                xValue.toString(),
                topLeft.x.plus(barWidth.div(2)),
                topLeft.y.plus(barHeight.plus(heightDisplacement)),
                Paint().apply {
                    color = labelTextColor.toArgb()
                    textSize = size.width.div(textSizeFactor).div(divisibleFactor)
                    textAlign = Paint.Align.CENTER
                },
            )
        }
    }
}
