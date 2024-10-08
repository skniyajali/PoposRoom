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

package com.niyaj.feature.market.marketType.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketTypeTags.ADD_EDIT_MARKET_TYPE_BUTTON
import com.niyaj.common.tags.MarketTypeTags.CREATE_NEW_TYPE
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_DESC_FIELD
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_ERROR_TAG
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_FIELD
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_LIST_ERROR_TAG
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_LIST_MSG
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_LIST_TYPES
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_SUPPLIER_FIELD
import com.niyaj.common.tags.MarketTypeTags.UPDATE_TYPE
import com.niyaj.common.utils.safeString
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposTonalIconButton
import com.niyaj.designsystem.components.StandardRoundedFilterChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun AddEditMarketTypeScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    typeId: Int = 0,
    typeName: String? = null,
    viewModel: AddEditMarketTypeViewModel = hiltViewModel(),
) {
    val typeError by viewModel.typeNameError.collectAsStateWithLifecycle()
    val listNameError by viewModel.listNameError.collectAsStateWithLifecycle()
    val listTypesError by viewModel.listTypesError.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val listTypes = viewModel.listTypes.toList()
    val selectedList = viewModel.selectedTypes.toList()

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

    val title = if (typeId == 0) CREATE_NEW_TYPE else UPDATE_TYPE
    val icon = if (typeId == 0) PoposIcons.Add else PoposIcons.Edit

    TrackScreenViewEvent(screenName = "$title/typeId=$typeId/typeName=$typeName")

    AddEditMarketTypeScreenContent(
        title = title,
        icon = icon,
        state = viewModel.state,
        typeError = typeError,
        listNameError = listNameError,
        listTypesError = listTypesError,
        listTypes = listTypes,
        selectedList = selectedList,
        modifier = modifier,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@VisibleForTesting
@Composable
internal fun AddEditMarketTypeScreenContent(
    state: AddEditMarketTypeState,
    typeError: String?,
    listNameError: String?,
    listTypesError: String?,
    listTypes: List<String>,
    selectedList: List<String>,
    onEvent: (AddEditMarketTypeEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = CREATE_NEW_TYPE,
    icon: ImageVector = PoposIcons.Add,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val hasError = listOf(typeError, listNameError, listTypesError).any { it != null }

    PoposSecondaryScaffold(
        title = title,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = true,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_MARKET_TYPE_BUTTON),
                text = title,
                icon = icon,
                enabled = !hasError,
                onClick = {
                    onEvent(AddEditMarketTypeEvent.SaveMarketType)
                },
            )
        },
    ) { paddingValues ->
        TrackScrollJank(
            scrollableState = lazyListState,
            stateName = "AddEditMarketTypeScreen::Fields",
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            state = lazyListState,
        ) {
            item(MARKET_TYPE_FIELD) {
                StandardOutlinedTextField(
                    label = MARKET_TYPE_FIELD,
                    leadingIcon = PoposIcons.Radar,
                    value = state.typeName,
                    onValueChange = {
                        onEvent(AddEditMarketTypeEvent.TypeNameChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = typeError != null,
                    errorText = typeError,
                    readOnly = false,
                    errorTextTag = MARKET_TYPE_ERROR_TAG,
                )
            }

            item(MARKET_TYPE_DESC_FIELD) {
                StandardOutlinedTextField(
                    label = MARKET_TYPE_DESC_FIELD,
                    leadingIcon = PoposIcons.Note,
                    value = state.typeDesc ?: "",
                    onValueChange = {
                        onEvent(AddEditMarketTypeEvent.TypeDescChanged(it))
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }

            item(MARKET_TYPE_SUPPLIER_FIELD) {
                StandardOutlinedTextField(
                    label = MARKET_TYPE_SUPPLIER_FIELD,
                    leadingIcon = PoposIcons.Person,
                    value = state.supplierId.safeString,
                    onValueChange = {
                        onEvent(AddEditMarketTypeEvent.SupplierIdChanged(it.toInt()))
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = false,
                    readOnly = true,
                )
            }

            item(MARKET_TYPE_LIST_TYPES) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    StandardOutlinedTextField(
                        label = MARKET_TYPE_LIST_TYPES,
                        leadingIcon = PoposIcons.ListAlt,
                        value = state.listType,
                        onValueChange = {
                            onEvent(AddEditMarketTypeEvent.ListTypeChanged(it))
                        },
                        isError = listNameError != null,
                        errorText = listNameError,
                        trailingIcon = {
                            PoposTonalIconButton(
                                icon = PoposIcons.Add,
                                onClick = {
                                    onEvent(AddEditMarketTypeEvent.OnSelectListType(state.listType))
                                },
                                enabled = state.listType.isNotEmpty() && listNameError == null,
                            )
                        },
                        errorTextTag = MARKET_TYPE_LIST_ERROR_TAG,
                        message = MARKET_TYPE_LIST_MSG,
                        suffix = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(SpaceMini),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = "Click here")
                                Icon(
                                    imageVector = PoposIcons.ArrowRightAlt,
                                    contentDescription = "Create",
                                )
                            }
                        },
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(SpaceMini, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        listTypes.forEach {
                            StandardRoundedFilterChip(
                                text = it,
                                selected = selectedList.contains(it),
                                onClick = {
                                    onEvent(AddEditMarketTypeEvent.OnSelectListType(it))
                                },
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = listTypesError != null,
                    ) {
                        NoteText(text = listTypesError.toString())
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditMarketTypeScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AddEditMarketTypeScreenContent(
            modifier = modifier,
            state = AddEditMarketTypeState(
                typeName = "Carmelo Gross",
                typeDesc = null,
                supplierId = 1535,
                listType = "IN_STOCK",
            ),
            typeError = null,
            listNameError = null,
            listTypesError = null,
            listTypes = listOf(),
            selectedList = listOf(),
            onEvent = {},
            onBackClick = {},
        )
    }
}
