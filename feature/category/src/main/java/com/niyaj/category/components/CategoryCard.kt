package com.niyaj.category.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.CategoryConstants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Category
import com.niyaj.ui.components.CircularBox

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryData(
    modifier: Modifier = Modifier,
    item: Category,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) = trace("CategoryData") {
    val borderStroke = if (doesSelected(item.categoryId)) border else null

    ElevatedCard(
        modifier = modifier
            .testTag(CategoryConstants.CATEGORY_ITEM_TAG.plus(item.categoryId))
            .padding(SpaceSmall)
            .then(
                borderStroke?.let {
                    Modifier.border(it, CardDefaults.elevatedShape)
                } ?: Modifier,
            )
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    onClick(item.categoryId)
                },
                onLongClick = {
                    onLongClick(item.categoryId)
                },
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularBox(
                icon = PoposIcons.Category,
                doesSelected = doesSelected(item.categoryId),
                showBorder = !item.isAvailable,
                text = item.categoryName,
            )

            Text(
                text = item.categoryName,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}