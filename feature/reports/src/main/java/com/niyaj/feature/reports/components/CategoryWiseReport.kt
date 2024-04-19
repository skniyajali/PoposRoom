package com.niyaj.feature.reports.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.theme.LightColor2
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.reports.CategoryWiseReportState
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable

@Composable
fun CategoryWiseReport(
    categoryState: CategoryWiseReportState,
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
        )
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
                    icon = Icons.Default.Category
                )
            },
            trailing = {
                OrderTypeDropdown(
                    text = categoryState.orderType.ifEmpty { "All" }
                ) {
                    onClickOrderType(it)
                }
            },
            expand = null,
            contentDesc = "Category wise report",
            content = {
                Crossfade(
                    targetState = categoryState,
                    label = "CategoryState"
                ) { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.categoryWiseReport.isNotEmpty() -> {
                            CategoryWiseReportCard(
                                report = state.categoryWiseReport,
                                selectedCategory = selectedCategory,
                                onExpandChanged = onCategoryExpandChanged,
                                onProductClick = onProductClick
                            )
                        }

                        else -> {
                            ItemNotAvailable(
                                text = state.hasError ?: "Category wise report not available",
                                showImage = false,
                            )
                        }
                    }
                }
            },
        )
    }
}