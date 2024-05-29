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

package com.niyaj.feature.chart.common.calculations

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

private const val BOUND_FACTOR = 1.2F

internal fun dataToOffSet(
    index: Int,
    bound: Float,
    size: Size,
    data: Float,
    yScaleFactor: Float,
): Offset {
    val startX = index.times(bound.times(BOUND_FACTOR))
    val endX = index.plus(1).times(bound.times(BOUND_FACTOR))
    val y = size.height.minus(data.times(yScaleFactor))
    return Offset(((startX.plus(endX)).div(2F)), y)
}
