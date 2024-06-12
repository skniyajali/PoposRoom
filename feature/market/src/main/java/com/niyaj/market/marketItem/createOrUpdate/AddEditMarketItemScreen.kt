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

package com.niyaj.market.marketItem.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketListTestTags.ADD_EDIT_MARKET_ITEM_BUTTON
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_ITEM
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_MEASURE_ERROR_TAG
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_MEASURE_FIELD
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_ERROR_TAG
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_NAME_FIELD
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_PRICE_ERROR_TAG
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_PRICE_FIELD
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_TYPE_ERROR_TAG
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_TYPE_FIELD
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_ITEM_DESC
import com.niyaj.common.tags.MarketListTestTags.UPDATE_ITEM
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.market.destinations.AddEditMarketTypeScreenDestination
import com.niyaj.market.destinations.AddEditMeasureUnitScreenDestination
import com.niyaj.model.MarketTypeIdAndName
import com.niyaj.model.MeasureUnit
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun AddEditMarketItemScreen(
    itemId: Int = 0,
    navigator: DestinationsNavigator,
    viewModel: AddEditMarketItemViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val typeError by viewModel.typeError.collectAsStateWithLifecycle()
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val amountError by viewModel.priceError.collectAsStateWithLifecycle()
    val unitError by viewModel.unitError.collectAsStateWithLifecycle()
    val typeNames by viewModel.itemTypes.collectAsStateWithLifecycle()
    val measureUnits by viewModel.measureUnits.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    val title = if (itemId == 0) CREATE_NEW_ITEM else UPDATE_ITEM
    val icon = if (itemId == 0) PoposIcons.Add else PoposIcons.Edit

    AddEditMarketItemScreenContent(
        modifier = Modifier,
        title = title,
        icon = icon,
        state = viewModel.state,
        typeNames = typeNames,
        measureUnits = measureUnits,
        typeError = typeError,
        nameError = nameError,
        amountError = amountError,
        unitError = unitError,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
        onClickAddMarketType = {
            navigator.navigate(AddEditMarketTypeScreenDestination(typeName = it))
        },
        onClickAddMeasureUnit = {
            navigator.navigate(AddEditMeasureUnitScreenDestination(unitName = it))
        }
    )
}

