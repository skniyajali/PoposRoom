package com.niyaj.address.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.data.utils.AddressTestTags.ADDRESS_FULL_NAME_ERROR
import com.niyaj.data.utils.AddressTestTags.ADDRESS_FULL_NAME_FIELD
import com.niyaj.data.utils.AddressTestTags.ADDRESS_SHORT_NAME_ERROR
import com.niyaj.data.utils.AddressTestTags.ADDRESS_SHORT_NAME_FIELD
import com.niyaj.data.utils.AddressTestTags.ADD_EDIT_ADDRESS_BTN
import com.niyaj.data.utils.AddressTestTags.CREATE_ADDRESS_SCREEN
import com.niyaj.data.utils.AddressTestTags.CREATE_NEW_ADDRESS
import com.niyaj.data.utils.AddressTestTags.EDIT_ADDRESS
import com.niyaj.data.utils.AddressTestTags.UPDATE_ADDRESS_SCREEN
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
fun AddEditAddressScreen(
    addressId: Int = 0,
    navController: NavController,
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

    StandardScaffoldWithOutDrawer(
        title = title,
        onBackClick = {
            navController.navigateUp()
        },
        showBottomBar = enableBtn,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .testTag(ADD_EDIT_ADDRESS_BTN)
                    .padding(horizontal = SpaceSmallMax),
                text = if (addressId == 0) CREATE_NEW_ADDRESS else EDIT_ADDRESS,
                enabled = enableBtn,
                icon = if (addressId == 0) Icons.Default.Add else Icons.Default.EditLocationAlt,
                onClick = {
                    viewModel.onEvent(AddEditAddressEvent.CreateOrUpdateAddress(addressId))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .testTag(title)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
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
        }
    }
}