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

package com.niyaj.address.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.AddressTestTags
import com.niyaj.common.tags.AddressTestTags.ADDRESS_FULL_NAME_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_FULL_NAME_FIELD
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SHORT_NAME_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SHORT_NAME_FIELD
import com.niyaj.common.tags.AddressTestTags.CREATE_ADDRESS_SCREEN
import com.niyaj.common.tags.AddressTestTags.UPDATE_ADDRESS_SCREEN
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun AddEditAddressScreen(
    addressId: Int = 0,
    navigator: DestinationsNavigator,
    viewModel: AddEditAddressViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val shortNameError by viewModel.shortNameError.collectAsStateWithLifecycle()
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

    val title = if (addressId == 0) CREATE_ADDRESS_SCREEN else UPDATE_ADDRESS_SCREEN
    val icon = if (addressId == 0) PoposIcons.Add else PoposIcons.EditLocation

    TrackScreenViewEvent(screenName = "Add/Edit Address Screen")

    AddEditAddressScreenContent(
        modifier = Modifier,
        title = title,
        icon = icon,
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        nameError = nameError,
        shortNameError = shortNameError,
        onBackClick = navigator::navigateUp,
    )
}

@VisibleForTesting
@Composable
internal fun AddEditAddressScreenContent(
    modifier: Modifier = Modifier,
    title: String = CREATE_ADDRESS_SCREEN,
    icon: ImageVector = PoposIcons.Add,
    state: AddEditAddressState,
    onEvent: (AddEditAddressEvent) -> Unit,
    nameError: String? = null,
    shortNameError: String? = null,
    onBackClick: () -> Unit,
) {
    val enableBtn = nameError == null && shortNameError == null

    PoposSecondaryScaffold(
        modifier = modifier,
        title = title,
        showBackButton = true,
        showBottomBar = true,
        onBackClick = onBackClick,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(AddressTestTags.ADD_EDIT_ADDRESS_BTN)
                    .padding(horizontal = SpaceMedium, vertical = SpaceLarge),
                text = title,
                enabled = enableBtn,
                icon = icon,
                onClick = {
                    onEvent(AddEditAddressEvent.CreateOrUpdateAddress)
                },
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .testTag(title)
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(ADDRESS_FULL_NAME_FIELD) {
                StandardOutlinedTextField(
                    value = state.addressName,
                    label = ADDRESS_FULL_NAME_FIELD,
                    leadingIcon = PoposIcons.Address,
                    isError = nameError != null,
                    errorText = nameError,
                    errorTextTag = ADDRESS_FULL_NAME_ERROR,
                    showClearIcon = state.addressName.isNotEmpty(),
                    onValueChange = {
                        onEvent(AddEditAddressEvent.AddressNameChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditAddressEvent.AddressNameChanged(""))
                    },
                )
            }

            item(ADDRESS_SHORT_NAME_FIELD) {
                StandardOutlinedTextField(
                    value = state.shortName,
                    label = ADDRESS_SHORT_NAME_FIELD,
                    leadingIcon = PoposIcons.Rupee,
                    isError = shortNameError != null,
                    errorText = shortNameError,
                    errorTextTag = ADDRESS_SHORT_NAME_ERROR,
                    showClearIcon = state.shortName.isNotEmpty(),
                    onValueChange = {
                        onEvent(AddEditAddressEvent.ShortNameChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditAddressEvent.ShortNameChanged(""))
                    },
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditAddressScreenContentPreview() {
    AddEditAddressScreenContent(
        modifier = Modifier,
        state = AddEditAddressState(
            shortName = "New Ladies",
            addressName = "NL",
        ),
        onEvent = {},
        nameError = null,
        shortNameError = null,
        onBackClick = {},
    )
}
