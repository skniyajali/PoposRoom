package com.niyaj.customer.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.CustomerTestTags.ADD_EDIT_CUSTOMER_BUTTON
import com.niyaj.common.tags.CustomerTestTags.ADD_EDIT_CUSTOMER_SCREEN
import com.niyaj.common.tags.CustomerTestTags.CREATE_NEW_CUSTOMER
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_FIELD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_FIELD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_FIELD
import com.niyaj.common.tags.CustomerTestTags.EDIT_CUSTOMER_ITEM
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardScaffoldWithOutDrawer
import com.niyaj.ui.components.StandardTextField
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun AddEditCustomerScreen(
    customerId: Int = 0,
    navController: NavController,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AddEditCustomerViewModel = hiltViewModel(),
) {
    val phoneError = viewModel.phoneError.collectAsStateWithLifecycle().value
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val emailError = viewModel.emailError.collectAsStateWithLifecycle().value

    val enableBtn = phoneError == null && nameError == null && emailError == null

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value
    val title = if (customerId == 0) CREATE_NEW_CUSTOMER else EDIT_CUSTOMER_ITEM

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when(data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    StandardScaffoldWithOutDrawer(
        title = title,
        onBackClick = {
            navController.navigateUp()
        },
        showBottomBar = enableBtn,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .testTag(ADD_EDIT_CUSTOMER_BUTTON)
                    .padding(horizontal = SpaceSmallMax),
                text = title,
                icon = if (customerId == 0) Icons.Default.Add else Icons.Default.Edit,
                enabled = enableBtn,
                onClick = {
                    viewModel.onEvent(AddEditCustomerEvent.CreateOrUpdateCustomer(customerId))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .testTag(ADD_EDIT_CUSTOMER_SCREEN)
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            StandardTextField(
                value = viewModel.addEditState.customerPhone,
                label = CUSTOMER_PHONE_FIELD,
                leadingIcon = Icons.Default.PhoneAndroid,
                isError = phoneError != null,
                errorText = phoneError,
                errorTextTag = CUSTOMER_PHONE_ERROR,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    viewModel.onEvent(AddEditCustomerEvent.CustomerPhoneChanged(it))
                }
            )


            StandardTextField(
                value = viewModel.addEditState.customerName ?: "",
                label = CUSTOMER_NAME_FIELD,
                leadingIcon = Icons.Default.Person,
                isError = nameError != null,
                errorText = nameError,
                errorTextTag = CUSTOMER_NAME_ERROR,
                onValueChange = {
                    viewModel.onEvent(AddEditCustomerEvent.CustomerNameChanged(it))
                }
            )


            StandardTextField(
                value = viewModel.addEditState.customerEmail ?: "",
                label = CUSTOMER_EMAIL_FIELD,
                leadingIcon = Icons.Default.Email,
                isError = emailError != null,
                errorText = emailError,
                errorTextTag = CUSTOMER_EMAIL_ERROR,
                onValueChange = {
                    viewModel.onEvent(AddEditCustomerEvent.CustomerEmailChanged(it))
                }
            )

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}