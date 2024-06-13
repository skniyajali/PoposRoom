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

package com.niyaj.expenses.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_TAG
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.IconSizeSmall
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Expense
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.parameterProvider.ExpensePreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank

@Composable
fun ExpensesList(
    modifier: Modifier = Modifier,
    items: List<Expense>,
    doesSelected: (Int) -> Boolean,
    isInSelectionMode: Boolean,
    onSelectItem: (Int) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScrollJank(scrollableState = lazyListState, stateName = "Expenses::List")

    val groupByName = remember(items) { items.groupBy { it.expenseName } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        state = lazyListState,
    ) {
        groupByName.forEach { (_, expenses) ->
            if (expenses.size > 1) {
                item {
                    GroupedExpensesData(
                        items = expenses,
                        doesSelected = doesSelected,
                        onClick = {
                            if (isInSelectionMode) {
                                onSelectItem(it)
                            }
                        },
                        onLongClick = onSelectItem,
                    )
                }
            } else {
                item {
                    ExpensesData(
                        item = expenses.first(),
                        doesSelected = doesSelected,
                        onClick = {
                            if (isInSelectionMode) {
                                onSelectItem(it)
                            }
                        },
                        onLongClick = onSelectItem,
                    )
                }
            }
        }
    }
}

@Composable
fun ExpensesList(
    modifier: Modifier = Modifier,
    items: List<Expense>,
    doesSelected: (Int) -> Boolean,
    onSelectItem: (Int) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScrollJank(scrollableState = lazyListState, stateName = "Expenses::List")

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        state = lazyListState,
    ) {
        items(
            items = items,
            key = { it.expenseId },
        ) {
            ExpensesData(
                item = it,
                doesSelected = doesSelected,
                onClick = onSelectItem,
                onLongClick = onSelectItem,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesData(
    modifier: Modifier = Modifier,
    item: Expense,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) = trace("ExpensesData") {
    val borderStroke = if (doesSelected(item.expenseId)) border else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                borderStroke?.let {
                    Modifier.border(it, RoundedCornerShape(SpaceMini))
                } ?: Modifier,
            )
            .clip(RoundedCornerShape(SpaceMini))
            .combinedClickable(
                onClick = {
                    onClick(item.expenseId)
                },
                onLongClick = {
                    onLongClick(item.expenseId)
                },
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            ListItem(
                modifier = Modifier
                    .testTag(EXPENSE_TAG.plus(item.expenseId))
                    .fillMaxWidth(),
                headlineContent = {
                    Text(
                        text = item.expenseName,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                supportingContent = {
                    Text(text = item.expenseAmount.toRupee)
                },
                leadingContent = {
                    CircularBox(
                        icon = PoposIcons.Person,
                        doesSelected = doesSelected(item.expenseId),
                        text = item.expenseName,
                    )
                },
                trailingContent = {
                    NoteText(
                        text = item.expenseDate.toPrettyDate(),
                        icon = PoposIcons.CalenderMonth,
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )

            if (item.expenseNote.isNotEmpty()) {
                NoteText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SpaceSmall, horizontal = SpaceMedium),
                    text = item.expenseNote,
                    icon = PoposIcons.TurnedInNot,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun GroupedExpensesData(
    modifier: Modifier = Modifier,
    items: List<Expense>,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) = trace("GroupedExpensesData") {
    val item = items.first()
    val totalAmount = items.sumOf { it.expenseAmount.toInt() }.toString()
    val notes = items.map { it.expenseNote }.filter { it.isNotEmpty() }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            ListItem(
                modifier = modifier
                    .fillMaxWidth(),
                headlineContent = {
                    Text(
                        text = item.expenseName,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                supportingContent = {
                    Text(text = totalAmount.toRupee)
                },
                leadingContent = {
                    CircularBox(
                        icon = PoposIcons.Person,
                        doesSelected = false,
                        text = item.expenseName,
                    )
                },
                trailingContent = {
                    NoteText(
                        text = item.expenseDate.toPrettyDate(),
                        icon = PoposIcons.CalenderMonth,
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )

            Spacer(modifier = Modifier.height(SpaceMini))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = SpaceSmall, horizontal = SpaceMedium),
                horizontalArrangement = Arrangement.spacedBy(SpaceMini),
                verticalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                items.forEach { expense ->
                    val borderStroke = if (doesSelected(expense.expenseId)) border else null

                    ElevatedCard(
                        modifier = modifier
                            .testTag(EXPENSE_TAG.plus(expense.expenseId))
                            .then(
                                borderStroke?.let {
                                    Modifier.border(it, RoundedCornerShape(SpaceMini))
                                } ?: Modifier,
                            )
                            .clip(RoundedCornerShape(SpaceMini))
                            .combinedClickable(
                                onClick = {
                                    onClick(expense.expenseId)
                                },
                                onLongClick = {
                                    onLongClick(expense.expenseId)
                                },
                            ),
                        shape = RoundedCornerShape(SpaceMini),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 2.dp,
                        ),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Icon(
                                imageVector = if (doesSelected(expense.expenseId)) {
                                    PoposIcons.Check
                                } else {
                                    PoposIcons.Rupee
                                },
                                contentDescription = null,
                                modifier = Modifier.size(IconSizeSmall),
                            )

                            Spacer(modifier = Modifier.width(SpaceMini))

                            Text(
                                text = expense.expenseAmount,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(SpaceSmall))
                }
            }

            Spacer(modifier = Modifier.height(SpaceMini))

            if (notes.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(SpaceMini))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SpaceSmall, horizontal = SpaceMedium),
                ) {
                    notes.forEach { note ->
                        NoteText(
                            text = note,
                            icon = PoposIcons.StickyNote2,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(SpaceMini))
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ExpensesListPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ExpensesList(
            modifier = modifier,
            items = ExpensePreviewData.expenses,
            doesSelected = { it % 2 == 0 },
            isInSelectionMode = false,
            onSelectItem = {},
        )
    }
}
