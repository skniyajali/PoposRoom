/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.Category
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.TrackScrollJank
import kotlinx.collections.immutable.ImmutableList


const val CATEGORY_ITEM_TAG = "Category-"


@Composable
fun CategoriesData(
    modifier: Modifier = Modifier,
    lazyRowState: LazyListState,
    uiState: UiState<ImmutableList<Category>>,
    selectedCategory: Int,
    onSelect: (Int) -> Unit,
) = trace("CategoriesData") {
    Crossfade(
        targetState = uiState,
        label = "CategoryData::State",
    ) { state ->
        when (state) {
            is UiState.Success -> {
                TrackScrollJank(scrollableState = lazyRowState, stateName = "category:list")

                LazyRow(
                    modifier = modifier.fillMaxWidth(),
                    state = lazyRowState,
                ) {
                    items(
                        items = state.data,
                        key = {
                            it.categoryId
                        },
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

            else -> Unit
        }
    }

}

@Composable
fun CategoriesData(
    modifier: Modifier = Modifier,
    lazyRowState: LazyListState,
    categories: ImmutableList<Category>,
    doesSelected: (Int) -> Boolean,
    onSelect: (Int) -> Unit,
) = trace("CategoriesData") {
    TrackScrollJank(scrollableState = lazyRowState, stateName = "category:list")

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        state = lazyRowState,
    ) {
        items(
            items = categories,
            key = {
                it.categoryId
            },
        ) { category ->
            CategoryData(
                item = category,
                doesSelected = doesSelected,
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
    unselectedColor: Color = MaterialTheme.colorScheme.surface,
) = trace("CategoryData") {
    val color = if (doesSelected(item.categoryId)) selectedColor else unselectedColor

    ElevatedCard(
        modifier = modifier
            .testTag(CATEGORY_ITEM_TAG.plus(item.categoryId))
            .padding(SpaceSmall),
        onClick = {
            onClick(item.categoryId)
        },
        colors = CardDefaults.elevatedCardColors(
            containerColor = color,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularBox(
                icon = PoposIcons.Category,
                doesSelected = doesSelected(item.categoryId),
                size = 25.dp,
                text = item.categoryName,
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