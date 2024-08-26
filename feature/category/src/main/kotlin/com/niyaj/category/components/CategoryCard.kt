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

package com.niyaj.category.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.CategoryConstants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Category
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun CategoryList(
    items: ImmutableList<Category>,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    selected: (Int) -> Boolean,
    modifier: Modifier = Modifier,
    padding: Dp = SpaceSmall,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    TrackScrollJank(scrollableState = lazyGridState, stateName = "Exported Category::List")

    LazyVerticalGrid(
        modifier = modifier
            .testTag(CategoryConstants.CATEGORY_LIST)
            .fillMaxSize(),
        contentPadding = PaddingValues(padding),
        horizontalArrangement = Arrangement.spacedBy(padding),
        verticalArrangement = Arrangement.spacedBy(padding),
        columns = GridCells.Fixed(2),
        state = lazyGridState,
    ) {
        items(
            items = items,
            key = { it.categoryId },
        ) { item: Category ->
            CategoryData(
                item = item,
                selected = selected,
                onClick = onClick,
                onLongClick = onLongClick,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryData(
    item: Category,
    selected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("CategoryData") {
    val borderStroke = if (selected(item.categoryId)) border else null

    ElevatedCard(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .testTag(CategoryConstants.CATEGORY_ITEM_TAG.plus(item.categoryId))
            .semantics {
                this.selected = selected(item.categoryId)
            }
            .then(
                borderStroke?.let {
                    Modifier.border(it, CardDefaults.elevatedShape)
                } ?: Modifier,
            )
            .combinedClickable(
                onClick = {
                    onClick(item.categoryId)
                },
                onLongClick = {
                    onLongClick(item.categoryId)
                },
            ),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = containerColor,
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
                selected = selected(item.categoryId),
                text = item.categoryName,
                showBorder = !item.isAvailable,
            )

            Text(
                text = item.categoryName,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun CategoryDataPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CategoryData(
            modifier = modifier,
            item = Category(
                categoryId = 2,
                categoryName = "Long category data for testing does text overflow it work",
                isAvailable = true,
            ),
            selected = { false },
            onClick = {},
            onLongClick = {},
        )
    }
}

@DevicePreviews
@Composable
private fun CategoryListEmptyDataPreview() {
    PoposRoomTheme {
        Surface {
            CategoryList(
                items = persistentListOf(),
                onClick = {},
                onLongClick = {},
                selected = { false },
            )
        }
    }
}

@DevicePreviews
@Composable
private fun CategoryListPreview(
    items: ImmutableList<Category> = CategoryPreviewData.categoryList.toImmutableList(),
) {
    PoposRoomTheme {
        Surface {
            CategoryList(
                items = items,
                onClick = {},
                onLongClick = {},
                selected = { false },
            )
        }
    }
}
