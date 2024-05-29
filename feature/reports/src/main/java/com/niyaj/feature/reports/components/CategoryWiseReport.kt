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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor2
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CategoryWiseReport
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState

@Composable
fun CategoryWiseReport(
    categoryState: UiState<List<CategoryWiseReport>>,
    orderType: String,
    reportExpanded: Boolean,
    selectedCategory: String,
    onCategoryExpandChanged: (String) -> Unit,
    onExpandChanged: () -> Unit,
    onClickOrderType: (String) -> Unit,
    onProductClick: (productId: Int) -> Unit,
) = trace("CategoryWiseReport") {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightColor2,
        ),
    ) {
        StandardExpandable(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = reportExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Category Wise Report",
                    icon = PoposIcons.Category,
                )
            },
            trailing = {
                OrderTypeDropdown(
                    text = orderType.ifEmpty { "All" },
                ) {
                    onClickOrderType(it)
                }
            },
            expand = null,
            contentDesc = "Category wise report",
            content = {
                Crossfade(
                    targetState = categoryState,
                    label = "CategoryState",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Category wise report not available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            CategoryWiseReportCard(
                                report = state.data,
                                selectedCategory = selectedCategory,
                                onExpandChanged = onCategoryExpandChanged,
                                onProductClick = onProductClick,
                            )
                        }
                    }
                }
            },
        )
    }
}
