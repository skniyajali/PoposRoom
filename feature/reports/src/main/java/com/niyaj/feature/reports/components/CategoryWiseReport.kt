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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.components.PoposIconButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.CategoryWiseReport
import com.niyaj.model.ProductWiseReport
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CategoryWiseReportPreviewProvider
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun CategoryWiseReport(
    categoryState: UiState<List<CategoryWiseReport>>,
    orderType: String,
    reportExpanded: Boolean,
    selectedCategory: String,
    onCategoryExpandChanged: (String) -> Unit,
    onExpandChanged: () -> Unit,
    onClickOrderType: (String) -> Unit,
    onProductClick: (productId: Int) -> Unit,
    onPrintProductWiseReport: () -> Unit,
) = trace("CategoryWiseReport") {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(),
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
                Row {
                    OrderTypeDropdown(
                        text = orderType.ifEmpty { "All" },
                    ) {
                        onClickOrderType(it)
                    }

                    PoposIconButton(
                        icon = PoposIcons.Print,
                        onClick = onPrintProductWiseReport,
                    )
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
                        is UiState.Loading -> LoadingIndicatorHalf()

                        is UiState.Empty -> {
                            ItemNotAvailableHalf(
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

@Composable
private fun CategoryWiseReportCard(
    report: List<CategoryWiseReport>,
    selectedCategory: String,
    onExpandChanged: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
) = trace("CategoryWiseReportCard") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        report.forEachIndexed { index, (categoryName, products) ->
            val totalQty = products.sumOf { it.quantity }
            if (products.isNotEmpty()) {
                StandardExpandable(
                    modifier = Modifier
                        .fillMaxWidth(),
                    expanded = categoryName == selectedCategory,
                    onExpandChanged = {
                        onExpandChanged(categoryName)
                    },
                    title = {
                        IconWithText(
                            text = categoryName,
                            icon = PoposIcons.Category,
                            isTitle = true,
                        )
                    },
                    trailing = {
                        CountBox(count = totalQty.toString())
                    },
                    rowClickable = true,
                    expand = { modifier: Modifier ->
                        IconButton(
                            modifier = modifier,
                            onClick = {
                                onExpandChanged(categoryName)
                            },
                        ) {
                            Icon(
                                imageVector = PoposIcons.ArrowDown,
                                contentDescription = "Expand More",
                                tint = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    },
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(containerColor),
                        ) {
                            products.forEachIndexed { index, productWithQty ->
                                ProductReportCard(
                                    report = productWithQty,
                                    onProductClick = onProductClick,
                                )

                                if (index != products.size - 1) {
                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    },
                )

                if (index != report.size - 1) {
                    Spacer(modifier = Modifier.height(SpaceMini))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(SpaceMini))
                }
            }
        }
    }
}

@Composable
private fun ProductReportCard(
    report: ProductWiseReport,
    onProductClick: (Int) -> Unit,
) = trace("ProductReportCard") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onProductClick(report.productId)
            }
            .padding(horizontal = SpaceSmall, vertical = SpaceSmallMax),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = report.productName,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            text = report.quantity.toString(),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(0.5F),
        )
    }
}

@DevicePreviews
@Composable
private fun CategoryWiseReportPreview(
    @PreviewParameter(CategoryWiseReportPreviewProvider::class)
    categoryState: UiState<List<CategoryWiseReport>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CategoryWiseReport(
            categoryState = categoryState,
            orderType = "All",
            reportExpanded = true,
            selectedCategory = "Category 1",
            onCategoryExpandChanged = {},
            onExpandChanged = {},
            onClickOrderType = {},
            onProductClick = {},
            onPrintProductWiseReport = {},
        )
    }
}
