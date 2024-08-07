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

package com.niyaj.customer.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.niyaj.common.tags.CustomerTestTags.ADD_EDIT_CUSTOMER_BTN
import com.niyaj.common.tags.CustomerTestTags.CREATE_NEW_CUSTOMER
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_FIELD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_FIELD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_FIELD
import com.niyaj.common.tags.CustomerTestTags.EDIT_CUSTOMER_ITEM
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.PhoneNoCountBox
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.ADD_EDIT_CUSTOMER_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination(route = ADD_EDIT_CUSTOMER_SCREEN)
@Composable
fun AddEditCustomerScreen(
    customerId: Int = 0,
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AddEditCustomerViewModel = hiltViewModel(),
) {
    val phoneError by viewModel.phoneError.collectAsStateWithLifecycle()
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val emailError by viewModel.emailError.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val title = if (customerId == 0) CREATE_NEW_CUSTOMER else EDIT_CUSTOMER_ITEM
    val icon = if (customerId == 0) PoposIcons.Add else PoposIcons.Edit

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

    AddEditCustomerScreenContent(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
        modifier = Modifier,
        title = title,
        icon = icon,
        phoneError = phoneError,
        nameError = nameError,
        emailError = emailError,
    )
}

@VisibleForTesting
@Composable
internal fun AddEditCustomerScreenContent(
    state: AddEditCustomerState,
    onEvent: (AddEditCustomerEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = CREATE_NEW_CUSTOMER,
    icon: ImageVector = PoposIcons.Add,
    phoneError: String? = null,
    nameError: String? = null,
    emailError: String? = null,
) {
    val enableBtn = phoneError == null && nameError == null && emailError == null

    TrackScreenViewEvent(screenName = "Add/Edit Customer Screen")

    PoposSecondaryScaffold(
        modifier = modifier,
        title = title,
        showBottomBar = true,
        showBackButton = true,
        onBackClick = onBackClick,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_CUSTOMER_BTN),
                text = title,
                icon = icon,
                enabled = enableBtn,
                onClick = {
                    onEvent(AddEditCustomerEvent.CreateOrUpdateCustomer)
                },
            )
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(CUSTOMER_PHONE_FIELD) {
                StandardOutlinedTextField(
                    value = state.customerPhone,
                    label = CUSTOMER_PHONE_FIELD,
                    leadingIcon = PoposIcons.PhoneAndroid,
                    isError = phoneError != null,
                    errorText = phoneError,
                    errorTextTag = CUSTOMER_PHONE_ERROR,
                    keyboardType = KeyboardType.Number,
                    showClearIcon = state.customerPhone.isNotEmpty(),
                    onValueChange = {
                        onEvent(AddEditCustomerEvent.CustomerPhoneChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditCustomerEvent.CustomerPhoneChanged(""))
                    },
                    suffix = {
                        PhoneNoCountBox(count = state.customerPhone.length)
                    },
                )
            }

            item(CUSTOMER_NAME_FIELD) {
                StandardOutlinedTextField(
                    value = state.customerName ?: "",
                    label = CUSTOMER_NAME_FIELD,
                    leadingIcon = PoposIcons.Person,
                    isError = nameError != null,
                    errorText = nameError,
                    errorTextTag = CUSTOMER_NAME_ERROR,
                    showClearIcon = !state.customerName.isNullOrEmpty(),
                    onValueChange = {
                        onEvent(AddEditCustomerEvent.CustomerNameChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditCustomerEvent.CustomerNameChanged(""))
                    },
                )
            }

            item(CUSTOMER_EMAIL_FIELD) {
                StandardOutlinedTextField(
                    value = state.customerEmail ?: "",
                    label = CUSTOMER_EMAIL_FIELD,
                    leadingIcon = PoposIcons.Email,
                    isError = emailError != null,
                    errorText = emailError,
                    errorTextTag = CUSTOMER_EMAIL_ERROR,
                    showClearIcon = !state.customerEmail.isNullOrEmpty(),
                    onValueChange = {
                        onEvent(AddEditCustomerEvent.CustomerEmailChanged(it))
                    },
                    onClickClearIcon = {
                        onEvent(AddEditCustomerEvent.CustomerEmailChanged(""))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditCustomerScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AddEditCustomerScreenContent(
            state = AddEditCustomerState(
                customerPhone = "9078563412",
                customerName = "New Customer",
                customerEmail = "new@gmail.com",
            ),
            onEvent = {},
            onBackClick = {},
            modifier = modifier,
            phoneError = null,
            nameError = null,
            emailError = null,
        )
    }
}
