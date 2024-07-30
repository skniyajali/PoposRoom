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

package com.niyaj.addonitem.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.AddOnTestTags
import com.niyaj.common.tags.AddOnTestTags.ADDON_APPLIED_SWITCH
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_ERROR_TAG
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_FIELD
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_ERROR_TAG
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_FIELD
import com.niyaj.common.tags.AddOnTestTags.ADD_EDIT_ADDON_SCREEN
import com.niyaj.common.tags.AddOnTestTags.CREATE_NEW_ADD_ON
import com.niyaj.common.tags.AddOnTestTags.EDIT_ADD_ON_ITEM
import com.niyaj.common.utils.safeString
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination(route = Screens.ADD_EDIT_ADD_ON_ITEM_SCREEN)
@Composable
fun AddEditAddOnItemScreen(
    itemId: Int = 0,
    navigator: DestinationsNavigator,
    viewModel: AddEditAddOnItemViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val priceError = viewModel.priceError.collectAsStateWithLifecycle().value

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

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

    val title = if (itemId == 0) CREATE_NEW_ADD_ON else EDIT_ADD_ON_ITEM
    val icon = if (itemId == 0) PoposIcons.Add else PoposIcons.Edit

    TrackScreenViewEvent(screenName = Screens.ADD_EDIT_ADD_ON_ITEM_SCREEN)

    AddEditAddOnItemScreenContent(
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
internal fun AddEditAddOnItemScreenContent(
    modifier: Modifier = Modifier,
    title: String = CREATE_NEW_ADD_ON,
    icon: ImageVector = PoposIcons.Add,
    state: AddEditAddOnItemState,
    nameError: String? = null,
    priceError: String? = null,
    onEvent: (AddEditAddOnItemEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val enableBtn = nameError == null && priceError == null

    PoposSecondaryScaffold(
        modifier = modifier,
        title = title,
        showBackButton = true,
        onBackClick = onBackClick,
        showBottomBar = true,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .testTag(AddOnTestTags.ADD_EDIT_ADDON_BUTTON)
                    .fillMaxWidth(),
                text = title,
                enabled = enableBtn,
                icon = icon,
                onClick = {
                    onEvent(AddEditAddOnItemEvent.CreateUpdateAddOnItem)
                },
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    ) { paddingValues ->
        TrackScrollJank(scrollableState = lazyListState, stateName = "Create New Addon::Fields")

        LazyColumn(
            modifier = Modifier
                .testTag(ADD_EDIT_ADDON_SCREEN)
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(ADDON_NAME_FIELD) {
                StandardOutlinedTextField(
                    value = state.itemName,
                    label = ADDON_NAME_FIELD,
                    leadingIcon = PoposIcons.Category,
                    isError = nameError != null,
                    errorText = nameError,
                    errorTextTag = ADDON_NAME_ERROR_TAG,
                    showClearIcon = state.itemName.isNotEmpty(),
                    onValueChange = {
                        onEvent(AddEditAddOnItemEvent.ItemNameChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditAddOnItemEvent.ItemNameChanged(""))
                    },
                )
            }

            item(ADDON_PRICE_FIELD) {
                StandardOutlinedTextField(
                    value = state.itemPrice.safeString,
                    label = ADDON_PRICE_FIELD,
                    leadingIcon = PoposIcons.Rupee,
                    isError = priceError != null,
                    errorText = priceError,
                    keyboardType = KeyboardType.Number,
                    errorTextTag = ADDON_PRICE_ERROR_TAG,
                    showClearIcon = state.itemPrice.safeString.isNotEmpty(),
                    onValueChange = {
                        onEvent(AddEditAddOnItemEvent.ItemPriceChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditAddOnItemEvent.ItemPriceChanged(""))
                    },
                )
            }

            item(ADDON_APPLIED_SWITCH) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        modifier = Modifier.testTag(ADDON_APPLIED_SWITCH),
                        checked = state.isApplicable,
                        onCheckedChange = {
                            onEvent(AddEditAddOnItemEvent.ItemApplicableChanged)
                        },
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = if (state.isApplicable) {
                            "Marked as applied"
                        } else {
                            "Marked as not applied"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditAddOnItemScreenContentPreview() {
    PoposRoomTheme {
        AddEditAddOnItemScreenContent(
            state = AddEditAddOnItemState(
                itemName = "Lily McClure",
                itemPrice = 6426,
                isApplicable = false,
            ),
            nameError = null,
            priceError = null,
            onEvent = {},
            onBackClick = {},
        )
    }
}