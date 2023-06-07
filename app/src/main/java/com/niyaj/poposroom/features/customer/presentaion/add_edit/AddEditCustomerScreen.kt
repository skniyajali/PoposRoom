package com.niyaj.poposroom.features.customer.presentaion.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.niyaj.poposroom.features.common.components.StandardButton
import com.niyaj.poposroom.features.common.components.StandardTextField
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.ADD_EDIT_CUSTOMER_BUTTON
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.ADD_EDIT_CUSTOMER_SCREEN
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CREATE_NEW_CUSTOMER
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_EMAIL_ERROR
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_EMAIL_FIELD
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_NAME_ERROR
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_NAME_FIELD
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_PHONE_ERROR
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_PHONE_FIELD
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.EDIT_CUSTOMER_ITEM

@Composable
fun AddEditCustomerScreen(
    customerId: Int = 0,
    closeSheet: () -> Unit,
    viewModel: AddEditCustomerViewModel = hiltViewModel(),
) {
    val phoneError = viewModel.phoneError.collectAsStateWithLifecycle().value
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val emailError = viewModel.emailError.collectAsStateWithLifecycle().value

    val enableBtn = phoneError == null && nameError == null && emailError == null

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = Unit) {
        viewModel.resetFields()
    }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when(data) {
                is UiEvent.IsLoading -> {}
                is UiEvent.OnError -> {
                    closeSheet()
                }
                is UiEvent.OnSuccess -> {
                    closeSheet()
                }
            }
        }
    }

    LaunchedEffect(key1 = customerId) {
        viewModel.getCustomerById(customerId)
    }

    Column(
        modifier = Modifier
            .testTag(ADD_EDIT_CUSTOMER_SCREEN)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
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

        Spacer(modifier = Modifier.height(SpaceSmall))

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

        Spacer(modifier = Modifier.height(SpaceSmall))

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

        StandardButton(
            modifier = Modifier.testTag(ADD_EDIT_CUSTOMER_BUTTON),
            text = if (customerId == 0) CREATE_NEW_CUSTOMER else EDIT_CUSTOMER_ITEM,
            icon = if (customerId == 0) Icons.Default.Add else Icons.Default.Edit,
            enabled = enableBtn,
            onClick = {
                viewModel.onEvent(AddEditCustomerEvent.CreateOrUpdateCustomer(customerId))
            }
        )
    }
}