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

package com.niyaj.market.marketList.createOrUpdate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MarketListTestTags.CREATE_NEW_LIST
import com.niyaj.common.tags.MarketListTestTags.TYPES_NOTE_TEXT
import com.niyaj.common.tags.MarketListTestTags.UPDATE_LIST
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposOutlinedAssistChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.MarketTypeIdAndListTypes
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.StandardBottomSheet
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun AddEditMarketListScreen(
    marketId: Int = 0,
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AddEditMarketListViewModel = hiltViewModel(),
) {
    val dialogState = rememberMaterialDialogState()

    val listTypes = viewModel.marketTypes.collectAsStateWithLifecycle().value
    val selectedDate = viewModel.selectedDate.collectAsStateWithLifecycle().value
    val isError = viewModel.isError.collectAsStateWithLifecycle().value
    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = event) {
        event?.let {
            when (it) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(it.errorMessage)
                }
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(it.successMessage)
                }
            }
        }
    }

    val title = if (marketId == 0) CREATE_NEW_LIST else UPDATE_LIST

    TrackScreenViewEvent(screenName = "$title/marketId=$marketId")

    StandardBottomSheet(
        title = title,
        onBackClick = navigator::navigateUp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(SpaceMini),
                    )
                    .padding(SpaceSmall),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
                ) {
                    CircularBox(
                        icon = PoposIcons.CalenderMonth,
                        doesSelected = false,
                    )

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(SpaceMini),
                    ) {
                        Text(
                            text = "Market Date".uppercase(),
                            style = MaterialTheme.typography.bodySmall,
                        )

                        Text(
                            text = selectedDate.toPrettyDate(),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }

                PoposOutlinedAssistChip(
                    text = "Change Date",
                    icon = PoposIcons.CalenderMonth,
                    onClick = dialogState::show,
                    trailingIcon = PoposIcons.ArrowDropDown,
                )
            }

            Text(
                text = "Choose Market List Types",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
                maxItemsInEachRow = 2,
            ) {
                listTypes.forEach {
                    MarketTypeBox(
                        type = it,
                        isSelected = viewModel::isTypeChecked,
                        onListTypeClick = viewModel::updateSelectedListTypes,
                        isListTypeChecked = viewModel::isListTypeChecked,
                    )
                }
            }

            AnimatedVisibility(
                visible = isError,
            ) {
                NoteCard(text = TYPES_NOTE_TEXT)
            }

            PoposButton(
                text = title,
                enabled = !isError,
                icon = if (marketId == 0) PoposIcons.Add else PoposIcons.Edit,
                onClick = viewModel::createOrUpdateMarketList,
            )
        }
    }

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        },
    ) {
        datepicker(
            allowedDateValidator = { date ->
                date <= LocalDate.now()
            },
        ) { date ->
            viewModel.updateSelectedDate(date.toMilliSecond)
        }
    }
}

@Composable
fun MarketTypeBox(
    modifier: Modifier = Modifier,
    type: MarketTypeIdAndListTypes,
    isSelected: (typeId: Int) -> Boolean,
    isListTypeChecked: (typeId: Int, listName: String) -> Boolean,
    onListTypeClick: (typeId: Int, listName: String) -> Unit,
) {
    val borderStroke =
        if (isSelected(type.typeId)) BorderStroke(1.dp, MaterialTheme.colorScheme.secondary) else null

    Surface(
        modifier = modifier
            .fillMaxWidth(0.490f),
        border = borderStroke,
        shape = RoundedCornerShape(SpaceMini),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .clickable { onListTypeClick(type.typeId, type.listTypes.first()) }
                    .padding(SpaceSmallMax),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = type.typeName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            AnimatedVisibility(
                visible = isSelected(type.typeId),
            ) {
                Spacer(modifier = Modifier.height(SpaceSmall))

                Column {
                    type.listTypes.forEachIndexed { key, listType ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onListTypeClick(type.typeId, listType) },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = listType,
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                fontFamily = FontFamily.Cursive,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(SpaceSmall),
                            )

                            Checkbox(
                                checked = isListTypeChecked(type.typeId, listType),
                                onCheckedChange = {
                                    onListTypeClick(type.typeId, listType)
                                },
                            )
                        }

                        if (key != type.listTypes.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
