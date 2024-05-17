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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toSafeString
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.MintCream
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.ui.components.IncDecBox
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun MarketItemWithQuantityCard(
    item: MarketItemAndQuantity,
    onAddItem: (itemId: Int) -> Unit,
    onRemoveItem: (itemId: Int) -> Unit,
    onDecreaseQuantity: (itemId: Int) -> Unit,
    onIncreaseQuantity: (itemId: Int) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) = trace("MarketItemWithQuantityCard") {

    val itemState by derivedStateOf {
        item.itemQuantity?.let { quantity -> quantity >= 0.0 } ?: false
    }

    val color = animateColorAsState(
        targetValue = if (itemState) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        label = "Color State",
    )

    SwipeableActionsBox(
        startActions = listOf(
            addSwipeAction {
                if (!itemState) onAddItem(item.itemId)
            },
        ),
        endActions = listOf(
            removeSwipeAction {
                if (itemState) onRemoveItem(item.itemId)
            },
        ),
        backgroundUntilSwipeThreshold = MaterialTheme.colorScheme.background,
    ) {
        ListItem(
            leadingContent = {
                Checkbox(
                    checked = itemState,
                    onCheckedChange = {
                        if (!itemState) {
                            onAddItem(item.itemId)
                        } else {
                            onRemoveItem(item.itemId)
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        disabledIndeterminateColor = MaterialTheme.colorScheme.secondary,
                    ),
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
                    quantity = item.itemQuantity?.toSafeString() ?: "0",
                    measureUnit = item.unitName,
                    enableDecreasing = (item.itemQuantity != 0.0) && itemState,
                    enableIncreasing = itemState,
                    onDecrease = {
                        onDecreaseQuantity(item.itemId)
                    },
                    onIncrease = {
                        onIncreaseQuantity(item.itemId)
                    },
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MintCream,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !itemState,
                    onClick = {
                        onAddItem(item.itemId)
                    },
                ),
        )
    }
}

@Composable
fun MarketItemWithQuantityCard(
    item: MarketItemAndQuantity,
    onAddItem: (listTypeId: Int, itemId: Int) -> Unit,
    onRemoveItem: (listTypeId: Int, itemId: Int) -> Unit,
    onDecreaseQuantity: (listTypeId: Int, itemId: Int) -> Unit,
    onIncreaseQuantity: (listTypeId: Int, itemId: Int) -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) = trace("MarketItemWithQuantityCard") {

    val itemState by derivedStateOf {
        item.itemQuantity?.let { quantity -> quantity >= 0.0 } ?: false
    }

    val color = animateColorAsState(
        targetValue = if (itemState) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        label = "Color State",
    )

    SwipeableActionsBox(
        startActions = listOf(
            addSwipeAction {
                if (!itemState) onAddItem(item.listWithTypeId, item.itemId)
            },
        ),
        endActions = listOf(
            removeSwipeAction {
                if (itemState) onRemoveItem(item.listWithTypeId, item.itemId)
            },
        ),
        backgroundUntilSwipeThreshold = MaterialTheme.colorScheme.background,
    ) {
        ListItem(
            leadingContent = {
                Checkbox(
                    checked = itemState,
                    onCheckedChange = {
                        if (!itemState) {
                            onAddItem(item.listWithTypeId, item.itemId)
                        } else {
                            onRemoveItem(item.listWithTypeId, item.itemId)
                        }
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        disabledIndeterminateColor = MaterialTheme.colorScheme.secondary,
                    ),
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
                    quantity = item.itemQuantity?.toSafeString() ?: "0",
                    measureUnit = item.unitName,
                    enableDecreasing = (item.itemQuantity != 0.0) && itemState,
                    enableIncreasing = itemState,
                    onDecrease = {
                        onDecreaseQuantity(item.listWithTypeId, item.itemId)
                    },
                    onIncrease = {
                        onIncreaseQuantity(item.listWithTypeId, item.itemId)
                    },
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MintCream,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !itemState,
                    onClick = {
                        onAddItem(item.listWithTypeId, item.itemId)
                    },
                ),
        )
    }
}


@Composable
fun addSwipeAction(
    onSwipe: () -> Unit,
): SwipeAction {
    return SwipeAction(
        icon = rememberVectorPainter(PoposIcons.Add),
        background = MaterialTheme.colorScheme.primaryContainer,
        isUndo = false,
        onSwipe = onSwipe,
    )
}

@Composable
fun removeSwipeAction(
    onSwipe: () -> Unit,
): SwipeAction {
    return SwipeAction(
        icon = rememberVectorPainter(PoposIcons.Delete),
        background = MaterialTheme.colorScheme.secondaryContainer,
        isUndo = false,
        onSwipe = onSwipe,
    )
}