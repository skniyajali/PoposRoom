/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.expenses.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_TAG
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Expense
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.NoteText

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
            .padding(SpaceSmall)
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
                        .padding(SpaceSmall),
                    text = item.expenseNote,
                    icon = PoposIcons.TurnedInNot,
                    color = MaterialTheme.colorScheme.onSecondary,
                )
            }
        }
    }
}