@VisibleForTesting
@Composable
internal fun AddEditMarketItemScreenContent(
    modifier: Modifier = Modifier,
    title: String = CREATE_NEW_ITEM,
    icon: ImageVector = PoposIcons.Add,
    state: AddEditMarketItemState,
    typeNames: List<MarketTypeIdAndName>,
    measureUnits: List<MeasureUnit>,
    typeError: String?,
    nameError: String?,
    amountError: String?,
    unitError: String?,
    onEvent: (AddEditMarketItemEvent) -> Unit,
    onBackClick: () -> Unit,
    onClickAddMarketType: (String) -> Unit,
    onClickAddMeasureUnit: (String) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val enableBtn = listOf(typeError, nameError, amountError, unitError).all {
        it == null
    }

    var expanded by remember { mutableStateOf(false) }
    var measureExpanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    PoposSecondaryScaffold(
        modifier = modifier,
        title = title,
        showBottomBar = true,
        showBackButton = true,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_MARKET_ITEM_BUTTON),
                text = title,
                icon = icon,
                enabled = enableBtn,
                onClick = {
                    onEvent(AddEditMarketItemEvent.AddOrUpdateItem)
                },
            )
        },
        onBackClick = onBackClick,
    ) { paddingValues ->
        TrackScrollJank(
            scrollableState = lazyListState,
            stateName = "AddEditMarketItemScreen::Fields",
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            state = lazyListState,
        ) {
            item(MARKET_ITEM_TYPE_FIELD) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                // This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        value = state.marketType.typeName,
                        label = MARKET_ITEM_TYPE_FIELD,
                        leadingIcon = PoposIcons.Radar,
                        isError = typeError != null,
                        errorText = typeError,
                        errorTextTag = MARKET_ITEM_TYPE_ERROR_TAG,
                        readOnly = false,
                        showClearIcon = state.marketType.typeName.isNotEmpty(),
                        onValueChange = {
                            expanded = true
                            onEvent(
                                AddEditMarketItemEvent.ItemTypeChanged(
                                    MarketTypeIdAndName(typeId = 0, typeName = it),
                                ),
                            )
                        },
                        onClickClearIcon = {
                            expanded = true
                            onEvent(
                                AddEditMarketItemEvent.ItemTypeChanged(
                                    MarketTypeIdAndName(typeId = 0, typeName = "")
                                )
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                    )

                    DropdownMenu(
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        properties = PopupProperties(
                            focusable = false,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            excludeFromSystemGesture = true,
                            clippingEnabled = true,
                        ),
                    ) {
                        typeNames.forEachIndexed { index, marketType ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(marketType.typeName)
                                    .fillMaxWidth(),
                                text = { Text(marketType.typeName) },
                                onClick = {
                                    expanded = false
                                    onEvent(
                                        AddEditMarketItemEvent.ItemTypeChanged(marketType),
                                    )
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )

                            if (index != typeNames.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 0.8.dp,
                                    color = Color.Gray,
                                )
                            }
                        }

                        if (typeNames.isEmpty()) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                enabled = false,
                                onClick = {},
                                text = {
                                    Text(
                                        text = "Market types are not available, Click below to create new",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.CenterHorizontally),
                                    )
                                },
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                onClickAddMarketType(state.marketType.typeName)
                            },
                            text = {
                                Text(
                                    text = "Create new Type",
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = PoposIcons.Add,
                                    contentDescription = "Create",
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = PoposIcons.ArrowRightAlt,
                                    contentDescription = "trailing",
                                )
                            },
                        )
                    }
                }
            }

            item(MARKET_ITEM_NAME_FIELD) {
                StandardOutlinedTextField(
                    value = state.itemName,
                    label = MARKET_ITEM_NAME_FIELD,
                    leadingIcon = PoposIcons.WorkOutline,
                    isError = nameError != null,
                    errorText = nameError,
                    errorTextTag = MARKET_ITEM_NAME_ERROR_TAG,
                    showClearIcon = state.itemName.isNotEmpty(),
                    onValueChange = {
                        onEvent(AddEditMarketItemEvent.ItemNameChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditMarketItemEvent.ItemNameChanged(""))
                    },
                )
            }

            item(MARKET_ITEM_MEASURE_FIELD) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                // This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            },
                        value = state.itemMeasureUnit.unitName,
                        label = MARKET_ITEM_MEASURE_FIELD,
                        leadingIcon = PoposIcons.MonitorWeight,
                        isError = unitError != null,
                        errorText = unitError,
                        errorTextTag = MARKET_ITEM_MEASURE_ERROR_TAG,
                        readOnly = false,
                        showClearIcon = state.itemMeasureUnit.unitName.isNotEmpty(),
                        onValueChange = {
                            measureExpanded = true
                            onEvent(AddEditMarketItemEvent.ItemMeasureUnitNameChanged(it))
                        },
                        onClickClearIcon = {
                            measureExpanded = true
                            onEvent(AddEditMarketItemEvent.ItemMeasureUnitNameChanged(""))
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = measureExpanded)
                        },
                    )

                    DropdownMenu(
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                        expanded = measureExpanded,
                        onDismissRequest = { measureExpanded = false },
                        properties = PopupProperties(
                            focusable = false,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            excludeFromSystemGesture = true,
                            clippingEnabled = true,
                        ),
                    ) {
                        measureUnits.forEachIndexed { index, unit ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(unit.unitName)
                                    .fillMaxWidth(),
                                text = { Text(unit.unitName) },
                                onClick = {
                                    measureExpanded = false
                                    onEvent(
                                        AddEditMarketItemEvent.ItemMeasureUnitChanged(unit),
                                    )
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )

                            if (index != measureUnits.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 0.8.dp,
                                    color = Color.Gray,
                                )
                            }
                        }

                        if (measureUnits.isEmpty()) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                enabled = false,
                                onClick = {},
                                text = {
                                    Text(
                                        text = "Measure units not available, Click below to create new.",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.CenterHorizontally),
                                    )
                                },
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                onClickAddMeasureUnit(state.itemMeasureUnit.unitName)
                            },
                            text = {
                                Text(
                                    text = "Create New Unit",
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = PoposIcons.Add,
                                    contentDescription = "Create",
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = PoposIcons.ArrowRightAlt,
                                    contentDescription = "trailing",
                                )
                            },
                        )
                    }
                }
            }

            item(MARKET_ITEM_PRICE_FIELD) {
                StandardOutlinedTextField(
                    value = state.itemPrice ?: "",
                    label = MARKET_ITEM_PRICE_FIELD,
                    leadingIcon = PoposIcons.Rupee,
                    isError = amountError != null,
                    errorText = amountError,
                    errorTextTag = MARKET_ITEM_PRICE_ERROR_TAG,
                    keyboardType = KeyboardType.Number,
                    showClearIcon = !state.itemPrice.isNullOrEmpty(),
                    onValueChange = {
                        onEvent(AddEditMarketItemEvent.ItemPriceChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditMarketItemEvent.ItemPriceChanged(""))
                    },
                )
            }

            item(MARKET_LIST_ITEM_DESC) {
                StandardOutlinedTextField(
                    value = state.itemDesc ?: "",
                    label = MARKET_LIST_ITEM_DESC,
                    leadingIcon = PoposIcons.Note,
                    showClearIcon = !state.itemDesc.isNullOrEmpty(),
                    onValueChange = {
                        onEvent(AddEditMarketItemEvent.ItemDescriptionChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditMarketItemEvent.ItemDescriptionChanged(""))
                    },
                )
            }
        }
    }
}


@DevicePreviews
@Composable
private fun AddEditMarketItemScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AddEditMarketItemScreenContent(
            modifier = modifier,
            state = AddEditMarketItemState(
                marketType = MarketTypeIdAndName(
                    typeId = 8704,
                    typeName = "Marcy Dawson",
                ),
                itemName = "Xavier Randolph",
                itemMeasureUnit = MeasureUnit(
                    unitId = 5982,
                    unitName = "Johnnie Adams",
                    unitValue = 2.3,
                ),
                itemPrice = null,
                itemDesc = null,
            ),
            typeNames = listOf(),
            measureUnits = listOf(),
            typeError = null,
            nameError = null,
            amountError = null,
            unitError = null,
            onEvent = {},
            onBackClick = {},
            onClickAddMarketType = {},
            onClickAddMeasureUnit = {}
        )
    }
}