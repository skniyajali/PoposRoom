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

package com.niyaj.feature.reports.components

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.feature.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.ui.event.UiState

class ProductWiseReportPreviewProvider :
    PreviewParameterProvider<UiState<List<HorizontalBarData>>> {
    override val values: Sequence<UiState<List<HorizontalBarData>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(ProductWiseReportPreviewData.productWiseReport),
        )
}

class ReportBarDataPreviewProvider :
    PreviewParameterProvider<UiState<List<HorizontalBarData>>> {
    override val values: Sequence<UiState<List<HorizontalBarData>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(ProductWiseReportPreviewData.lastDaysReports),
        )
}

object ProductWiseReportPreviewData {

    val productWiseReport: List<HorizontalBarData> = List(10) {
        HorizontalBarData(
            xValue = it.times((1..20).random()).toFloat(),
            yValue = "Product $it",
        )
    }

    val lastDaysReports: List<HorizontalBarData> = List(5) {
        HorizontalBarData(
            xValue = it.plus(1).times((4000..5000).random()).toFloat(),
            yValue = "Date $it",
        )
    }
}
