package com.niyaj.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.Category
import com.niyaj.ui.utils.TrackScrollJank
import kotlinx.collections.immutable.ImmutableList


const val CATEGORY_ITEM_TAG = "Category-"


@Composable
fun CategoriesData(
    lazyRowState: LazyListState,
    categories: ImmutableList<Category>,
    selectedCategory: Int,
    onSelect: (Int) -> Unit,
) {
    TrackScrollJank(scrollableState = lazyRowState, stateName = "category:list")

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = lazyRowState,
    ) {
        items(
            items = categories,
            key = {
                it.categoryId
            }
        ) { category ->
            CategoryData(
                item = category,
                doesSelected = {
                    selectedCategory == it
                },
                onClick = onSelect,
            )
        }
    }
}

@Composable
fun CategoriesData(
    lazyRowState: LazyListState,
    categories: ImmutableList<Category>,
    selectedCategory: List<Int>,
    onSelect: (Int) -> Unit,
) {
    TrackScrollJank(scrollableState = lazyRowState, stateName = "category:list")

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        state = lazyRowState,
    ) {
        items(
            items = categories,
            key = {
                it.categoryId
            }
        ) { category ->
            CategoryData(
                item = category,
                doesSelected = {
                    selectedCategory.contains(it)
                },
                onClick = onSelect,
            )
        }
    }
}

@Composable
fun CategoryData(
    modifier: Modifier = Modifier,
    item: Category,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    selectedColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    unselectedColor: Color = MaterialTheme.colorScheme.surface
) {
    val color = if (doesSelected(item.categoryId)) selectedColor else unselectedColor

    ElevatedCard(
        modifier = modifier
            .testTag(CATEGORY_ITEM_TAG.plus(item.categoryId))
            .padding(SpaceSmall),
        onClick = {
            onClick(item.categoryId)
        },
        colors = CardDefaults.elevatedCardColors(
            containerColor = color
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularBox(
                icon = Icons.Default.Category,
                doesSelected = doesSelected(item.categoryId),
                size = 25.dp,
                text = item.categoryName
            )

            Spacer(modifier = Modifier.width(SpaceSmallMax))

            Text(
                text = item.categoryName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}