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

package com.niyaj.market.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toSafeString
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.model.ItemQuantityAndType
import com.niyaj.model.MarketItem
import com.niyaj.ui.components.IncDecBox
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun MarketItemWithQuantityCard(
    item: MarketItem,
    itemQuantity: ItemQuantityAndType,
    itemState: (itemId: Int) -> ToggleableState,
    onAddItem: (itemId: Int) -> Unit,
    onRemoveItem: (itemId: Int) -> Unit,
    onDecreaseQuantity: (itemId: Int) -> Unit,
    onIncreaseQuantity: (itemId: Int) -> Unit,
) = trace("MarketItemWithQuantityCard") {
    val interactionSource = remember { MutableInteractionSource() }
    val toggleState by rememberUpdatedState(newValue = itemState(item.itemId))
    val quantity by rememberUpdatedState(newValue = itemQuantity.itemQuantity)

    val addItem = key(item.itemId) {
        SwipeAction(
            icon = rememberVectorPainter(PoposIcons.Add),
            background = MaterialTheme.colorScheme.primaryContainer,
            isUndo = true,
            onSwipe = {
                onAddItem(item.itemId)
            }
        )
    }

    val removeItem = key(item.itemId) {
        SwipeAction(
            icon = rememberVectorPainter(PoposIcons.Delete),
            background = MaterialTheme.colorScheme.secondaryContainer,
            isUndo = true,
            onSwipe = {
                onRemoveItem(item.itemId)
            },
        )
    }

    val color = animateColorAsState(
        targetValue = when (toggleState) {
            ToggleableState.Off -> MaterialTheme.colorScheme.outline
            ToggleableState.Indeterminate -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onBackground
        },
        label = "Animate Text Color"
    )

    SwipeableActionsBox(
        startActions = listOf(addItem),
        endActions = listOf(removeItem),
        backgroundUntilSwipeThreshold = MaterialTheme.colorScheme.background
    ) {
        ListItem(
            leadingContent = {
                TriStateCheckbox(
                    state = toggleState,
                    onClick = {
                        onAddItem(item.itemId)
                    },
                    enabled = toggleState != ToggleableState.Indeterminate,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        disabledIndeterminateColor = MaterialTheme.colorScheme.secondary,
                    )
                )
            },
            headlineContent = {
                Text(
                    text = item.itemName,
                    fontWeight = FontWeight.SemiBold,
                    color = color.value,
                )
            },
            supportingContent = {
                item.itemPrice?.let {
                    Text(text = it.toRupee)
                }
            },
            trailingContent = {
                IncDecBox(
                    quantity = quantity.toSafeString(),
                    measureUnit = item.itemMeasureUnit?.unitName ?: "",
                    enableDecreasing = quantity != 0.0 && toggleState == ToggleableState.On,
                    enableIncreasing = toggleState == ToggleableState.On,
                    onDecrease = {
                        onDecreaseQuantity(item.itemId)
                    },
                    onIncrease = {
                        onIncreaseQuantity(item.itemId)
                    },
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = itemState(item.itemId) != ToggleableState.On,
                    onClick = {
                        onAddItem(item.itemId)
                    }
                )
        )
    }
}