package com.niyaj.daily_market.market_list.add_edit

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material.icons.twotone.RestartAlt
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.MarketListTestTags
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_LIST
import com.niyaj.common.tags.MarketListTestTags.UPDATE_LIST
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.MarketItem
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination
fun AddEditMarketListScreen(
    marketId: Int = 0,
    navController: NavController,
    viewModel: AddEditMarketListViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val lazyListState = rememberLazyListState()

    val items = viewModel.items.collectAsStateWithLifecycle().value
    val groupedByType = items.groupBy { it.itemType }

    val selectedItems = viewModel.selectedItems.toList()
    val removedItems = viewModel.removedItems.toList()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val itemQuantity = viewModel.itemWithQuantity.toList()

    val title = if (marketId == 0) CREATE_NEW_LIST else UPDATE_LIST

    StandardScaffoldNew(
        navController = navController,
        title = if (selectedItems.isEmpty()) title else "${selectedItems.size} Selected",
        showBackButton = true,
        showBottomBar = lazyListState.isScrollingUp(),
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(MarketListTestTags.ADD_EDIT_MARKET_LIST_BUTTON)
                    .padding(SpaceMedium),
                text = title,
                icon = if (marketId == 0) Icons.Default.Add else Icons.Default.Edit,
                enabled = true,
                onClick = {

                }
            )
        },
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = "Search for items...",
                    onClearClick = viewModel::clearSearchText,
                    onSearchTextChanged = viewModel::searchTextChanged
                )
            } else {
                if (items.isNotEmpty()) {
                    IconButton(
                        onClick = viewModel::selectAllItems
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = Constants.SELECTALL_ICON
                        )
                    }

                    IconButton(
                        onClick = viewModel::openSearchBar,
                        modifier = Modifier.testTag(NAV_SEARCH_BTN)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall)
                .consumeWindowInsets(paddingValues)
                .imePadding(),
            state = lazyListState
        ) {
            items(
                items = items,
                key = {
                    it.itemId
                }
            ) { item ->
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = "0",
                    onValueChange = {

                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@Composable
fun MarketItemCard(
    item: MarketItem,
    itemQuantity: (itemId: Int) -> String,
    doesSelected: (itemId: Int) -> ToggleableState,
    onSelectItem: (itemId: Int) -> Unit,
    onDismiss: (itemId: Int) -> Unit,
    decreaseQuantity: (itemId: Int) -> Unit,
    increaseQuantity: (itemId: Int) -> Unit,
) {
    val archive = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.RestartAlt),
        background = MaterialTheme.colorScheme.primaryContainer,
        isUndo = true,
        onSwipe = {
            onDismiss(item.itemId)
        }
    )

    val snooze = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.Delete),
        background = MaterialTheme.colorScheme.secondaryContainer,
        isUndo = true,
        onSwipe = {
            onDismiss(item.itemId)
        },
    )

    SwipeableActionsBox(
        startActions = listOf(archive),
        endActions = listOf(snooze),
        backgroundUntilSwipeThreshold = MaterialTheme.colorScheme.background
    ) {
        ListItem(
            leadingContent = {
                TriStateCheckbox(
                    state = doesSelected(item.itemId),
                    onClick = {
                        onSelectItem(item.itemId)
                    }
                )
            },
            headlineContent = {
                Text(
                    text = item.itemName,
                    fontWeight = FontWeight.SemiBold
                )
            },
            supportingContent = {
                item.itemPrice?.let {
                    Text(text = it.toRupee)
                }
            },
            trailingContent = {
                MarketItemIncDecCard(
                    itemQuantity = itemQuantity(item.itemId),
                    decreaseQuantity = {
                        decreaseQuantity(item.itemId)
                    },
                    increaseQuantity = {
                        increaseQuantity(item.itemId)
                    },
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
    }

}


@Composable
fun MarketItemCard(
    item: MarketItem,
    itemQuantity: (itemId: Int) -> String,
    doesSelected: (itemId: Int) -> ToggleableState,
    onSelectItem: (itemId: Int) -> Unit,
    onDismiss: (itemId: Int) -> Unit,
    onValueChanged: (itemId: Int, quantity: String) -> Unit,
) {
    val archive = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.RestartAlt),
        background = MaterialTheme.colorScheme.primaryContainer,
        isUndo = true,
        onSwipe = {
            onDismiss(item.itemId)
        }
    )

    val snooze = SwipeAction(
        icon = rememberVectorPainter(Icons.TwoTone.Delete),
        background = MaterialTheme.colorScheme.secondaryContainer,
        isUndo = true,
        onSwipe = {
            onDismiss(item.itemId)
        },
    )

    key(item.itemId) {
        SwipeableActionsBox(
            startActions = listOf(archive),
            endActions = listOf(snooze),
            backgroundUntilSwipeThreshold = MaterialTheme.colorScheme.background
        ) {
            ListItem(
                leadingContent = {
                    TriStateCheckbox(
                        state = doesSelected(item.itemId),
                        onClick = {
                            onSelectItem(item.itemId)
                        }
                    )
                },
                headlineContent = {
                    Text(
                        text = item.itemName,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                supportingContent = {
                    item.itemPrice?.let {
                        Text(text = it.toRupee)
                    }
                },
                trailingContent = {
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    }
}


@Composable
fun MarketItemIncDecCard(
    itemQuantity: String,
    decreaseQuantity: () -> Unit,
    increaseQuantity: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .height(40.dp),
        shape = RoundedCornerShape(SpaceMini),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = decreaseQuantity,
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease quantity",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Crossfade(
                targetState = itemQuantity,
                label = "Item quantity"
            ) {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            IconButton(
                onClick = increaseQuantity,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase quantity",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}



@Composable
private fun DuckieTextField(
    text: String,
    onTextChanged: (String) -> Unit,
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChanged,
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                text.forEachIndexed { index, char ->
                    DuckieTextFieldCharContainer(
                        text = char,
                        isFocused = index == text.lastIndex,
                    )
                }
                repeat(3 - text.length) {
                    DuckieTextFieldCharContainer(
                        text = ' ',
                        isFocused = false,
                    )
                }
            }
        },
    )
}

@Composable
private fun DuckieTextFieldCharContainer(
    modifier: Modifier = Modifier,
    text: Char,
    isFocused: Boolean,
) {
    val shape = remember { RoundedCornerShape(4.dp) }

    Box(
        modifier = modifier
            .size(
                width = 29.dp,
                height = 40.dp,
            )
            .background(
                color = Color(0xFFF6F6F6),
                shape = shape,
            )
            .run {
                if (isFocused) {
                    border(
                        width = 1.dp,
                        color = Color(0xFFFF8300),
                        shape = shape,
                    )
                } else {
                    this
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text.toString())
    }
}


//            groupedByType.forEach { (type, marketItems) ->
//                stickyHeader {
//                    NoteCard(
//                        text = type,
//                        icon = Icons.Default.Category,
//                        backgroundColor = MaterialTheme.colorScheme.surface,
//                        textStyle = MaterialTheme.typography.labelLarge,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                }
//
//                items(
//                    items = marketItems,
//                    key = {
//                        it.itemId
//                    }
//                ) { item ->
////                    MarketItemCard(
////                        item = item,
////                        itemQuantity = { itemId ->
////                            itemQuantity.find {
////                                it.itemId == itemId
////                            }?.quantity ?: "0"
////                        },
////                        doesSelected = {
////                            if (selectedItems.contains(it)) {
////                                ToggleableState.On
////                            } else if (removedItems.contains(it)) {
////                                ToggleableState.Indeterminate
////                            } else {
////                                ToggleableState.Off
////                            }
////                        },
////                        onSelectItem = viewModel::selectItem,
////                        onDismiss = viewModel::removeItem,
////                        onValueChanged = viewModel::onValueChanged
////                    )
//
//                    TextField(
//                        value = itemQuantity.find {
//                            it.itemId == item.itemId
//                        }?.quantity ?: "0",
//                        onValueChange = {
//                            viewModel.onValueChanged(item.itemId, it)
//                        },
//                    )
//
//                    Spacer(modifier = Modifier.height(SpaceMini))
//                }
//            }