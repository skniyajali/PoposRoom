package com.niyaj.daily_market.market_list.add_edit

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.twotone.Add
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.MarketListTestTags
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_ITEM
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_LIST
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NOT_AVAILABLE
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.MarketListTestTags.UPDATE_LIST
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toSafeString
import com.niyaj.daily_market.destinations.AddEditMarketItemScreenDestination
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.ItemQuantityAndType
import com.niyaj.model.MarketItem
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketList
import com.niyaj.ui.components.AnimatedTextDivider
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.IncDecBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.ItemNotFound
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedAssistChip
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.components.TwoGridTexts
import com.niyaj.ui.components.drawRainbowBorder
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.CaptureController
import com.niyaj.ui.utils.ScrollableCapturable
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination
fun AddEditMarketListScreen(
    marketId: Int = 0,
    navController: NavController,
    viewModel: AddEditMarketListViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyListState()
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val captureController = rememberCaptureController()

    val marketItems = viewModel.marketItems.collectAsStateWithLifecycle().value
    val marketList = viewModel.marketList.collectAsStateWithLifecycle().value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val title = if (marketId == 0) CREATE_NEW_LIST else UPDATE_LIST

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value
    val selectedDate = viewModel.selectedDate.collectAsStateWithLifecycle().value

    val dialogState = rememberMaterialDialogState()
    val showList = viewModel.showList.collectAsStateWithLifecycle().value
    val marketLists = viewModel.listItems.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.successMessage)
                    }
                }
            }
        }
    }

    StandardScaffoldNew(
        navController = navController,
        title = title,
        showBackButton = true,
        showFab = lazyListState.isScrollingUp(),
        snackbarHostState = snackbarState,
        fabPosition = FabPosition.EndOverlay,
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onShowList,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share List")
            }
        },
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = MARKET_ITEM_SEARCH_PLACEHOLDER,
                    onClearClick = viewModel::clearSearchText,
                    onSearchTextChanged = viewModel::searchTextChanged
                )
            } else {
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
        },
    ) {
        Crossfade(
            targetState = marketItems,
            label = "Add Edit Market List State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = MARKET_ITEM_NOT_AVAILABLE,
                        buttonText = CREATE_NEW_ITEM,
                        onClick = {
                            navController.navigate(AddEditMarketItemScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    val groupedByType = state.data.groupBy { it.item.itemType }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall),
                        state = lazyListState,
                    ) {
                        item {
                            marketList?.let {
                                ItemHeader(
                                    marketList = it,
                                    selectedDate = selectedDate.ifEmpty { it.marketDate.toString() },
                                    onClickDate = {
                                        dialogState.show()
                                    },
                                    onClickSaveChanges = {}
                                )
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }

                        groupedByType.forEach { (type, marketItems) ->
                            stickyHeader {
                                InfoText(
                                    text = type,
                                    icon = Icons.Default.Category,
                                    backgroundColor = MaterialTheme.colorScheme.surface,
                                    textStyle = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(SpaceMini))
                            }

                            items(
                                items = marketItems,
                                key = {
                                    it.item.itemId
                                }
                            ) { itemWithQuantity ->
                                val quantity =
                                    itemWithQuantity.quantity.collectAsStateWithLifecycle().value

                                val doesExist =
                                    itemWithQuantity.doesExist.collectAsStateWithLifecycle().value

                                MarketItemCard(
                                    item = itemWithQuantity.item,
                                    itemQuantity = quantity,
                                    itemState = {
                                        if (doesExist) {
                                            ToggleableState.On
                                        } else if (marketList?.whitelistItems?.contains(it) == true) {
                                            ToggleableState.Indeterminate
                                        } else {
                                            ToggleableState.Off
                                        }
                                    },
                                    onAddItem = viewModel::onAddItem,
                                    onRemoveItem = viewModel::onRemoveItem,
                                    onDecreaseQuantity = viewModel::onDecreaseQuantity,
                                    onIncreaseQuantity = viewModel::onIncreaseQuantity
                                )

                                Spacer(modifier = Modifier.height(SpaceMini))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            ItemNotFound(
                                onBtnClick = {
                                    navController.navigate(AddEditMarketItemScreenDestination())
                                }
                            )
                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker(
            allowedDateValidator = { date ->
                date <= LocalDate.now()
            }
        ) { date ->
            viewModel.selectDate(date.toMilliSecond)
        }
    }

    AnimatedVisibility(
        visible = showList && marketLists.isNotEmpty()
    ) {
        ShareableMarketList(
            captureController = captureController,
            marketDate = marketList?.marketDate ?: System.currentTimeMillis(),
            onDismiss = viewModel::onDismissList,
            marketLists = marketLists,
            onClickShare = {
                captureController.captureLongScreenshot()
            },
            onCaptured = { bitmap, error ->
                bitmap?.let {
                    scope.launch {
                        val uri = viewModel.saveImage(it, context)
                        uri?.let {
                            viewModel.shareContent(context, "Share Image", uri)
                        }
                    }
                }
                error?.let {
                    Log.d("Capturable", "Error: ${it.message}\n${it.stackTrace.joinToString()}")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareableMarketList(
    captureController: CaptureController,
    marketDate: Long,
    marketLists: List<MarketItemAndQuantity>,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onCaptured: (Bitmap?, Throwable?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
        ),
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                ScrollableCapturable(
                    controller = captureController,
                    onCaptured = onCaptured,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2.5f)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMedium),
                            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            CircularBox(
                                icon = Icons.Default.ShoppingBag,
                                doesSelected = false,
                                size = 80.dp,
                            )

                            Text(
                                text = "Market Date".uppercase(),
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = marketDate.toPrettyDate(),
                                style = MaterialTheme.typography.titleLarge
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = SpaceLarge)
                                    .drawRainbowBorder(3.dp, durationMillis = 5000),
                                thickness = SpaceMini
                            )
                            val groupedByType = marketLists.groupBy { it.item.itemType }

                            groupedByType.forEach { (itemType, list) ->
                                AnimatedTextDivider(
                                    text = itemType,
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                )

                                list.fastForEachIndexed { i, it ->
                                    TwoGridTexts(
                                        textOne = it.item.itemName,
                                        textTwo = it.quantityAndType.itemQuantity.toSafeString() +
                                                " " + it.item.itemMeasureUnit?.unitName,
                                        textStyle = MaterialTheme.typography.bodyLarge,
                                        isTitle = true
                                    )
                                    if (i != list.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(SpaceSmall),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        modifier = Modifier
                            .heightIn(ButtonSize)
                            .weight(1.4f),
                        onClick = onDismiss,
                        shape = RoundedCornerShape(SpaceMini),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                        Spacer(modifier = Modifier.width(SpaceMini))
                        Text(text = "Close")
                    }

                    Spacer(modifier = Modifier.width(SpaceMedium))

                    Button(
                        onClick = onClickShare,
                        modifier = Modifier
                            .heightIn(ButtonSize)
                            .weight(1.4f),
                        shape = RoundedCornerShape(SpaceMini),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Data"
                        )
                        Spacer(modifier = Modifier.width(SpaceMini))
                        Text(text = "Share")
                    }
                }
            }
        }
    }
}


@Composable
fun ItemHeader(
    marketList: MarketList,
    selectedDate: String,
    onClickDate: () -> Unit,
    onClickSaveChanges: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmallMax)
                .padding(vertical = SpaceSmall),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(SpaceLarge)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceSmall)
                ) {
                    CircularBox(
                        icon = Icons.Default.CalendarMonth,
                        doesSelected = false,
                    )

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(SpaceMini)
                    ) {
                        Text(
                            text = "Market Date".uppercase(),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = marketList.marketDate.toPrettyDate(),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                StandardOutlinedAssistChip(
                    text = if (selectedDate.toPrettyDate() == marketList.marketDate.toPrettyDate()) "Change" else selectedDate.toPrettyDate(),
                    icon = Icons.Default.CalendarMonth,
                    onClick = onClickDate,
                    trailingIcon = Icons.Default.ArrowDropDown
                )
            }

            AnimatedVisibility(
                visible = selectedDate.toPrettyDate() != marketList.marketDate.toPrettyDate()
            ) {
                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(MarketListTestTags.ADD_EDIT_MARKET_LIST_BUTTON),
                    text = "Save Changes",
                    icon = Icons.Default.EditCalendar,
                    enabled = true,
                    onClick = onClickSaveChanges
                )
            }
        }
    }
}


@Composable
fun MarketItemCard(
    item: MarketItem,
    itemQuantity: ItemQuantityAndType,
    itemState: (itemId: Int) -> ToggleableState,
    onAddItem: (itemId: Int) -> Unit,
    onRemoveItem: (itemId: Int) -> Unit,
    onDecreaseQuantity: (itemId: Int) -> Unit,
    onIncreaseQuantity: (itemId: Int) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val toggleState by rememberUpdatedState(newValue = itemState(item.itemId))
    val quantity by rememberUpdatedState(newValue = itemQuantity.itemQuantity)

    val addItem = key(item.itemId) {
        SwipeAction(
            icon = rememberVectorPainter(Icons.TwoTone.Add),
            background = MaterialTheme.colorScheme.primaryContainer,
            isUndo = true,
            onSwipe = {
                onAddItem(item.itemId)
            }
        )
    }

    val removeItem = key(item.itemId) {
        SwipeAction(
            icon = rememberVectorPainter(Icons.TwoTone.Delete),
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