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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.AddressTestTags
import com.niyaj.common.tags.AddressTestTags.ADDRESS_FULL_NAME_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_FULL_NAME_FIELD
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SHORT_NAME_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SHORT_NAME_FIELD
import com.niyaj.common.tags.AddressTestTags.CREATE_ADDRESS_SCREEN
import com.niyaj.common.tags.AddressTestTags.CREATE_NEW_ADDRESS
import com.niyaj.common.tags.AddressTestTags.EDIT_ADDRESS
import com.niyaj.common.tags.AddressTestTags.UPDATE_ADDRESS_SCREEN
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
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
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val shortNameError = viewModel.shortNameError.collectAsStateWithLifecycle().value

    val enableBtn = nameError == null && shortNameError == null

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

    val title = if (addressId == 0) CREATE_ADDRESS_SCREEN else UPDATE_ADDRESS_SCREEN

    TrackScreenViewEvent(screenName = "Add/Edit Address Screen")

    PoposSecondaryScaffold(
        title = title,
        showBackButton = true,
        showBottomBar = true,
        onBackClick = navigator::navigateUp,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .testTag(AddressTestTags.ADD_EDIT_ADDRESS_BTN)
                    .padding(SpaceMedium),
                text = if (addressId == 0) CREATE_NEW_ADDRESS else EDIT_ADDRESS,
                enabled = enableBtn,
                icon = if (addressId == 0) PoposIcons.Add else PoposIcons.EditLocation,
                onClick = {
                    viewModel.onEvent(AddEditAddressEvent.CreateOrUpdateAddress(addressId))
                },
            )
        },
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
                    value = viewModel.state.addressName,
                    label = ADDRESS_FULL_NAME_FIELD,
                    leadingIcon = PoposIcons.Address,
                    isError = nameError != null,
                    errorText = nameError,
                    errorTextTag = ADDRESS_FULL_NAME_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditAddressEvent.AddressNameChanged(it))
                    },
                )
            }

            item(ADDRESS_SHORT_NAME_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.shortName,
                    label = ADDRESS_SHORT_NAME_FIELD,
                    leadingIcon = PoposIcons.Rupee,
                    isError = shortNameError != null,
                    errorText = shortNameError,
                    errorTextTag = ADDRESS_SHORT_NAME_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditAddressEvent.ShortNameChanged(it))
                    },
                )
            }
        }
    }
}
