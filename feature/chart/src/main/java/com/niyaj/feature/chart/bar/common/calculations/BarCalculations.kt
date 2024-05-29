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

package com.niyaj.feature.chart.bar.common.calculations

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

internal fun getTopLeft(
    index: Int,
    barWidth: Float,
    size: Size,
    yValue: Float,
    yScalableFactor: Float,
) = Offset(
    x = index.times(barWidth.times(1.2F)),
    y = size.height.minus(yValue.times(yScalableFactor)),
)

internal fun getStackedTopLeft(
    index: Int,
    barWidth: Float,
    barHeight: Float,
) = Offset(
    x = index.times(barWidth.times(1.2F)),
    y = (barHeight),
)

internal fun getTopRight(
    index: Int,
    barWidth: Float,
    size: Size,
    yValue: Float,
    yScaleFactor: Float,
) = Offset(
    x = index.plus(1).times(barWidth.times(1.2F)),
    y = size.height.minus(yValue.times(yScaleFactor)),
)
