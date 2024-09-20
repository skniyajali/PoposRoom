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

package com.niyaj.feature.market.measureUnit.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.MeasureUnitTestTags.ADD_EDIT_UNIT_BUTTON
import com.niyaj.common.tags.MeasureUnitTestTags.CREATE_NEW_UNIT
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_ERROR_TAG
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_FIELD
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_VALUE_ERROR_TAG
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_VALUE_FIELD
import com.niyaj.common.tags.MeasureUnitTestTags.UPDATE_UNIT
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
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
fun AddEditMeasureUnitScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    unitId: Int = 0,
    unitName: String? = null,
    viewModel: AddEditMeasureUnitViewModel = hiltViewModel(),
) {
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val valueError by viewModel.valueError.collectAsStateWithLifecycle()

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

    val title = if (unitId == 0) CREATE_NEW_UNIT else UPDATE_UNIT
    val icon = if (unitId == 0) PoposIcons.Add else PoposIcons.Edit

    TrackScreenViewEvent(screenName = "$title/unitId=$unitId/unitName=$unitName")

    AddEditMeasureUnitScreenContent(
        title = title,
        icon = icon,
        state = viewModel.state,
        nameError = nameError,
        valueError = valueError,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
        modifier = modifier,
    )
}

@VisibleForTesting
@Composable
internal fun AddEditMeasureUnitScreenContent(
    state: AddEditMeasureUnitState,
    nameError: String?,
    valueError: String?,
    onEvent: (AddEditMeasureUnitEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = CREATE_NEW_UNIT,
    icon: ImageVector = PoposIcons.Add,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val enableBtn = listOf(nameError, valueError).all { it == null }

    PoposSecondaryScaffold(
        title = title,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = true,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_UNIT_BUTTON),
                text = title,
                icon = icon,
                enabled = enableBtn,
                onClick = {
                    onEvent(AddEditMeasureUnitEvent.SaveOrUpdateMeasureUnit)
                },
            )
        },
    ) { paddingValues ->
        TrackScrollJank(
            scrollableState = lazyListState,
            stateName = "AddEditMeasureUnitScreen::Fields",
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(UNIT_NAME_FIELD) {
                StandardOutlinedTextField(
                    label = UNIT_NAME_FIELD,
                    leadingIcon = PoposIcons.LineWeight,
                    value = state.unitName,
                    onValueChange = {
                        onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged(it))
                    },
                    isError = nameError != null,
                    errorText = nameError,
                    showClearIcon = state.unitName.isNotEmpty(),
                    errorTextTag = UNIT_NAME_ERROR_TAG,
                    onClickClearIcon = {
                        onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged(""))
                    },
                )
            }

            item(UNIT_VALUE_FIELD) {
                StandardOutlinedTextField(
                    label = UNIT_VALUE_FIELD,
                    leadingIcon = PoposIcons.Api,
                    value = state.unitValue,
                    onValueChange = {
                        onEvent(AddEditMeasureUnitEvent.MeasureUnitValueChanged(it))
                    },
                    isError = valueError != null,
                    errorText = valueError,
                    keyboardType = KeyboardType.Number,
                    showClearIcon = state.unitValue.isNotEmpty(),
                    errorTextTag = UNIT_VALUE_ERROR_TAG,
                    onClickClearIcon = {
                        onEvent(AddEditMeasureUnitEvent.MeasureUnitValueChanged(""))
                    },
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditMeasureUnitScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AddEditMeasureUnitScreenContent(
            modifier = modifier,
            state = AddEditMeasureUnitState(
                unitName = "Litter",
                unitValue = "0.5",
            ),
            nameError = null,
            valueError = null,
            onEvent = {},
            onBackClick = {},
        )
    }
}
