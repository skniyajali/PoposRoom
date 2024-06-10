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

package com.niyaj.charges.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.ChargesTestTags.ADD_EDIT_CHARGES_BUTTON
import com.niyaj.common.tags.ChargesTestTags.ADD_EDIT_CHARGES_SCREEN
import com.niyaj.common.tags.ChargesTestTags.CHARGES_AMOUNT_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_AMOUNT_FIELD
import com.niyaj.common.tags.ChargesTestTags.CHARGES_APPLIED_SWITCH
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_FIELD
import com.niyaj.common.tags.ChargesTestTags.CREATE_NEW_CHARGES
import com.niyaj.common.tags.ChargesTestTags.EDIT_CHARGES_ITEM
import com.niyaj.common.utils.safeString
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
fun AddEditChargesScreen(
    chargesId: Int = 0,
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AddEditChargesViewModel = hiltViewModel(),
) {
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val priceError by viewModel.priceError.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val title = if (chargesId == 0) CREATE_NEW_CHARGES else EDIT_CHARGES_ITEM
    val icon = if (chargesId == 0) PoposIcons.Add else PoposIcons.Edit

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

    TrackScreenViewEvent(screenName = "Add Edit Charges Screen - $chargesId")

    AddEditChargesScreenContent(
        modifier = Modifier,
        title = title,
        icon = icon,
        state = viewModel.addEditState,
        nameError = nameError,
        priceError = priceError,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
    )
}


@VisibleForTesting
@Composable
internal fun AddEditChargesScreenContent(
    modifier: Modifier = Modifier,
    title: String = CREATE_NEW_CHARGES,
    icon: ImageVector = PoposIcons.Add,
    state: AddEditChargesState,
    nameError: String? = null,
    priceError: String? = null,
    onEvent: (AddEditChargesEvent) -> Unit,
    onBackClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val enableBtn = nameError == null && priceError == null

    PoposSecondaryScaffold(
        modifier = modifier,
        title = title,
        showBackButton = true,
        showBottomBar = true,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_CHARGES_BUTTON),
                text = title,
                icon = icon,
                enabled = enableBtn,
                onClick = {
                    onEvent(AddEditChargesEvent.CreateOrUpdateCharges)
                },
            )
        },
        onBackClick = onBackClick,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    ) { paddingValues ->
        TrackScrollJank(
            scrollableState = lazyListState,
            stateName = "Add/Edit Charges Screen::Fields",
        )

        LazyColumn(
            modifier = Modifier
                .testTag(ADD_EDIT_CHARGES_SCREEN)
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item(CHARGES_NAME_FIELD) {
                StandardOutlinedTextField(
                    value = state.chargesName,
                    label = CHARGES_NAME_FIELD,
                    leadingIcon = PoposIcons.Category,
                    isError = nameError != null,
                    errorText = nameError,
                    errorTextTag = CHARGES_NAME_ERROR,
                    showClearIcon = state.chargesName.isNotEmpty(),
                    onValueChange = {
                        onEvent(AddEditChargesEvent.ChargesNameChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditChargesEvent.ChargesNameChanged(""))
                    },
                )
            }

            item(CHARGES_AMOUNT_FIELD) {
                StandardOutlinedTextField(
                    value = state.chargesPrice.safeString,
                    label = CHARGES_AMOUNT_FIELD,
                    leadingIcon = PoposIcons.Rupee,
                    isError = priceError != null,
                    errorText = priceError,
                    keyboardType = KeyboardType.Number,
                    errorTextTag = CHARGES_AMOUNT_ERROR,
                    showClearIcon = state.chargesPrice.safeString.isNotEmpty(),
                    onValueChange = {
                        onEvent(AddEditChargesEvent.ChargesPriceChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditChargesEvent.ChargesPriceChanged(""))
                    },
                )
            }

            item(CHARGES_APPLIED_SWITCH) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        modifier = Modifier.testTag(CHARGES_APPLIED_SWITCH),
                        checked = state.chargesApplicable,
                        onCheckedChange = {
                            onEvent(AddEditChargesEvent.ChargesApplicableChanged)
                        },
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = if (state.chargesApplicable) {
                            "Marked as applied"
                        } else {
                            "Marked as not applied"
                        },
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditChargesScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AddEditChargesScreenContent(
            modifier = modifier,
            state = AddEditChargesState(
                chargesName = "New Charges",
                chargesPrice = 10,
                chargesApplicable = true,
            ),
            nameError = null,
            priceError = null,
            onEvent = {},
            onBackClick = {},
        )
    }
}