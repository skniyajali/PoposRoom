package com.niyaj.poposroom.features.address.presentation.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_FULL_NAME_ERROR
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_FULL_NAME_FIELD
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_SHORT_NAME_ERROR
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_SHORT_NAME_FIELD
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.CREATE_ADDRESS_SCREEN
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.CREATE_NEW_ADDRESS
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.EDIT_ADDRESS
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.UPDATE_ADDRESS_SCREEN
import com.niyaj.poposroom.features.common.components.StandardButton
import com.niyaj.poposroom.features.common.components.StandardTextField
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.UiEvent

@Composable
fun AddEditAddressScreen(
    addressId: Int = 0,
    closeSheet: () -> Unit,
    viewModel: AddEditAddressViewModel = hiltViewModel(),
) {

    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val shortNameError = viewModel.shortNameError.collectAsStateWithLifecycle().value

    val enableBtn = nameError == null && shortNameError == null

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

    LaunchedEffect(key1 = addressId) {
        viewModel.getAddressById(addressId)
    }

    val testTag = if (addressId == 0) CREATE_ADDRESS_SCREEN else UPDATE_ADDRESS_SCREEN

    Column(
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        StandardTextField(
            value = viewModel.state.addressName,
            label = ADDRESS_FULL_NAME_FIELD,
            leadingIcon = Icons.Default.Business,
            isError = nameError != null,
            errorText = nameError,
            errorTextTag = ADDRESS_FULL_NAME_ERROR,
            onValueChange = {
                viewModel.onEvent(AddEditAddressEvent.AddressNameChanged(it))
            }
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        StandardTextField(
            value = viewModel.state.shortName,
            label = ADDRESS_SHORT_NAME_FIELD,
            leadingIcon = Icons.Default.CurrencyRupee,
            isError = shortNameError != null,
            errorText = shortNameError,
            errorTextTag = ADDRESS_SHORT_NAME_ERROR,
            onValueChange = {
                viewModel.onEvent(AddEditAddressEvent.ShortNameChanged(it))
            }
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        StandardButton(
            text = if (addressId == 0) CREATE_NEW_ADDRESS else EDIT_ADDRESS,
            enabled = enableBtn,
            onClick = {
                viewModel.onEvent(AddEditAddressEvent.CreateOrUpdateAddress(addressId))
            }
        )
    }
}