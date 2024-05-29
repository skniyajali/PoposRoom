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

package com.niyaj.feature.chart.horizontalbar.common

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.niyaj.feature.chart.horizontalbar.model.HorizontalBarData

internal fun getTopLeft(
    index: Int,
    barHeight: MutableState<Float>,
    size: Size,
    horizontalBarData: HorizontalBarData,
    scalableFactor: Float,
) = Offset(
    y = index.times(barHeight.value.times(1.2F)),
    x = size.width.minus(horizontalBarData.xValue.times(scalableFactor)),
)

internal fun getBottomLeft(
    index: Int,
    barHeight: MutableState<Float>,
    size: Size,
    horizontalBarData: HorizontalBarData,
    scalableFactor: Float,
) = Offset(
    y = index.plus(1).times(barHeight.value.times(1.2F)),
    x = size.width.minus(horizontalBarData.xValue.times(scalableFactor)),

)
